package io.composeflow.platform

import java.io.File

internal enum class OperatingSystem {
    Windows,
    Linux,
    MacOS,
    Unknown,
}

internal val currentOperatingSystem: OperatingSystem
    get() {
        val os = System.getProperty("os.name").lowercase()
        return when {
            os.contains("win") -> OperatingSystem.Windows
            os.contains("nix") || os.contains("nux") || os.contains("aix") -> {
                OperatingSystem.Linux
            }

            os.contains("mac") -> OperatingSystem.MacOS
            else -> OperatingSystem.Unknown
        }
    }

actual fun getCacheDir(): File =
    when (currentOperatingSystem) {
        OperatingSystem.Windows -> File(System.getenv("AppData"), "compose_flow/cache")
        OperatingSystem.Linux -> File(System.getProperty("user.home"), ".cache/compose_flow")
        OperatingSystem.MacOS -> File(System.getProperty("user.home"), "Library/Caches/compose_flow")
        else -> throw IllegalStateException("Unsupported operating system")
    }
