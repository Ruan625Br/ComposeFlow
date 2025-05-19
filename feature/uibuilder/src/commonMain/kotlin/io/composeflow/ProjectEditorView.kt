package io.composeflow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import io.composeflow.auth.LocalFirebaseIdToken
import io.composeflow.model.ProvideNavigator
import io.composeflow.model.TopLevelDestination
import io.composeflow.ui.Tooltip
import io.composeflow.ui.jewel.TitleBarContent
import io.composeflow.ui.statusbar.StatusBar
import io.composeflow.ui.statusbar.StatusBarViewModel
import io.composeflow.ui.toolbar.Toolbar
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.PopUpTo
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.viewmodel.viewModel
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.SelectableIconButton
import org.jetbrains.jewel.ui.component.styling.LocalIconButtonStyle
import org.jetbrains.jewel.ui.painter.hints.Size
import org.jetbrains.jewel.ui.painter.hints.Stroke
import org.jetbrains.jewel.ui.painter.rememberResourcePainterProvider

const val MainViewTestTag = "MainView"

@Composable
fun ProjectEditorView(
    projectId: String,
    onTitleBarContentSet: (TitleBarContent) -> Unit = {},
) {
    Column(modifier = Modifier.testTag(MainViewTestTag)) {
        ProjectEditorContent(
            onTitleBarContentSet = onTitleBarContentSet,
            projectId = projectId,
        )
    }
}

const val NavigationRailTestTag = "NavigationRail"

class MainViewUiState(
    val appDarkTheme: Boolean,
)

@Composable
fun ProjectEditorContent(
    projectId: String,
    onTitleBarContentSet: (TitleBarContent) -> Unit,
) {
    val projectEditorNavigator = rememberNavigator()
    val currentDestination = TopLevelDestination.entries.firstOrNull {
        it.route == projectEditorNavigator.currentEntry.collectAsState(null).value?.route?.route
    }
    Surface {
        val firebaseIdToken = LocalFirebaseIdToken.current
        val statusBarViewModel =
            viewModel(modelClass = StatusBarViewModel::class) {
                StatusBarViewModel()
            }
        val statusBarUiState by statusBarViewModel.uiState.collectAsState()
        ProvideNavigator(navigator = projectEditorNavigator) {
            Column {
                onTitleBarContentSet {
                    Toolbar(
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
                            currentDestination?.ordinal ?: 0
                        )
                    }
                    Column(
                        modifier = Modifier.width(40.dp)
                            .fillMaxHeight()
                            .background(color = MaterialTheme.colorScheme.surface),
                    ) {
                        TopLevelDestination.entries.forEachIndexed { index, item ->
                            Tooltip(item.label) {
                                SelectableIconButton(
                                    selected = selectedItem == item.ordinal,
                                    onClick = {
                                        projectEditorNavigator.navigate(
                                            item.route, options = NavOptions(
                                                popUpTo = PopUpTo(
                                                    route = item.route
                                                )
                                            )
                                        )
                                        selectedItem = index
                                    },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(5.dp)
                                        .testTag("$NavigationRailTestTag/${item.name}"),
                                ) { state ->
                                    val tint by LocalIconButtonStyle.current.colors.foregroundFor(
                                        state,
                                    )
                                    val painterProvider = rememberResourcePainterProvider(
                                        item.iconPath,
                                        Icons::class.java,
                                    )
                                    val painter by painterProvider.getPainter(
                                        Size(20),
                                        Stroke(tint),
                                    )
                                    Icon(painter = painter, "icon")
                                }
                            }
                        }
                    }
                    VerticalDivider(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp),
                    )
                    ProjectEditorNavHost(
                        navigator = projectEditorNavigator,
                        projectId = projectId,
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                StatusBar(uiState = statusBarUiState)
            }
        }
    }
}
