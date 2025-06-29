package io.composeflow.model.settings

import kotlinx.serialization.Serializable

data class ComposeBuilderSettings(
    val appDarkThemeSetting: DarkThemeSetting = DarkThemeSetting.Light,
    val composeBuilderDarkThemeSetting: DarkThemeSetting = DarkThemeSetting.Dark,
    val javaHome: PathSetting? = null,
    val showBordersInCanvas: Boolean = true,
    /**
     * The version to which the user is asked to update the ComposeFlow app.
     * Used to not show the same update prompt
     */
    val versionAskedToUpdate: String? = null,
)

enum class DarkThemeSetting {
    System, // Fall back to system setting by using isSystemInDarkTheme
    Light,
    Dark,
    ;

    companion object {
        fun fromOrdinal(ordinal: Int): DarkThemeSetting = entries.first { it.ordinal == ordinal }
    }
}

@Serializable
sealed interface PathSetting {
    fun path(): String

    @Serializable
    data class FromEnvVar(
        val envVarName: String,
        private val value: String,
    ) : PathSetting {
        override fun path(): String = value
    }

    @Serializable
    data class FromLocal(
        private val value: String,
    ) : PathSetting {
        override fun path(): String = value
    }
}
