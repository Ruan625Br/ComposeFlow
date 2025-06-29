import androidx.compose.ui.window.ComposeUIViewController
import io.composeflow.AppInitializer
import moe.tlaster.precompose.PreComposeApp

fun MainViewController() =
    ComposeUIViewController {
        AppInitializer.onApplicationStart()
        PreComposeApp {
            App()
        }
    }
