package io.composeflow

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import io.composeflow.model.parameter.TextTrait
import io.composeflow.robots.MainViewRobot
import io.composeflow.robots.UiBuilderRobot
import io.composeflow.ui.uibuilder.CanvasTopToolbarDarkModeSwitchTestTag
import io.composeflow.ui.uibuilder.CanvasTopToolbarZoomInTestTag
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Ignore
import kotlin.test.Test

// lateinit property scene has not been initialized
// kotlin.UninitializedPropertyAccessException: lateinit property scene has not been initialized
@Ignore
@OptIn(ExperimentalTestApi::class)
@RunWith(TestParameterInjector::class)
class CanvasTopToolbarTest {
    @get:Rule
    val screenTestRule = ScreenTestRule()

    @Test
    fun checkDarkThemeSwitchShot() {
        runDesktopComposeUiTest(
            width = 1920,
            height = 1080,
        ) {
            MainViewRobot().apply {
                setContent()

                UiBuilderRobot(screenTestRule).apply {
                    dragToCanvas(TextTrait())
                    clickDarkThemeSwitch()
                }

                captureNode(CanvasTopToolbarDarkModeSwitchTestTag)
            }
        }
    }

    @Test
    fun checkZoomInShot() {
        runDesktopComposeUiTest(
            width = 1920,
            height = 1080,
        ) {
            MainViewRobot().apply {
                setContent()

                UiBuilderRobot(screenTestRule).apply {
                    clickZoomIn()
                }

                captureNode(CanvasTopToolbarZoomInTestTag)
            }
        }
    }

    @Ignore // TODO: java.lang.AssertionError: Failed: assertExists.
    // Reason: Expected exactly '1' node but found '2' nodes that satisfy: (isRoot)
    @Test
    fun checkToggleTopAppBarShot() {
        runDesktopComposeUiTest(
            width = 1920,
            height = 1080,
        ) {
            MainViewRobot().apply {
                setContent()

                UiBuilderRobot(screenTestRule).apply {
                    clickToggleTopAppBarButton()
                }

                capture()
            }
        }
    }

    @Test
    fun checkToggleNavigationShot() {
        runDesktopComposeUiTest(
            width = 1920,
            height = 1080,
        ) {
            MainViewRobot().apply {
                setContent()

                UiBuilderRobot(screenTestRule).apply {
                    clickToggleNavigationButton()
                }

                capture()
            }
        }
    }
}
