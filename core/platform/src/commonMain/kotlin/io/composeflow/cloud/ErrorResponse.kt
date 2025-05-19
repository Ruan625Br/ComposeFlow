package io.composeflow.cloud

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val error: ErrorDetail,
)

@Serializable
data class ErrorDetail(
    val code: Int,
    val message: String,
    val errors: List<ErrorItem>,
    val status: String,
    val details: List<ErrorDetailItem> = emptyList(),
)

@Serializable
data class ErrorItem(
    val message: String,
    val domain: String,
    val reason: String,
)

@Serializable
data class ErrorDetailItem(
    @SerialName("@type")
    val type: String,
    val reason: String,
    val domain: String,
    val metadata: ErrorMetadata,
)

@Serializable
data class ErrorMetadata(
    val service: String,
    val method: String,
)
