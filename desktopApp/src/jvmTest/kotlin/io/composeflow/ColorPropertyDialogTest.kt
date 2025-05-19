package io.composeflow

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import io.composeflow.robots.ColorPropertyDialogRobot
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Ignore
import kotlin.test.Test

// lateinit property scene has not been initialized
// kotlin.UninitializedPropertyAccessException: lateinit property scene has not been initialized
@Ignore
@OptIn(ExperimentalTestApi::class)
@RunWith(TestParameterInjector::class)
class ColorPropertyDialogTest {
    @get:Rule
    val screenTestRule = ScreenTestRule()

    @Test
    fun checkLaunchShot() {
        runDesktopComposeUiTest(
            width = 1920,
            height = 1080,
        ) {
            ColorPropertyDialogRobot().apply {
                setContent()

                capture()
            }
        }
    }

    @Test
    fun checkDarkModeLaunchShot() {
        runDesktopComposeUiTest(
            width = 1920,
            height = 1080,
        ) {
            ColorPropertyDialogRobot().apply {
                setContent(isDarkTheme = true)

                capture()
            }
        }
    }
}
