package io.composeflow.kotlinpoet

import com.squareup.kotlinpoet.CodeBlock

/**
 * Indicates the class that implements this interface is able to express itself as [CodeBlock].
 */
interface CodeConvertible {
    fun asCodeBlock(): CodeBlock
}
