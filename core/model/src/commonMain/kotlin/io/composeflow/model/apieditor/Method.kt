package io.composeflow.model.apieditor

enum class Method {
    Get,
    Post,
    Delete,
    Put,
    Patch,
    ;

    companion object {
        fun fromOrdinal(ordinal: Int): Method = entries.first { it.ordinal == ordinal }
    }
}
