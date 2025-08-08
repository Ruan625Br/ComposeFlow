package io.composeflow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import io.composeflow.ai.AiChatDialog
import io.composeflow.auth.LocalFirebaseIdToken
import io.composeflow.auth.isAiEnabled
import io.composeflow.model.EMPTY_ROUTE
import io.composeflow.model.ProvideNavigator
import io.composeflow.model.ToolWindowTopLevelDestination
import io.composeflow.model.TopLevelDestination
import io.composeflow.ui.Tooltip
import io.composeflow.ui.jewel.TitleBarContent
import io.composeflow.ui.statusbar.StatusBar
import io.composeflow.ui.statusbar.StatusBarViewModel
import io.composeflow.ui.toolbar.LeftToolbar
import io.composeflow.ui.toolbar.RightToolbar
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.PopUpTo
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.viewmodel.viewModel
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.SelectableIconButton
import org.jetbrains.jewel.ui.component.VerticalSplitLayout
import org.jetbrains.jewel.ui.component.styling.LocalIconButtonStyle
import org.jetbrains.jewel.ui.painter.hints.Stroke
import org.jetbrains.jewel.ui.painter.rememberResourcePainterProvider

const val MAIN_VIEW_TEST_TAG = "MainView"

@Composable
fun ProjectEditorView(
    projectId: String,
    onTitleBarRightContentSet: (TitleBarContent) -> Unit,
    onTitleBarLeftContentSet: (TitleBarContent) -> Unit,
) {
    Column(modifier = Modifier.testTag(MAIN_VIEW_TEST_TAG)) {
        ProjectEditorContent(
            onTitleBarRightContentSet = onTitleBarRightContentSet,
            onTitleBarLeftContentSet = onTitleBarLeftContentSet,
            projectId = projectId,
        )
    }
}

const val NAVIGATION_RAIL_TEST_TAG = "NavigationRail"

class MainViewUiState(
    val appDarkTheme: Boolean,
)

@Composable
fun ProjectEditorContent(
    projectId: String,
    onTitleBarRightContentSet: (TitleBarContent) -> Unit,
    onTitleBarLeftContentSet: (TitleBarContent) -> Unit,
) {
    val firebaseIdToken = LocalFirebaseIdToken.current
    val isAiEnabled = isAiEnabled()

    val viewModel =
        viewModel(modelClass = ProjectEditorViewModel::class) {
            ProjectEditorViewModel(firebaseIdToken = firebaseIdToken, projectId = projectId)
        }
    val project = viewModel.project.collectAsState().value
    val aiAssistantUiState by viewModel.aiAssistantUiState.collectAsState()

    val projectEditorNavigator = rememberNavigator()
    val toolWindowNavigator = rememberNavigator("toolWindow")

    var hasToolWindowOpen by remember { mutableStateOf(true) }

    val currentDestination =
        TopLevelDestination.entries.firstOrNull {
            it.route ==
                projectEditorNavigator.currentEntry
                    .collectAsState(null)
                    .value
                    ?.route
                    ?.route
        }

    val currentDestinationToolWindow =
        ToolWindowTopLevelDestination.entries.firstOrNull {
            it.route ==
                toolWindowNavigator.currentEntry
                    .collectAsState(null)
                    .value
                    ?.route
                    ?.route
        }

    val showAiChatDialog = viewModel.showAiChatDialog.collectAsState().value
    val aiChatToggleVisibilityModifier =
        if (isAiEnabled) {
            Modifier.onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown && event.key == Key.K && (event.isMetaPressed || event.isCtrlPressed)) {
                    viewModel.onToggleShowAiChatDialog()
                    true
                } else {
                    false
                }
            }
        } else {
            Modifier
        }
    Surface(
        modifier = aiChatToggleVisibilityModifier,
    ) {
        val statusBarViewModel =
            viewModel(modelClass = StatusBarViewModel::class) {
                StatusBarViewModel()
            }
        val statusBarUiState by statusBarViewModel.uiState.collectAsState()
        BoxWithConstraints {
            val maxHeight = maxHeight.value
            val screenMaxSize = Size(maxWidth.value, maxHeight)
            ProvideNavigator(navigator = projectEditorNavigator) {
                Column {
                    onTitleBarLeftContentSet {
                        if (isAiEnabled) {
                            LeftToolbar(
                                onToggleVisibilityOfAiChatDialog = {
                                    viewModel.onToggleShowAiChatDialog()
                                },
                            )
                        }
                    }
                    onTitleBarRightContentSet {
                        RightToolbar(
                            firebaseIdToken = firebaseIdToken,
                            projectFileName = projectId,
                            onStatusBarUiStateChanged = statusBarViewModel::onStatusBarUiStateChanged,
                            statusBarUiState = statusBarUiState,
                            navigator = projectEditorNavigator,
                        )
                    }

                    Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        var selectedItem by remember(currentDestination) {
                            mutableStateOf(
                                currentDestination?.ordinal ?: 0,
                            )
                        }

                        var selectedItemToolWindow by remember(currentDestinationToolWindow) {
                            mutableStateOf(
                                currentDestinationToolWindow?.ordinal ?: 0,
                            )
                        }
                        Box(
                            modifier =
                                Modifier
                                    .width(40.dp)
                                    .fillMaxHeight()
                                    .background(color = MaterialTheme.colorScheme.surface),
                        ) {
                            Column {
                                TopLevelDestination.entries.forEachIndexed { index, item ->
                                    @Suppress("KotlinConstantConditions")
                                    if (BuildConfig.isRelease) {
                                        if (item == TopLevelDestination.StringEditor) {
                                            return@forEachIndexed
                                        }
                                    }
                                    Tooltip(item.label) {
                                        SelectableIconButton(
                                            selected = selectedItem == item.ordinal,
                                            onClick = {
                                                projectEditorNavigator.navigate(
                                                    item.route,
                                                    options =
                                                        NavOptions(
                                                            popUpTo =
                                                                PopUpTo(
                                                                    route = item.route,
                                                                ),
                                                        ),
                                                )
                                                selectedItem = index
                                            },
                                            modifier =
                                                Modifier
                                                    .size(40.dp)
                                                    .padding(5.dp)
                                                    .testTag("$NAVIGATION_RAIL_TEST_TAG/${item.name}"),
                                        ) { state ->
                                            val tint by LocalIconButtonStyle.current.colors.foregroundFor(
                                                state,
                                            )
                                            val painterProvider =
                                                rememberResourcePainterProvider(
                                                    item.iconPath,
                                                    Icons::class.java,
                                                )
                                            val painter by painterProvider.getPainter(
                                                org.jetbrains.jewel.ui.painter.hints
                                                    .Size(20),
                                                Stroke(tint),
                                            )
                                            Icon(painter = painter, "icon")
                                        }
                                    }
                                }
                            }

                            Column(
                                modifier = Modifier.align(Alignment.BottomCenter),
                            ) {
                                ToolWindowTopLevelDestination.entries.forEachIndexed { index, item ->
                                    Tooltip(item.label) {
                                        val selected = selectedItemToolWindow == item.ordinal
                                        val route = item.route
                                        val routeIndex = item.ordinal
                                        Logger.d("route: $route")
                                        Logger.d("routeIndex: $routeIndex")
                                        Logger.d("selected: $selected")
                                        Logger.d("selectedItemToolWindow: $selectedItemToolWindow")
                                        Logger.d("index: $index")
                                        Logger.d("ordinal: ${item.ordinal}")
                                        Logger.d("name: ${item.name}")
                                        Logger.d("hasToolWindowOpen: $hasToolWindowOpen")

                                        SelectableIconButton(
                                            selected = selected && hasToolWindowOpen,
                                            onClick = {
                                                hasToolWindowOpen = !(selected && hasToolWindowOpen)

                                                toolWindowNavigator.navigate(
                                                    route,
                                                    options =
                                                        NavOptions(
                                                            popUpTo =
                                                                PopUpTo(
                                                                    route = route,
                                                                ),
                                                        ),
                                                )
                                                selectedItemToolWindow = routeIndex
                                            },
                                            modifier =
                                                Modifier
                                                    .size(40.dp)
                                                    .padding(5.dp)
                                                    .testTag("$NAVIGATION_RAIL_TEST_TAG/${item.name}"),
                                        ) { state ->
                                            val tint by LocalIconButtonStyle.current.colors.foregroundFor(
                                                state,
                                            )
                                            val painterProvider =
                                                rememberResourcePainterProvider(
                                                    item.iconPath,
                                                    Icons::class.java,
                                                )
                                            val painter by painterProvider.getPainter(
                                                org.jetbrains.jewel.ui.painter.hints
                                                    .Size(20),
                                                Stroke(tint),
                                            )
                                            Icon(painter = painter, "icon")
                                        }
                                    }
                                }
                            }
                        }
                        VerticalDivider(
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            modifier =
                                Modifier
                                    .fillMaxHeight()
                                    .width(1.dp),
                        )

                        VerticalSplitLayout(
                            initialDividerPosition = if (hasToolWindowOpen) (maxHeight - (maxHeight * 0.2f)).dp else maxHeight.dp,
                            minRatio = 0.25f,
                            maxRatio = if (hasToolWindowOpen) 0.94f else 1f,
                            first = { firstModifier ->
                                Box(modifier = firstModifier) {
                                    ProjectEditorNavHost(
                                        navigator = projectEditorNavigator,
                                        project = project,
                                        aiAssistantUiState = aiAssistantUiState,
                                        onUpdateProject = viewModel::onUpdateProject,
                                        onToggleVisibilityOfAiChatDialog = viewModel::onToggleShowAiChatDialog,
                                        screenMaxSize = screenMaxSize,
                                    )
                                }
                            },
                            second = { secondModifier ->
                                Box(
                                    modifier =
                                        secondModifier
                                            .height(0.dp),
                                ) {
                                    if (hasToolWindowOpen) {
                                        ProjectToolWindowNavHost(
                                            navigator = toolWindowNavigator,
                                            project = project,
                                        )
                                    }
                                }
                            },
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                    StatusBar(uiState = statusBarUiState)
                }
            }
        }
    }
    if (showAiChatDialog && isAiEnabled) {
        AiChatDialog(
            project = project,
            aiAssistantUiState = aiAssistantUiState,
            firebaseIdToken = firebaseIdToken,
            onDismissDialog = { viewModel.onToggleShowAiChatDialog() },
            onAiAssistantUiStateUpdated = viewModel::onUpdateAiAssistantState,
            modifier = aiChatToggleVisibilityModifier,
        )
    }
}
