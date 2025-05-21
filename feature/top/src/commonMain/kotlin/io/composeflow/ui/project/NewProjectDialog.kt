package io.composeflow.ui.project

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.unit.dp
import io.composeflow.Res
import io.composeflow.ai.AiAssistantDialog
import io.composeflow.ai_create_project_alternative_manually
import io.composeflow.ai_create_project_placeholder
import io.composeflow.ai_title_prompt_dialog
import io.composeflow.cancel
import io.composeflow.confirm
import io.composeflow.editor.validator.NonEmptyStringValidator
import io.composeflow.editor.validator.ValidateResult
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.Screen
import io.composeflow.ui.modifier.moveFocusOnTab
import io.composeflow.ui.popup.PositionCustomizablePopup
import org.jetbrains.compose.resources.stringResource

@Composable
fun NewProjectDialog(
    onDismissDialog: () -> Unit,
    onConfirmProject: (projectName: String, packageName: String) -> Unit,
    onConfirmProjectWithScreens: (projectName: String, packageName: String, screens: List<Screen>) -> Unit,
    modifier: Modifier = Modifier,
) {
    PositionCustomizablePopup(
        onDismissRequest = {
            onDismissDialog()
        },
        onKeyEvent = {
            if (it.key == Key.Escape) {
                onDismissDialog()
                true
            } else {
                false
            }
        },
        modifier = modifier,
    ) {
        Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
            Column(
                modifier = Modifier.padding(16.dp)
                    .size(700.dp, 480.dp)
            ) {
                AiAssistedCreationInputs(
                    onConfirmProject = onConfirmProject,
                    onConfirmProjectWithScreens = onConfirmProjectWithScreens,
                    onDismissDialog = onDismissDialog
                )
            }
        }
    }
}

@Composable
private fun AiAssistedCreationInputs(
    onConfirmProject: (projectName: String, packageName: String) -> Unit,
    onConfirmProjectWithScreens: (projectName: String, packageName: String, screens: List<Screen>) -> Unit,
    onDismissDialog: () -> Unit,
) {
    var userQuery by remember { mutableStateOf("") }
    var openAiAssistantDialog by remember { mutableStateOf(false) }
    var openProjectDialogManually by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        var queryValidator by remember {
            mutableStateOf(NonEmptyStringValidator().validate(userQuery))
        }
        val isFormValid by remember {
            derivedStateOf {
                queryValidator is ValidateResult.Success
            }
        }
        val (first, second, third) = remember { FocusRequester.createRefs() }
        LaunchedEffect(Unit) {
            first.requestFocus()
        }
        Text(
            text = stringResource(Res.string.ai_title_prompt_dialog),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.size(32.dp))
        OutlinedTextField(
            value = userQuery,
            onValueChange = {
                userQuery = it
                queryValidator = NonEmptyStringValidator().validate(it)
            },
            placeholder = {
                Text(
                    stringResource(Res.string.ai_create_project_placeholder),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                )
            },
            singleLine = false,
            minLines = 7,
            modifier = Modifier.fillMaxWidth()
                .weight(1f)
                .focusRequester(first).moveFocusOnTab()
        )
        Spacer(Modifier.size(16.dp))
        TextButton(
            onClick = {
                openProjectDialogManually = true
            },
            modifier = Modifier.align(Alignment.End).focusRequester(third)
        ) {
            Text(stringResource(Res.string.ai_create_project_alternative_manually))
        }
        Spacer(Modifier.size(16.dp))
        Row(
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = {
                    onDismissDialog()
                },
                modifier = Modifier.padding(end = 16.dp).focusRequester(second),
            ) {
                Text(stringResource(Res.string.cancel))
            }
            OutlinedButton(
                onClick = {
                    openAiAssistantDialog = true
                },
                enabled = isFormValid,
                modifier = Modifier.focusRequester(third),
            ) {
                Text(stringResource(Res.string.confirm))
            }
        }
    }

    if (openAiAssistantDialog) {
        AiAssistantDialog(
            // Project is required to render the thumbnail. This is a temporary solution to create a
            // fake project as here is the place before creating a project
            project = Project(),
            onCloseClick = {
                openAiAssistantDialog = false
            },
            onConfirmProjectWithScreens = onConfirmProjectWithScreens,
            projectCreationPrompt = userQuery
        )
    }
    if (openProjectDialogManually) {
        val closeDialog = {
            openProjectDialogManually = false
        }
        AddNewProjectDialog(
            onDismissDialog = closeDialog,
            onConfirmProject = onConfirmProject,
        )
    }
}
