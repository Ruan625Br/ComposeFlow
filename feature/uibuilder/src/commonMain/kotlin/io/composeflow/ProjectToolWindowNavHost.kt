package io.composeflow

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.composeflow.model.EMPTY_ROUTE
import io.composeflow.model.TOOL_WINDOW_CONSOLE_ROUTE
import io.composeflow.model.project.Project
import io.composeflow.ui.bottomPainel.console.consolePanel
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun ProjectToolWindowNavHost(
    navigator: Navigator,
    project: Project,
    modifier: Modifier = Modifier,
) {
    NavHost(
        modifier = modifier,
        navigator = navigator,
        initialRoute = TOOL_WINDOW_CONSOLE_ROUTE,
    ) {
        consolePanel()

        scene(EMPTY_ROUTE) { }
    }
}
