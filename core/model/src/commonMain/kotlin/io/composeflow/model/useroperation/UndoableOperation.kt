package io.composeflow.model.useroperation

import java.time.LocalDateTime

data class UndoableOperation(
    val serializedProject: String,
    val userOperation: UserOperation,
    val date: LocalDateTime = LocalDateTime.now(),
)
