package io.composeflow.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.composeflow.auth.LocalFirebaseIdToken
import io.composeflow.model.project.Project
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.modifier.backgroundContainerNeutral
import io.composeflow.ui.settings.firebase.FirebaseApiAppResultState
import io.composeflow.ui.settings.firebase.FirebaseSettingsContent
import io.composeflow.ui.settings.preference.PreferencesContent
import moe.tlaster.precompose.viewmodel.viewModel


@Composable
fun SettingsScreen(
    project: Project,
    modifier: Modifier = Modifier,
    initialDestination: SettingsScreenDestination? = null,
) {
    val firebaseIdToken = LocalFirebaseIdToken.current
    val viewModel = viewModel(modelClass = SettingsViewModel::class) {
        SettingsViewModel(
            project = project,
            firebaseIdTokenArg = firebaseIdToken,
        )
    }
    val darkThemeSettingUiState = viewModel.darkThemeSettingsUiState.collectAsState()
    val firebaseApiResultState by viewModel.firebaseApiResultState.collectAsState()
    val firebaseApiAppResultState = FirebaseApiAppResultState(
        androidApp = viewModel.firebaseAndroidAppApiResultState.collectAsState().value,
        iOSApp = viewModel.firebaseIosAppApiResultState.collectAsState().value,
        webApp = viewModel.firebaseWebAppApiResultState.collectAsState().value
    )

    var selectedDestination by remember {
        mutableStateOf(
            initialDestination ?: SettingsScreenDestination.Preferences
        )
    }
    val settingsCallbacks = SettingsCallbacks(
        onConnectFirebaseProjectId = viewModel::onConnectFirebaseProjectId,
        onLoginScreenChanged = viewModel::onLoginScreenChanged,
    )
    Surface(modifier = modifier.fillMaxSize()) {
        Row {
            SettingsScreenContentNavigation(
                currentDestination = selectedDestination,
                onDestinationChanged = {
                    selectedDestination = it
                },
                modifier = Modifier.width(196.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .backgroundContainerNeutral()
                    .padding(16.dp),
            ) {
                Spacer(Modifier.width(96.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(vertical = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(color = MaterialTheme.colorScheme.surface),
                ) {

                    when (selectedDestination) {
                        SettingsScreenDestination.Preferences -> {
                            PreferencesContent(
                                project = project,
                                darkThemeSettingsUiState = darkThemeSettingUiState.value.darkThemeSettingSetterUiState,
                                settingsCallbacks = settingsCallbacks,
                            )
                        }

                        SettingsScreenDestination.Firebase -> {
                            FirebaseSettingsContent(
                                project = project,
                                firebaseApiResultState = firebaseApiResultState,
                                firebaseApiAppResultState = firebaseApiAppResultState,
                                settingsCallbacks = settingsCallbacks
                            )
                        }
                    }
                }
                Spacer(Modifier.width(96.dp))
            }
        }
    }
}

@Composable
fun SettingsScreenContentNavigation(
    currentDestination: SettingsScreenDestination,
    onDestinationChanged: (SettingsScreenDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    PermanentNavigationDrawer(
        drawerContent = {
            PermanentDrawerSheet(
                modifier = Modifier.width(180.dp),
            ) {
                Spacer(Modifier.height(16.dp))
                SettingsScreenDestination.entries.forEachIndexed { i, destination ->
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
                        selected = currentDestination.ordinal == i,
                        onClick = {
                            onDestinationChanged(destination)
                        },
                        modifier = Modifier
                            .heightIn(max = 40.dp)
                            .padding(horizontal = 12.dp),
                    )
                }
            }
        },
        modifier = modifier,
        content = {},
    )
}

