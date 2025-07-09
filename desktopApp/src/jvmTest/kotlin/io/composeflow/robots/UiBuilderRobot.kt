package io.composeflow.robots

import androidx.compose.ui.test.DesktopComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import io.composeflow.ScreenTestRule
import io.composeflow.model.parameter.ComposeTrait
import io.composeflow.testing.dragTo
import io.composeflow.testing.exists
import io.composeflow.ui.palette.PALETTE_TEST_TAG
import io.composeflow.ui.uibuilder.CANVAS_TOP_TOOLBAR_DARK_MODE_SWITCH_TEST_TAG
import io.composeflow.ui.uibuilder.CANVAS_TOP_TOOLBAR_ZOOM_IN_TEST_TAG
import io.composeflow.ui.uibuilder.DEVICE_CANVAS_TEST_TAG
import io.composeflow.ui.uibuilder.TOGGLE_NAV_BUTTON_TEST_TAG
import io.composeflow.ui.uibuilder.TOGGLE_TOP_APP_BAR_BUTTON_TEST_TAG
import io.composeflow.ui.uibuilder.UI_BUILDER_SCREEN_BUILDER_TAB_TEST_TAG

@OptIn(ExperimentalTestApi::class)
class UiBuilderRobot(
    val screenTestRule: ScreenTestRule,
) {
    context (DesktopComposeUiTest)
    fun dragToCanvas(composeTrait: ComposeTrait) {
        onAllNodesWithTag("$PALETTE_TEST_TAG/$composeTrait")
            .onFirst()
            .dragTo(
                target = onNodeWithTag(DEVICE_CANVAS_TEST_TAG),
            ) {
                onNodeWithTag("$DEVICE_CANVAS_TEST_TAG/${composeTrait.iconText()}").exists()
            }
    }

    context (DesktopComposeUiTest)
    fun dragToCanvasNodeLeftTopEdge(
        composeTrait: ComposeTrait,
        canvasNode: ComposeTrait,
    ) {
        onAllNodesWithTag("$PALETTE_TEST_TAG/${composeTrait.iconText()}")
            .onFirst()
            .dragTo(
                target = onNodeWithTag(DEVICE_CANVAS_TEST_TAG),
                targetPointXRatio = 0.1F,
                targetPointYRatio = 0.1F,
            ) {
                onNodeWithTag("$DEVICE_CANVAS_TEST_TAG/${composeTrait.iconText()}").exists()
            }
    }

    context (DesktopComposeUiTest)
    fun dragToCanvasNode(
        paletteNode: ComposeTrait,
        canvasNode: ComposeTrait,
    ) {
        onAllNodesWithTag("$PALETTE_TEST_TAG/$paletteNode")
            .onFirst()
            .dragTo(
                target = onNodeWithTag("$DEVICE_CANVAS_TEST_TAG/${canvasNode.iconText()}"),
            ) {
                onNodeWithTag("$DEVICE_CANVAS_TEST_TAG/${canvasNode.iconText()}").exists()
            }
    }

    context(DesktopComposeUiTest)
    fun clickCanvasNode(paletteNode: ComposeTrait) {
        val paletteSemanticsNode = onNodeWithTag("$DEVICE_CANVAS_TEST_TAG/$paletteNode")
        paletteSemanticsNode
            .performClick()
    }

    context(DesktopComposeUiTest)
    fun clickDarkThemeSwitch() {
        onNodeWithTag(CANVAS_TOP_TOOLBAR_DARK_MODE_SWITCH_TEST_TAG)
            .performClick()
        screenTestRule.coroutinesDispatcherRule.advanceUntilIdle()
    }

    context(DesktopComposeUiTest)
    fun clickZoomIn() {
        onNodeWithTag(CANVAS_TOP_TOOLBAR_ZOOM_IN_TEST_TAG)
            .performClick()
        screenTestRule.coroutinesDispatcherRule.advanceUntilIdle()
    }

    context(DesktopComposeUiTest)
    fun clickScreenBuilderTab() {
        onNodeWithTag(UI_BUILDER_SCREEN_BUILDER_TAB_TEST_TAG)
            .performClick()
        screenTestRule.coroutinesDispatcherRule.advanceUntilIdle()
    }

    context(DesktopComposeUiTest)
    fun clickToggleTopAppBarButton() {
        onNodeWithTag(TOGGLE_TOP_APP_BAR_BUTTON_TEST_TAG)
            .performClick()
        screenTestRule.coroutinesDispatcherRule.advanceUntilIdle()
    }

    context(DesktopComposeUiTest)
    fun clickToggleNavigationButton() {
        onNodeWithTag(TOGGLE_NAV_BUTTON_TEST_TAG)
            .performClick()
        screenTestRule.coroutinesDispatcherRule.advanceUntilIdle()
    }
}
