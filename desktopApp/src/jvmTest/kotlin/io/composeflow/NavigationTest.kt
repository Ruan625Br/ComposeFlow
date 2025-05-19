package io.composeflow

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import io.composeflow.model.TopLevelDestination
import io.composeflow.robots.MainViewRobot
import org.junit.Ignore
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
@RunWith(TestParameterInjector::class)
@Ignore // Temporarily ignoring until capturing works
class NavigationTest {
    @get:Rule
    val screenTestRule = ScreenTestRule()

    @Test
    fun checkDataTypeEditorShot() {
        runDesktopComposeUiTest(
            width = 1920,
            height = 1080,
        ) {
            MainViewRobot().apply {
                setContent()

                clickNavigationRailItem(TopLevelDestination.DataTypeEditor)

                capture()
            }
        }
    }

    @Test
    fun checkApiEditorShot() {
        runDesktopComposeUiTest(
            width = 1920,
            height = 1080,
        ) {
            MainViewRobot().apply {
                setContent()

                clickNavigationRailItem(TopLevelDestination.ApiEditor)

                capture()
            }
        }
    }

    @Test
    fun checkSettingsShot() {
        runDesktopComposeUiTest(
            width = 1920,
            height = 1080,
        ) {
            MainViewRobot().apply {
                setContent()

                clickNavigationRailItem(TopLevelDestination.Settings)

                capture()
            }
        }
    }
}
