package io.composeflow.ui.project

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.composeflow.Res
import io.composeflow.cancel
import io.composeflow.confirm
import io.composeflow.delete
import io.composeflow.delete_project_confirmation
import io.composeflow.delete_the_project
import io.composeflow.editor.validator.KotlinClassNameValidator
import io.composeflow.editor.validator.KotlinPackageNameValidator
import io.composeflow.editor.validator.ValidateResult
import io.composeflow.failed_to_load_project
import io.composeflow.model.project.LoadedProjectUiState
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.Screen
import io.composeflow.new_project
import io.composeflow.project_load_failed
import io.composeflow.ui.LocalOnAllDialogsClosed
import io.composeflow.ui.LocalOnAnyDialogIsShown
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.icon.ComposeFlowIconButton
import io.composeflow.ui.modifier.moveFocusOnTab
import io.composeflow.ui.popup.PositionCustomizablePopup
import io.composeflow.ui.switchByHovered
import io.composeflow.ui.textfield.SmallOutlinedTextField
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProjectScreen(
    onCreateProject: (projectName: String, packageName: String) -> Unit,
    onCreateProjectWithScreens: (project: Project, screens: List<Screen>) -> Unit,
    onDeleteProject: (String) -> Unit,
    onProjectSelected: (Project) -> Unit,
    projectUiStateList: List<LoadedProjectUiState>,
    modifier: Modifier = Modifier,
) {
    ProjectListBody(
        onCreateProject = onCreateProject,
        onCreateProjectWithScreens = onCreateProjectWithScreens,
        onDeleteProject = onDeleteProject,
        onProjectSelected = onProjectSelected,
        projectUiStateList = projectUiStateList,
        modifier = modifier,
    )
}

@Composable
private fun ProjectListBody(
    onCreateProject: (projectName: String, packageName: String) -> Unit,
    onCreateProjectWithScreens: (project: Project, screens: List<Screen>) -> Unit,
    onDeleteProject: (String) -> Unit,
    onProjectSelected: (Project) -> Unit,
    projectUiStateList: List<LoadedProjectUiState>,
    modifier: Modifier = Modifier,
) {
    var createProjectDialogOpen by remember { mutableStateOf(false) }
    var deleteProjectDialogOpen by remember { mutableStateOf(false) }
    var projectIdToBeDeleted by remember { mutableStateOf<String?>(null) }
    val onAnyDialogIsShown = LocalOnAnyDialogIsShown.current
    val onAllDialogsClosed = LocalOnAllDialogsClosed.current

    Row(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                TextButton(
                    onClick = {
                        createProjectDialogOpen = true
                        onAnyDialogIsShown()
                    },
                    modifier =
                        Modifier.padding(
                            start = 16.dp,
                            bottom = 8.dp,
                        ),
                ) {
                    Text("+ ${stringResource(Res.string.new_project)}")
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Adaptive(280.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp),
            ) {
                items(items = projectUiStateList) { projectUiState ->
                    Column(
                        modifier =
                            Modifier
                                .size(
                                    width = 280.dp,
                                    height = 420.dp,
                                ).clickable {
                                    when (projectUiState) {
                                        is LoadedProjectUiState.Success -> {
                                            onProjectSelected(projectUiState.project)
                                        }

                                        else -> {}
                                    }
                                },
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            when (projectUiState) {
                                is LoadedProjectUiState.Error -> {
                                    ProjectThumbnailErrorContent(
                                        projectId = projectUiState.yamlFileName,
                                        onDeleteProjectClicked = {
                                            projectIdToBeDeleted = it
                                            deleteProjectDialogOpen = true
                                            onAnyDialogIsShown()
                                        },
                                    )
                                }

                                LoadedProjectUiState.Loading -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.align(Alignment.Center),
                                    )
                                }

                                is LoadedProjectUiState.Success -> {
                                    ProjectThumbnail(
                                        project = projectUiState.project,
                                        onProjectSelected = onProjectSelected,
                                        onDeleteProjectClicked = {
                                            projectIdToBeDeleted = it
                                            deleteProjectDialogOpen = true
                                            onAnyDialogIsShown()
                                        },
                                    )
                                }

                                LoadedProjectUiState.NotFound -> {}
                            }
                        }
                    }
                }
            }
        }

        if (createProjectDialogOpen) {
            NewProjectDialog(
                onDismissDialog = {
                    createProjectDialogOpen = false
                    onAllDialogsClosed()
                },
                onConfirmProject = { projectName, packageName ->
                    createProjectDialogOpen = false
                    onCreateProject(projectName, packageName)
                    onAllDialogsClosed()
                },
                onConfirmProjectWithScreens = { project, screens ->
                    createProjectDialogOpen = false
                    onCreateProjectWithScreens(project, screens)
                    onAllDialogsClosed()
                },
            )
        }
        if (deleteProjectDialogOpen) {
            DeleteProjectDialog(
                onCloseClick = {
                    deleteProjectDialogOpen = false
                    onAllDialogsClosed()
                },
                onProjectDeleted = {
                    onDeleteProject(it)
                    deleteProjectDialogOpen = false
                    onAllDialogsClosed()
                },
                projectId = projectIdToBeDeleted,
            )
        }
    }
}

@Composable
fun AddNewProjectDialog(
    onDismissDialog: () -> Unit,
    onConfirmProject: (projectName: String, packageName: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var projectName by remember { mutableStateOf("") }
    var packageName by remember { mutableStateOf("") }
    var projectNameValidateResult by remember {
        mutableStateOf(KotlinClassNameValidator().validate(projectName))
    }
    var packageNameValidateResult by remember {
        mutableStateOf(KotlinPackageNameValidator().validate(packageName))
    }
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
        val confirmDialog = {
            onConfirmProject(
                projectName,
                packageName.ifEmpty {
                    "com.example"
                },
            )
        }
        val (first, second, third, fourth) = remember { FocusRequester.createRefs() }
        LaunchedEffect(Unit) {
            first.requestFocus()
        }
        Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
            Column(
                modifier =
                    Modifier
                        .size(width = 280.dp, height = 320.dp)
                        .padding(16.dp),
            ) {
                val isFormValid by remember {
                    derivedStateOf {
                        projectNameValidateResult is ValidateResult.Success &&
                            packageNameValidateResult is ValidateResult.Success
                    }
                }

                SmallOutlinedTextField(
                    value = projectName,
                    onValueChange = {
                        projectName = it
                        projectNameValidateResult = KotlinClassNameValidator().validate(it)
                    },
                    label = {
                        Text(
                            "Project name",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.alpha(0.8f),
                        )
                    },
                    supportingText = {
                        when (val result = projectNameValidateResult) {
                            is ValidateResult.Failure -> {
                                Text(result.message)
                            }

                            ValidateResult.Success -> {}
                        }
                    },
                    singleLine = true,
                    isError = projectNameValidateResult !is ValidateResult.Success,
                    modifier =
                        Modifier
                            .focusRequester(first)
                            .moveFocusOnTab()
                            .onKeyEvent {
                                if (it.key == Key.Enter && isFormValid) {
                                    confirmDialog()
                                    true
                                } else {
                                    false
                                }
                            }.fillMaxWidth()
                            .padding(bottom = 8.dp),
                )

                SmallOutlinedTextField(
                    value = packageName,
                    onValueChange = {
                        packageName = it
                        packageNameValidateResult = KotlinPackageNameValidator().validate(it)
                    },
                    label = {
                        Text(
                            "Package name",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.alpha(0.8f),
                        )
                    },
                    placeholder = {
                        Text(
                            "com.example",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.alpha(0.6f),
                        )
                    },
                    supportingText = {
                        when (val result = packageNameValidateResult) {
                            is ValidateResult.Failure -> {
                                Text(result.message)
                            }

                            ValidateResult.Success -> {}
                        }
                    },
                    singleLine = true,
                    isError = packageNameValidateResult !is ValidateResult.Success,
                    modifier =
                        Modifier
                            .focusRequester(second)
                            .moveFocusOnTab()
                            .onKeyEvent {
                                if (it.key == Key.Enter && isFormValid) {
                                    confirmDialog()
                                    true
                                } else {
                                    false
                                }
                            }.fillMaxWidth(),
                )

                Row(
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = {
                            onDismissDialog()
                        },
                        modifier = Modifier.padding(end = 16.dp).focusRequester(third),
                    ) {
                        Text(stringResource(Res.string.cancel))
                    }
                    OutlinedButton(
                        onClick = {
                            confirmDialog()
                        },
                        enabled = isFormValid,
                        modifier = Modifier.focusRequester(fourth),
                    ) {
                        Text(stringResource(Res.string.confirm))
                    }
                }
            }
        }
    }
}

@Composable
private fun ProjectThumbnailErrorContent(
    projectId: String,
    onDeleteProjectClicked: (String) -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .switchByHovered(
                    hovered =
                        Modifier.border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(16.dp),
                        ),
                    notHovered =
                        Modifier.alpha(0.5f).border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(16.dp),
                        ),
                ),
    ) {
        Row(
            modifier = Modifier.zIndex(1f).padding(top = 16.dp, end = 16.dp),
        ) {
            Spacer(modifier = Modifier.weight(1f))
            ComposeFlowIconButton(
                onClick = {
                    onDeleteProjectClicked(projectId)
                },
            ) {
                ComposeFlowIcon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(Res.string.delete_the_project),
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
        Column(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 64.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = stringResource(Res.string.project_load_failed),
                tint = MaterialTheme.colorScheme.error,
            )
            Text(text = stringResource(Res.string.failed_to_load_project))
        }
    }
}

@Composable
private fun ProjectThumbnail(
    project: Project,
    onProjectSelected: (Project) -> Unit,
    onDeleteProjectClicked: (String) -> Unit,
) {
    Box(modifier = Modifier.clip(RoundedCornerShape(16.dp))) {
        val defaultScreen = project.screenHolder.currentScreen()
        defaultScreen.thumbnail(
            project = project,
            thumbnailName = project.name,
            modifier =
                Modifier.pointerInput(Unit) {
                    awaitPointerEventScope {
                        // To consume the click event in this Composable.
                        // (Not let the children handle click event in the thumbnail)
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.changes.any { it.pressed }) {
                                onProjectSelected(project)
                                event.changes.forEach { it.consume() }
                            }
                        }
                    }
                },
        )

        Row(
            modifier = Modifier.zIndex(1f).padding(top = 16.dp, end = 16.dp),
        ) {
            Spacer(modifier = Modifier.weight(1f))
            ComposeFlowIconButton(
                onClick = {
                    onDeleteProjectClicked(project.id.toString())
                },
            ) {
                ComposeFlowIcon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(Res.string.delete_the_project),
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun DeleteProjectDialog(
    projectId: String?,
    onCloseClick: () -> Unit,
    onProjectDeleted: (String) -> Unit,
) {
    if (projectId == null) return

    PositionCustomizablePopup(
        onDismissRequest = {
            onCloseClick()
        },
        onKeyEvent = {
            if (it.type == KeyEventType.KeyDown && it.key == Key.Escape) {
                onCloseClick()
                true
            } else {
                false
            }
        },
    ) {
        Surface {
            Column(
                modifier = Modifier.size(width = 300.dp, height = 160.dp).padding(16.dp),
            ) {
                Text(
                    text = stringResource(Res.string.delete_project_confirmation),
                )

                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    TextButton(
                        onClick = {
                            onCloseClick()
                        },
                        modifier = Modifier.padding(end = 16.dp),
                    ) {
                        Text(stringResource(Res.string.cancel))
                    }
                    OutlinedButton(
                        onClick = {
                            onProjectDeleted(projectId)
                        },
                        modifier = Modifier.padding(end = 16.dp),
                    ) {
                        Text(
                            text = stringResource(Res.string.delete),
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        }
    }
}
