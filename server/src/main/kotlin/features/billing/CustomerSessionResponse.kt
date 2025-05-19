package io.composeflow.features.billing

import kotlinx.serialization.Serializable

@Serializable
data class CustomerSessionResponse(
    val uid: String,
    val pricingTableUrl: String
)
