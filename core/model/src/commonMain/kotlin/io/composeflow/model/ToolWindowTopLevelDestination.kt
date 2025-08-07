package io.composeflow.model

const val TOOL_WINDOW_CONSOLE_ROUTE = "console_route"
const val EMPTY_ROUTE = "empty_route"

enum class ToolWindowTopLevelDestination(
    val iconPath: String,
    val label: String,
    val route: String,
) {
    Console(
        "icons/terminal.svg",
        "Console",
        TOOL_WINDOW_CONSOLE_ROUTE,
    ),
}
