package io.composeflow
import com.hashicorp.cdktf.TerraformIterator
import com.hashicorp.cdktf.TerraformStack
import com.hashicorp.cdktf.Token
import com.hashicorp.cdktf.providers.google.project_service.ProjectService
import com.hashicorp.cdktf.providers.google.provider.GoogleProvider
import com.hashicorp.cdktf.providers.google_beta.google_firestore_database.GoogleFirestoreDatabase
import com.hashicorp.cdktf.providers.google_beta.provider.GoogleBetaProvider
import software.constructs.Construct

data class FirestoreStackConfig(
    val projectId: String
)

class FirestoreStack (
    scope: Construct,
    id: String,
    props: FirestoreStackConfig
): TerraformStack(
    scope,
    id
) {
    init {
        useBackend(props.projectId)

        GoogleProvider.Builder.create(this, "Google").apply {
            project(props.projectId)
            billingProject(props.projectId)
            userProjectOverride(true)
            region("us-central1")
        }.build()

        val provider = GoogleBetaProvider.Builder.create(this, "GoogleBeta").apply {
            project(props.projectId)
            billingProject(props.projectId)
            userProjectOverride(true)
            region("us-central1")
        }.build()

        val apisIterator = TerraformIterator.fromList(
            listOf(
                "firestore.googleapis.com"
            )
        )
        val apis = ProjectService.Builder.create(this, "EnableApi").apply {
            forEach(apisIterator)
            service(Token.asString(apisIterator.value))
        }.build()

        GoogleFirestoreDatabase.Builder.create(this, "Firestore").apply {
            provider(provider)
            project(props.projectId)
            name("composeflow")
            locationId("us-central1")
            type("FIRESTORE_NATIVE")
        }.dependsOn(listOf(
            apis
        )).build()
    }
}