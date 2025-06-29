package io.composeflow.appbuilder.wrapper

import co.touchlab.kermit.Logger
import io.composeflow.appbuilder.wrapper.CommandUtil.runCommand
import io.composeflow.appbuilder.wrapper.CommandUtil.runCommandAndWait
import io.composeflow.removeLineBreak

class AndroidEmulatorWrapper {
    private val sdkPath =
        System.getenv("ANDROID_SDK_ROOT") ?: System.getenv("ANDROID_HOME") ?: null
    private val emulatorPath =
        sdkPath?.let {
            "$it/emulator/emulator"
        }

    suspend fun listAvdsAndWait(): List<String> =
        emulatorPath?.let {
            val command = arrayOf(emulatorPath, "-list-avds")
            val output = runCommandAndWait(command)
            output
                .split("\n")
                .filterNot { it.startsWith("INFO") }
                .filter { it.isNotEmpty() }
                .map { it.removeLineBreak() }
        } ?: emptyList()

    fun runAvd(
        avdName: String,
        portNumber: Int,
    ) {
        emulatorPath?.let {
            val command = arrayOf(emulatorPath, "-port", "$portNumber", "-avd", avdName)
            runCommand(command)
        } ?: Logger.w("No emulator command is found")
    }
}
