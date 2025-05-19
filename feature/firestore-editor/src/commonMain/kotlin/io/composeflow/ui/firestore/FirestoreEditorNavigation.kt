package io.composeflow.ui.firestore

import io.composeflow.model.firestoreEditorRoute
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.transition.NavTransition

fun RouteBuilder.firestoreEditorScreen(
    projectId: String,
    navTransition: NavTransition? = null,
) {
    scene(
        route = firestoreEditorRoute,
        navTransition = navTransition,
    ) {
        FirestoreEditorScreen(projectId = projectId)
    }
}
