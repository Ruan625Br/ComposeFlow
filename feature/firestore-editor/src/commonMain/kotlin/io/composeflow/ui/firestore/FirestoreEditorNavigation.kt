package io.composeflow.ui.firestore

import io.composeflow.model.FIRESTORE_EDITOR_ROUTE
import io.composeflow.model.project.Project
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.transition.NavTransition

fun RouteBuilder.firestoreEditorScreen(
    project: Project,
    navTransition: NavTransition? = null,
) {
    scene(
        route = FIRESTORE_EDITOR_ROUTE,
        navTransition = navTransition,
    ) {
        FirestoreEditorScreen(project = project)
    }
}
