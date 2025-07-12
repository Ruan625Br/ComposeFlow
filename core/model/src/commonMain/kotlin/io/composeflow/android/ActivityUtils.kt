package io.composeflow.android

fun generateMainActivityKt(
    packageName: String,
    isSplashScreenEnabled: Boolean,
): String {
    val splashScreenImport =
        if (isSplashScreenEnabled) {
            "import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen\n"
        } else {
            ""
        }
    val installSplashScreen =
        if (isSplashScreenEnabled) {
            "installSplashScreen()\n"
        } else {
            ""
        }

    return """
package $packageName

import MainView
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
$splashScreenImport
import androidx.activity.enableEdgeToEdge
import moe.tlaster.precompose.PreComposeApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        $installSplashScreen
        enableEdgeToEdge()
        setContent {
            PreComposeApp {
                MainView()
            }
        }
    }
}
    """
}
