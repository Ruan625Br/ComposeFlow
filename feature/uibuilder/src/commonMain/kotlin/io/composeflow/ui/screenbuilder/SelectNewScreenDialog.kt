package io.composeflow.ui.screenbuilder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import io.composeflow.Res
import io.composeflow.add_new_screen
import io.composeflow.ai.AiAssistantDialog
import io.composeflow.ai.AiAssistantDialogCallbacks
import io.composeflow.ai_add_screen_with_ai
import io.composeflow.cancel
import io.composeflow.confirm
import io.composeflow.custom.ComposeFlowIcons
import io.composeflow.custom.composeflowicons.NounAi
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.Screen
import io.composeflow.screen_name
import io.composeflow.template.ScreenTemplatePair
import io.composeflow.template.ScreenTemplates
import io.composeflow.ui.modifier.moveFocusOnTab
import io.composeflow.ui.popup.PositionCustomizablePopup
import org.jetbrains.compose.resources.stringResource

@Composable
fun SelectNewScreenDialog(
    project: Project,
    onCloseClick: () -> Unit,
    onScreenTemplateSelected: (ScreenTemplatePair) -> Unit,
    onAddScreen: ((Screen) -> Unit)? = null,
) {
    var showAiAssistantDialog by remember { mutableStateOf(false) }
    PositionCustomizablePopup(
        onDismissRequest = {
            onCloseClick()
        },
        onKeyEvent = {
            if (it.key == Key.Escape) {
                onCloseClick()
                true
            } else {
                false
            }
        },
    ) {
        Surface(modifier = Modifier.size(1100.dp, 1000.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(Res.string.add_new_screen),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(12.dp),
                    )
                    Spacer(Modifier.width(16.dp))
                    if (onAddScreen != null) {
                        TextButton(
                            onClick = {
                                showAiAssistantDialog = true
                            },
                            modifier = Modifier.padding(end = 16.dp),
                        ) {
                            Icon(
                                imageVector = ComposeFlowIcons.NounAi,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp).padding(end = 4.dp),
                            )
                            Text(stringResource(Res.string.ai_add_screen_with_ai))
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(280.dp),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    items(items = ScreenTemplates.initialTemplates()) {
                        Column(
                            modifier =
                                Modifier
                                    .pointerInput(Unit) {
                                        awaitPointerEventScope {
                                            // To consume the click event in this Composable.
                                            // (Not let the children handle click event in the thumbnail)
                                            while (true) {
                                                val event = awaitPointerEvent()
                                                if (event.changes.any { it.pressed }) {
                                                    onScreenTemplateSelected(it)
                                                    event.changes.forEach { it.consume() }
                                                }
                                            }
                                        }
                                    },
                        ) {
                            it.screen.thumbnail(
                                project,
                                includeUpdateTime = false,
                            )
                        }
                    }
                }
                HorizontalDivider()
                Row(
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = {
                            onCloseClick()
                        },
                        modifier =
                            Modifier
                                .padding(end = 16.dp),
                    ) {
                        Text(stringResource(Res.string.cancel))
                    }
                }
            }
        }
    }

    if (showAiAssistantDialog && onAddScreen != null) {
        AiAssistantDialog(
            project = project,
            callbacks =
                AiAssistantDialogCallbacks(
                    onAddNewScreen = onAddScreen,
                ),
            onCloseClick = {
                showAiAssistantDialog = false
            },
            // This is only used from the initial project creation
            onConfirmProjectWithScreens = { _, _ -> },
        )
    }
}

@Composable
fun ScreenNameDialog(
    initialName: String,
    onNameConfirmed: (String) -> Unit,
    onCloseClick: () -> Unit,
) {
    var screenName by remember { mutableStateOf(initialName) }
    PositionCustomizablePopup(
        onDismissRequest = {
            onCloseClick()
        },
        onKeyEvent = {
            if (it.key == Key.Escape) {
                onCloseClick()
                true
            } else {
                false
            }
        },
    ) {
        val first = remember { FocusRequester() }
        val second = remember { FocusRequester() }
        val third = remember { FocusRequester() }
        LaunchedEffect(Unit) {
            first.requestFocus()
        }
        Surface(modifier = Modifier.size(240.dp, 160.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = screenName,
                    onValueChange = {
                        screenName = it
                    },
                    label = { Text(stringResource(Res.string.screen_name)) },
                    singleLine = true,
                    isError = screenName.isEmpty(),
                    modifier =
                        Modifier
                            .focusRequester(first)
                            .moveFocusOnTab()
                            .onKeyEvent {
                                if (it.key == Key.Enter && screenName.isNotEmpty()) {
                                    onNameConfirmed(screenName)
                                    true
                                } else {
                                    false
                                }
                            },
                )

                Row(
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    TextButton(
                        onClick = {
                            onCloseClick()
                        },
                        modifier =
                            Modifier
                                .padding(end = 16.dp)
                                .focusRequester(second),
                    ) {
                        Text(stringResource(Res.string.cancel))
                    }
                    OutlinedButton(
                        onClick = {
                            onNameConfirmed(screenName)
                        },
                        modifier =
                            Modifier
                                .padding(end = 16.dp)
                                .focusRequester(third),
                    ) {
                        Text(stringResource(Res.string.confirm))
                    }
                }
            }
        }
    }
}
