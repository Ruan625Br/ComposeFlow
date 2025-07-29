package io.composeflow

enum class CurrentOs {
    Windows,
    Linux,
    Mac,
    Other,
}

val currentOs: CurrentOs by lazy {
    val osName = System.getProperty("os.name").lowercase()
    if (osName.contains("win")) {
        CurrentOs.Windows
    } else if (osName.contains("nix") ||
        osName.contains("nux") ||
        osName.contains("aix")
    ) {
        CurrentOs.Linux
    } else if (osName.contains("mac")) {
        CurrentOs.Mac
    } else {
        CurrentOs.Other
    }
}
