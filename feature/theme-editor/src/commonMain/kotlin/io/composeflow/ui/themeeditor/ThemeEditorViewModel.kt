package io.composeflow.ui.themeeditor

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.graphics.Color
import com.materialkolor.PaletteStyle
import io.composeflow.auth.FirebaseIdToken
import io.composeflow.font.FontFamilyWrapper
import io.composeflow.model.color.ColorSchemeWrapper
import io.composeflow.model.enumwrapper.TextStyleWrapper
import io.composeflow.model.project.LoadedProjectUiState
import io.composeflow.model.project.Project
import io.composeflow.model.project.asLoadedProjectUiState
import io.composeflow.model.project.asProjectStateFlow
import io.composeflow.model.project.theme.TextStyleOverride
import io.composeflow.model.project.theme.TextStyleOverrides
import io.composeflow.model.useroperation.OperationHistory
import io.composeflow.model.useroperation.UserOperation
import io.composeflow.repository.ProjectRepository
import io.composeflow.ui.common.defaultDarkScheme
import io.composeflow.ui.common.defaultLightScheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class ThemeEditorViewModel(
    firebaseIdToken: FirebaseIdToken,
    projectId: String,
    private val projectRepository: ProjectRepository = ProjectRepository(firebaseIdToken),
) : ViewModel() {
    private val _projectUiState: MutableStateFlow<LoadedProjectUiState> =
        MutableStateFlow(LoadedProjectUiState.Success(Project()))
    val projectUiState: StateFlow<LoadedProjectUiState> = _projectUiState

    var project by mutableStateOf(_projectUiState.asProjectStateFlow(viewModelScope).value)
        private set

    // Primary font family edited in the ThemeEditorScreen. This isn't reflected to the persisted project
    var primaryFontFamily by mutableStateOf(
        project.themeHolder.fontHolder.primaryFontFamily,
        policy = structuralEqualityPolicy()
    )
        private set

    // Secondary font family edited in the ThemeEditorScreen. This isn't reflected to the persisted project
    var secondaryFontFamily by mutableStateOf(
        project.themeHolder.fontHolder.secondaryFontFamily,
        policy = structuralEqualityPolicy()
    )
        private set

    // Text overrides edited in the ThemeEditorScreen. This isn't reflected to the persisted project
    val textStyleOverrides: TextStyleOverrides = mutableStateMapOf()

    init {
        viewModelScope.launch {
            _projectUiState.value = LoadedProjectUiState.Loading
            _projectUiState.value =
                projectRepository.loadProject(projectId).asLoadedProjectUiState(projectId)
            when (val state = _projectUiState.value) {
                is LoadedProjectUiState.Success -> {
                    project = state.project

                    primaryFontFamily = project.themeHolder.fontHolder.primaryFontFamily
                    secondaryFontFamily = project.themeHolder.fontHolder.secondaryFontFamily
                    textStyleOverrides.putAll(project.themeHolder.fontHolder.textStyleOverrides)
                }

                else -> {}
            }
        }
    }

    fun onColorSchemeUpdated(
        sourceColor: Color,
        paletteStyle: PaletteStyle,
        lightScheme: ColorScheme,
        darkScheme: ColorScheme,
    ) {
        val lightSchemeWrapper = ColorSchemeWrapper.fromColorScheme(lightScheme)
        val darkSchemeWrapper = ColorSchemeWrapper.fromColorScheme(darkScheme)
        recordOperation(
            project,
            UserOperation.UpdateColorSchemes(
                sourceColor = sourceColor,
                paletteStyle = paletteStyle,
                lightScheme = lightSchemeWrapper,
                darkScheme = darkSchemeWrapper,
            ),
        )

        project.themeHolder.colorSchemeHolder.apply {
            this.sourceColor = sourceColor
            this.paletteStyle = paletteStyle
            this.lightColorScheme.value = lightSchemeWrapper
            this.darkColorScheme.value = darkSchemeWrapper
        }

        saveProject()
    }

    fun onColorResetToDefault() {
        recordOperation(project, UserOperation.ResetColorSchemes)

        project.themeHolder.colorSchemeHolder.apply {
            sourceColor = null
            paletteStyle = PaletteStyle.TonalSpot
            lightColorScheme.value = ColorSchemeWrapper.fromColorScheme(defaultLightScheme)
            darkColorScheme.value = ColorSchemeWrapper.fromColorScheme(defaultDarkScheme)
        }

        saveProject()
    }

    fun onPrimaryFontFamilyChanged(fontFamily: FontFamilyWrapper) {
        primaryFontFamily = fontFamily
    }

    fun onSecondaryFontFamilyChanged(fontFamily: FontFamilyWrapper) {
        secondaryFontFamily = fontFamily
    }

    fun onTextStyleOverrideChanged(
        textStyleWrapper: TextStyleWrapper,
        textStyleOverride: TextStyleOverride,
    ) {
        textStyleOverrides[textStyleWrapper] = textStyleOverride
    }

    fun onApplyFontEditableParams() {
        project.themeHolder.fontHolder.primaryFontFamily = primaryFontFamily
        project.themeHolder.fontHolder.secondaryFontFamily = secondaryFontFamily
        project.themeHolder.fontHolder.textStyleOverrides.clear()
        project.themeHolder.fontHolder.textStyleOverrides.putAll(textStyleOverrides)

        saveProject()
    }

    fun onResetFonts() {
        project.themeHolder.fontHolder.resetToDefaults()
        primaryFontFamily = project.themeHolder.fontHolder.primaryFontFamily
        secondaryFontFamily = project.themeHolder.fontHolder.secondaryFontFamily
        textStyleOverrides.clear()
        textStyleOverrides.putAll(project.themeHolder.fontHolder.textStyleOverrides)

        saveProject()
    }

    private fun saveProject() {
        viewModelScope.launch {
            projectRepository.updateProject(project)
        }
    }

    private fun recordOperation(
        project: Project,
        userOperation: UserOperation,
    ) {
        viewModelScope.launch {
            OperationHistory.record(
                project = project,
                userOperation = userOperation,
            )
        }
    }
}
