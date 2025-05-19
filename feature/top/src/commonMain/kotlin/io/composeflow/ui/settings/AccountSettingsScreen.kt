package io.composeflow.ui.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.composeflow.Res
import io.composeflow.advanced_settings
import io.composeflow.java_home_description
import io.composeflow.model.settings.DarkThemeSettingSetter
import io.composeflow.model.settings.PathSetting
import io.composeflow.settings
import io.composeflow.ui.Tooltip
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.textfield.SmallOutlinedTextField
import io.composeflow.ui.utils.TreeExpander
import moe.tlaster.precompose.viewmodel.viewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun AccountSettingsScreen(
    modifier: Modifier = Modifier,
) {
    val viewModel = viewModel(modelClass = AccountSettingsViewModel::class) {
        AccountSettingsViewModel(
        )
    }
    val javaHomePath = viewModel.javaHomePath.collectAsState().value
    val darkThemeSettingsUiState = viewModel.darkThemeSettingsUiState.collectAsState().value

    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.settings),
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        DarkThemeSettingSetter(
            darkThemeSettingSetterUiState = darkThemeSettingsUiState.darkThemeSettingSetterUiState,
        )

        Spacer(Modifier.size(24.dp))

        var advancedSettingsOpen by remember { mutableStateOf(false) }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(end = 8.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    advancedSettingsOpen = !advancedSettingsOpen
                }
        ) {
            TreeExpander(
                expanded = advancedSettingsOpen,
                onClick = {
                    advancedSettingsOpen = !advancedSettingsOpen
                }
            )
            Text(
                stringResource(Res.string.advanced_settings),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
        }

        if (advancedSettingsOpen) {
            Column(modifier = Modifier.animateContentSize(keyframes { durationMillis = 200 })) {
                AdvancedSettingsContent(
                    javaHomePath = javaHomePath,
                )
            }
        }
    }
}

@Composable
private fun AdvancedSettingsContent(
    javaHomePath: PathSetting?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Spacer(Modifier.size(16.dp))
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.padding(
                bottom = 8.dp
            )
        ) {
            Text(
                "Java Home",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(end = 8.dp)
            )
            val javaHomeDesc = stringResource(Res.string.java_home_description)
            Tooltip(javaHomeDesc) {
                ComposeFlowIcon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = javaHomeDesc,
                    tint = MaterialTheme.colorScheme.secondary,
                )
            }
            when (javaHomePath) {
                is PathSetting.FromEnvVar -> {
                    Text(
                        "(Set from Environment Variable)",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                else -> {}
            }
        }

        SmallOutlinedTextField(
            value = javaHomePath?.path() ?: "",
            onValueChange = {
            },
            modifier = Modifier.width(880.dp),
            maxLines = 1,
            readOnly = true,
            enabled = true,
        )
    }
}