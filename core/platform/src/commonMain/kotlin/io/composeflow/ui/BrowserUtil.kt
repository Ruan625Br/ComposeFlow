package io.composeflow.ui

import java.awt.Desktop
import java.net.URI
import java.util.Locale

fun openInBrowser(uri: URI) {
    val osName by lazy(LazyThreadSafetyMode.NONE) {
        System.getProperty("os.name").lowercase(Locale.getDefault())
    }
    val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null

    when {
        desktop?.isSupported(Desktop.Action.BROWSE) == true -> try {
            desktop.browse(uri)
        } catch (e: Exception) {
            fallbackOpen(uri, osName)
        }

        "mac" in osName -> Runtime.getRuntime().exec(arrayOf("open", uri.toString()))
        "nix" in osName || "nux" in osName -> Runtime.getRuntime().exec(arrayOf("xdg-open", uri.toString()))
        else -> throw RuntimeException("Cannot open $uri")
    }
}

private fun fallbackOpen(uri: URI, osName: String) {
    try {
        when {
            "mac" in osName -> Runtime.getRuntime().exec(arrayOf("open", uri.toString()))
            "nix" in osName || "nux" in osName -> Runtime.getRuntime().exec(arrayOf("xdg-open", uri.toString()))
            else -> throw RuntimeException("Fallback failed for $uri")
        }
    } catch (e: Exception) {
        throw RuntimeException("Failed to open browser for $uri", e)
    }
}