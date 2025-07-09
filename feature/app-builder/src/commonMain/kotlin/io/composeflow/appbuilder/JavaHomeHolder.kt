package io.composeflow.appbuilder

import io.composeflow.BuildConfig
import io.composeflow.CurrentOs
import io.composeflow.currentOs
import io.composeflow.logger.logger
import java.io.File

object JavaHomeHolder {
    private const val LINUX_DEFAULT_JAVA_HOME = "/opt/composeflow/lib/runtime"

    val javaHome: String? =
        if (BuildConfig.isRelease) {
            when (currentOs) {
                CurrentOs.Windows -> {
                    val userDir = System.getProperty("user.dir")
                    val expected = userDir?.let { File(it).resolve("runtime") }
                    logger.debug("Win expected JavaHome: {}", expected)
                    if (expected?.resolve("bin")?.exists() == true) {
                        logger.debug("expected found at {}", expected)
                        expected.toString()
                    } else {
                        getJavaHomeFromEnv()
                    }
                }

                CurrentOs.Linux -> {
                    if (File(LINUX_DEFAULT_JAVA_HOME).resolve("bin").resolve("java").exists()) {
                        LINUX_DEFAULT_JAVA_HOME
                    } else {
                        getJavaHomeFromEnv()
                    }
                }

                CurrentOs.Mac -> {
                    // There isn't reliable way to get the path to the Java runtime for the
                    // installed runtime. We try to guess it from the DYLD_LIBRARY_PATH
                    System.getenv("DYLD_LIBRARY_PATH").let {
                        val libPath = it.replace(":", "")
                        "$libPath/../runtime/Contents/Home"
                    }
                }

                CurrentOs.Other -> getJavaHomeFromEnv()
            }
        } else {
            logger.debug("isRelease: false. Using javaHome from env variable. ${getJavaHomeFromEnv()}")
            getJavaHomeFromEnv()
        }

    private fun getJavaHomeFromEnv(): String? = System.getenv("JAVA_HOME") ?: null
}
