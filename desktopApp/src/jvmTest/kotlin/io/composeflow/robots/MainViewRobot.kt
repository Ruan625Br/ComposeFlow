package io.composeflow.robots

import androidx.compose.ui.test.DesktopComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.TestLogWriter
import co.touchlab.kermit.loggerConfigInit
import com.github.takahirom.roborazzi.DEFAULT_ROBORAZZI_OUTPUT_DIR_PATH
import io.composeflow.NAVIGATION_RAIL_TEST_TAG
import io.composeflow.ProjectEditorView
import io.composeflow.ProvideTestPreCompose
import io.composeflow.TestPreComposeWindowHolder
import io.composeflow.appbuilder.AppRunner
import io.composeflow.model.TopLevelDestination
import io.composeflow.ui.ProvideCloseDialogCallback
import io.composeflow.ui.ProvideShowDialogCallback
import io.composeflow.ui.toolbar.TOOLBAR_RUN_BUTTON_TEST_TAG
import io.github.takahirom.roborazzi.captureRoboImage
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class, ExperimentalKermitApi::class)
class MainViewRobot(
    private val precomposeStateHolder: TestPreComposeWindowHolder = TestPreComposeWindowHolder(),
) {
    context(DesktopComposeUiTest)
    fun capture(fileName: String) {
        onRoot().captureRoboImage(
            filePath = "$DEFAULT_ROBORAZZI_OUTPUT_DIR_PATH/$fileName.png",
        )
    }

    context(DesktopComposeUiTest)
    fun capture() {
        onRoot().captureRoboImage()
    }

    context(DesktopComposeUiTest)
    fun captureNode(testTag: String) {
        onNodeWithTag(testTag).captureRoboImage()
    }

    context(DesktopComposeUiTest)
    fun setContent() {
        setContent {
            ProvideTestPreCompose(
                precomposeStateHolder = precomposeStateHolder,
            ) {
                ProvideShowDialogCallback(
                    onAnyDialogIsShown = {},
                ) {
                    ProvideCloseDialogCallback(onAllDialogsClosed = {}) {
                        IntUiTheme {
                            ProjectEditorView(
                                projectId = "testId",
                                onTitleBarLeftContentSet = {},
                                onTitleBarRightContentSet = {},
                            )
                        }
                    }
                }
            }
        }
    }

    context(DesktopComposeUiTest)
    fun clickRunButton(): List<TestLogWriter.LogEntry> {
        val testLogWriter =
            TestLogWriter(
                loggable = Severity.Verbose, // accept everything
            )
        AppRunner.buildLogger = Logger(loggerConfigInit(testLogWriter))
        onNodeWithTag(TOOLBAR_RUN_BUTTON_TEST_TAG)
            .performClick()
        return testLogWriter.logs
    }

    fun checkSucceed(logs: List<TestLogWriter.LogEntry>) {
        assertTrue(logs.any { it.message.contains("BUILD SUCCESSFUL") })
    }

    context(DesktopComposeUiTest)
    fun clickNavigationRailItem(topLevelDestination: TopLevelDestination) {
        val navigationRailSemanticsNode =
            onNodeWithTag("$NAVIGATION_RAIL_TEST_TAG/${topLevelDestination.name}")
        navigationRailSemanticsNode
            .performClick()
    }
}
