package io.composeflow

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import io.composeflow.model.parameter.RowTrait
import io.composeflow.model.parameter.TextTrait
import io.composeflow.robots.MainViewRobot
import io.composeflow.robots.UiBuilderRobot
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Ignore
import kotlin.test.Test

// lateinit property scene has not been initialized
// kotlin.UninitializedPropertyAccessException: lateinit property scene has not been initialized
@Ignore
@OptIn(ExperimentalTestApi::class)
@RunWith(TestParameterInjector::class)
class ScreenBuilderTabTest {
    @get:Rule
    val screenTestRule = ScreenTestRule()

    @Test
    fun checkScreenBuilderTab() {
        runDesktopComposeUiTest(
            width = 1920,
            height = 1080,
        ) {
            MainViewRobot().apply {
                setContent()

                UiBuilderRobot(screenTestRule).apply {
                    dragToCanvas(RowTrait())
                    dragToCanvasNodeLeftTopEdge(TextTrait(), RowTrait())
                    clickCanvasNode(RowTrait())
                    clickScreenBuilderTab()
                }

                capture()
            }
        }
    }
}
