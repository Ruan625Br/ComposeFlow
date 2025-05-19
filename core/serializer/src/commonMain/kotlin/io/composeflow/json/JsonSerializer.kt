package io.composeflow.json

import kotlinx.serialization.json.Json

val jsonSerializer: Json = Json { ignoreUnknownKeys = true }