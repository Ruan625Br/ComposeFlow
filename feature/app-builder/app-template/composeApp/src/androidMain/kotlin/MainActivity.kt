package packageName

import MainView
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.createDefaultAndroid
import moe.tlaster.precompose.PreComposeApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        updateStatusBarIconsForTheme()
        setContent {
            PreComposeApp {
                CompositionLocalProvider(
                    LocalImageLoader provides ImageLoader.createDefaultAndroid(applicationContext)
                ) {
                    MainView()
                }
            }
        }
    }

    /**
     * Updates the status bar icons color based on the current theme (dark or light)
     */
    private fun updateStatusBarIconsForTheme() {
        // Check if we're in dark mode
        val isDarkTheme = resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window?.let { win ->
                win.decorView.post {
                    if (isDarkTheme) {
                        win.insetsController?.setSystemBarsAppearance(
                            0, // Clear the appearance flags
                            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                        )
                    } else {
                        win.insetsController?.setSystemBarsAppearance(
                            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                        )
                    }
                }
            }
        } else {
            @Suppress("DEPRECATION")
            window?.decorView?.let { decorView ->
                decorView.post {
                    if (isDarkTheme) {
                        decorView.systemUiVisibility =
                            decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                    } else {
                        decorView.systemUiVisibility =
                            decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    }
                }
            }
        }
    }

    /**
     * Handle configuration changes like dark/light mode toggle
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Update status bar icons when configuration changes (e.g., dark/light mode toggle)
        updateStatusBarIconsForTheme()
    }
}
