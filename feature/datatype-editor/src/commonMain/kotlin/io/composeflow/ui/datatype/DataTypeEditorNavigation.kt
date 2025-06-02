package io.composeflow.ui.datatype

import io.composeflow.model.project.Project
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.transition.NavTransition

const val dataTypeEditorRoute = "datatype_editor_route"

fun RouteBuilder.dataTypeEditorScreen(
    project: Project,
    navTransition: NavTransition? = null,
) {
    scene(
        route = dataTypeEditorRoute,
        navTransition = navTransition,
    ) {
        DataTypeEditor(project = project)
    }
}
