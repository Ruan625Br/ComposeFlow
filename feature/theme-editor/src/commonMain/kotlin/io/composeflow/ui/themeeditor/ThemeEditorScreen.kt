package io.composeflow.ui.themeeditor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.composeflow.auth.LocalFirebaseIdToken
import io.composeflow.model.project.LoadedProjectUiState
import io.composeflow.model.project.Project
import io.composeflow.ui.icon.ComposeFlowIcon
import moe.tlaster.precompose.viewmodel.viewModel

enum class ThemeEditorNavigationDestination(
    val icon: ImageVector,
    val destinationName: String,
) {
    ColorEditor(
        icon = Icons.Outlined.Palette,
        destinationName = "Colors"
    ),
    FontEditor(
        icon = Icons.Outlined.TextFields,
        destinationName = "Fonts"
    ),
}

@Composable
fun ThemeEditorScreen(
    projectId: String,
    modifier: Modifier = Modifier,
) {
    val firebaseIdToken = LocalFirebaseIdToken.current
    val viewModel = viewModel(modelClass = ThemeEditorViewModel::class) {
        ThemeEditorViewModel(
            firebaseIdToken = firebaseIdToken, projectId = projectId
        )
    }
    val callbacks = ThemeEditorCallbacks(
        onColorSchemeUpdated = viewModel::onColorSchemeUpdated,
        onColorResetToDefault = viewModel::onColorResetToDefault,
        onPrimaryFontFamilyChanged = viewModel::onPrimaryFontFamilyChanged,
        onSecondaryFontFamilyChanged = viewModel::onSecondaryFontFamilyChanged,
        onTextStyleOverridesChanged = viewModel::onTextStyleOverrideChanged,
        onApplyFontEditableParams = viewModel::onApplyFontEditableParams,
        onResetFonts = viewModel::onResetFonts,
    )
    val fontEditableParams = FontEditableParams(
        primaryFontFamily = viewModel.primaryFontFamily,
        secondaryFontFamily = viewModel.secondaryFontFamily,
        textStyleOverrides = viewModel.textStyleOverrides,
    )
    val projectUiState by viewModel.projectUiState.collectAsState()
    when (val state = projectUiState) {
        is LoadedProjectUiState.Error -> {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Failed to load project",
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }

        LoadedProjectUiState.Loading -> {
            CircularProgressIndicator()
        }

        LoadedProjectUiState.NotFound -> {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Failed to load project",
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }

        is LoadedProjectUiState.Success -> {
            ThemeEditorContent(
                project = state.project,
                callbacks = callbacks,
                fontEditableParams = fontEditableParams,
                modifier = modifier
            )
        }
    }
}

@Composable
fun ThemeEditorContent(
    project: Project,
    callbacks: ThemeEditorCallbacks,
    fontEditableParams: FontEditableParams,
    modifier: Modifier = Modifier,
) {
    var selectedDestination by remember { mutableStateOf(ThemeEditorNavigationDestination.ColorEditor) }
    PermanentNavigationDrawer(
        drawerContent = {
            PermanentDrawerSheet(
                modifier = Modifier.width(180.dp),
            ) {
                Spacer(Modifier.height(16.dp))
                ThemeEditorNavigationDestination.entries.forEachIndexed { i, destination ->
                    NavigationDrawerItem(
                        icon = {
                            ComposeFlowIcon(
                                imageVector = destination.icon,
                                contentDescription = null,
                            )
                        },
                        label = {
                            Text(
                                destination.destinationName,
                                style = MaterialTheme.typography.titleSmall,
                            )
                        },
                        selected = selectedDestination.ordinal == i,
                        onClick = {
                            selectedDestination = destination
                        },
                        modifier = Modifier
                            .heightIn(max = 40.dp)
                            .padding(horizontal = 12.dp),
                    )
                }
            }
        },
        modifier = modifier,
        content = {
            Scaffold {
                when (selectedDestination) {
                    ThemeEditorNavigationDestination.ColorEditor -> {
                        ColorEditorContent(
                            project = project,
                            callbacks = callbacks,
                        )
                    }

                    ThemeEditorNavigationDestination.FontEditor -> {
                        FontEditorContent(
                            project = project,
                            callbacks = callbacks,
                            fontEditableParams = fontEditableParams,
                        )
                    }
                }
            }
        },
    )
}
