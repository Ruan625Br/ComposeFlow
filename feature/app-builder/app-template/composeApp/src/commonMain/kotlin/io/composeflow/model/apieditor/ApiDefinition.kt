package io.composeflow.model.apieditor

data class ApiDefinition(
    val id: String = "",
    val name: String = "",
    val method: Method = Method.Get,
    val url: String = "",
    val headers: List<Pair<String, String>> = emptyList(),
    val queryParameters: List<Pair<String, String>> = emptyList(),
    val exampleJsonResponse: JsonWithJsonPath? = null,
)
