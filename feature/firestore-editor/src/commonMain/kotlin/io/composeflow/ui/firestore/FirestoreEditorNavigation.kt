package io.composeflow.ui.firestore

import io.composeflow.model.firestoreEditorRoute
import io.composeflow.model.project.Project
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.transition.NavTransition

fun RouteBuilder.firestoreEditorScreen(
    project: Project,
    navTransition: NavTransition? = null,
) {
    scene(
        route = firestoreEditorRoute,
        navTransition = navTransition,
    ) {
        FirestoreEditorScreen(project = project)
    }
}
