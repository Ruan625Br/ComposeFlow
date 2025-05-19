package io.composeflow.ui.datatype

import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.transition.NavTransition

const val dataTypeEditorRoute = "datatype_editor_route"

fun RouteBuilder.dataTypeEditorScreen(
    projectId: String,
    navTransition: NavTransition? = null,
) {
    scene(
        route = dataTypeEditorRoute,
        navTransition = navTransition,
    ) {
        DataTypeEditor(projectId = projectId)
    }
}
