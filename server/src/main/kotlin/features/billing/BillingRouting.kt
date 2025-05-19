package io.composeflow.features.billing

import com.stripe.Stripe
import com.stripe.model.Customer
import com.stripe.model.CustomerSession
import com.stripe.param.CustomerCreateParams
import com.stripe.param.CustomerListParams
import com.stripe.param.CustomerSessionCreateParams
import io.composeflow.getDecodedToken
import io.composeflow.responseInternalServerError
import io.composeflow.responseUnauthorized
import io.ktor.http.Url
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route


fun Route.billingRouting() {
    Stripe.apiKey = System.getenv("STRIPE_API_KEY")

    route("/billing") {
        post("/createPricingTableLink") {
            val firebaseUser = call.getDecodedToken()
            if (firebaseUser == null) {
                call.responseUnauthorized()
                return@post
            }

            // Create or Get a Stripe customer
            val params =
                CustomerListParams.builder().setEmail(firebaseUser.email).setLimit(1L).build()
            val customers = Customer.list(params)
            val customer: Customer? = if (customers.data.isNotEmpty()) {
                customers.data.first()
            } else {
                val createParams = CustomerCreateParams.builder()
                    .setEmail(firebaseUser.email)
                    .setDescription("Firebase UID: ${firebaseUser.uid}")
                    .setName(firebaseUser.name)
                    .setMetadata(
                        mapOf(
                            "firebase_uid" to firebaseUser.uid
                        )
                    )
                    .build()
                Customer.create(createParams)
            }

            if (customer?.id == null) {
                call.responseInternalServerError()
                return@post
            }

            // CustomerSession expires after 30 minutes. Purchases will fail if it expires.
            val customerSession = CustomerSession.create(
                CustomerSessionCreateParams.builder()
                    .setCustomer(customer.id)
                    .setComponents(
                        CustomerSessionCreateParams.Components.builder()
                            .setPricingTable(
                                CustomerSessionCreateParams.Components.PricingTable.builder()
                                    .setEnabled(true)
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )

            val pricingTableUrl =
                Url("https://www.composeflow.io/billing?session_id=${customerSession.clientSecret}")

            call.respond(CustomerSessionResponse(firebaseUser.uid, pricingTableUrl.toString()))
        }
    }
}