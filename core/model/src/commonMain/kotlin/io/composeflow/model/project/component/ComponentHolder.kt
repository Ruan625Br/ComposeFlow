package io.composeflow.model.project.component

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.MemberName
import io.composeflow.formatter.suppressRedundantVisibilityModifier
import io.composeflow.model.project.COMPOSEFLOW_PACKAGE
import io.composeflow.model.project.Project
import io.composeflow.override.mutableStateListEqualsOverrideOf
import io.composeflow.serializer.FallbackMutableStateListSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("ComponentHolder")
data class ComponentHolder(
    @Serializable(with = FallbackMutableStateListSerializer::class)
    val components: MutableList<Component> = mutableStateListEqualsOverrideOf(),
) {
    fun generateKoinViewModelModule(project: Project): FileSpec {
        val fileBuilder = FileSpec.builder(
            fileName = "ViewModelModule",
            packageName = "${COMPOSEFLOW_PACKAGE}.components"
        )
        val funSpecBuilder = FunSpec.builder("componentViewModelModule")
            .returns(org.koin.core.module.Module::class)
            .addCode("return %M {", MemberName("org.koin.dsl", "module"))
        components.forEach {
            funSpecBuilder.addStatement(
                "factory { %T() } ",
                ClassName(it.getPackageName(project), it.viewModelFileName)
            )
        }
        funSpecBuilder.addCode("}")
        fileBuilder.addFunction(funSpecBuilder.build())
        fileBuilder.suppressRedundantVisibilityModifier()
        return fileBuilder.build()
    }
}
