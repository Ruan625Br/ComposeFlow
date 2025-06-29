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
import io.composeflow.ui.palette.PaletteTestTag
import io.composeflow.ui.uibuilder.CanvasTopToolbarDarkModeSwitchTestTag
import io.composeflow.ui.uibuilder.CanvasTopToolbarZoomInTestTag
import io.composeflow.ui.uibuilder.DeviceCanvasTestTag
import io.composeflow.ui.uibuilder.ToggleNavButtonTestTag
import io.composeflow.ui.uibuilder.ToggleTopAppBarButtonTestTag
import io.composeflow.ui.uibuilder.UiBuilderScreenBuilderTabTestTag

@OptIn(ExperimentalTestApi::class)
class UiBuilderRobot(
    val screenTestRule: ScreenTestRule,
) {
    context (DesktopComposeUiTest)
    fun dragToCanvas(composeTrait: ComposeTrait) {
        onAllNodesWithTag("$PaletteTestTag/$composeTrait")
            .onFirst()
            .dragTo(
                target = onNodeWithTag(DeviceCanvasTestTag),
            ) {
                onNodeWithTag("$DeviceCanvasTestTag/${composeTrait.iconText()}").exists()
            }
    }

    context (DesktopComposeUiTest)
    fun dragToCanvasNodeLeftTopEdge(
        composeTrait: ComposeTrait,
        canvasNode: ComposeTrait,
    ) {
        onAllNodesWithTag("$PaletteTestTag/${composeTrait.iconText()}")
            .onFirst()
            .dragTo(
                target = onNodeWithTag(DeviceCanvasTestTag),
                targetPointXRatio = 0.1F,
                targetPointYRatio = 0.1F,
            ) {
                onNodeWithTag("$DeviceCanvasTestTag/${composeTrait.iconText()}").exists()
            }
    }

    context (DesktopComposeUiTest)
    fun dragToCanvasNode(
        paletteNode: ComposeTrait,
        canvasNode: ComposeTrait,
    ) {
        onAllNodesWithTag("$PaletteTestTag/$paletteNode")
            .onFirst()
            .dragTo(
                target = onNodeWithTag("$DeviceCanvasTestTag/${canvasNode.iconText()}"),
            ) {
                onNodeWithTag("$DeviceCanvasTestTag/${canvasNode.iconText()}").exists()
            }
    }

    context(DesktopComposeUiTest)
    fun clickCanvasNode(paletteNode: ComposeTrait) {
        val paletteSemanticsNode = onNodeWithTag("$DeviceCanvasTestTag/$paletteNode")
        paletteSemanticsNode
            .performClick()
    }

    context(DesktopComposeUiTest)
    fun clickDarkThemeSwitch() {
        onNodeWithTag(CanvasTopToolbarDarkModeSwitchTestTag)
            .performClick()
        screenTestRule.coroutinesDispatcherRule.advanceUntilIdle()
    }

    context(DesktopComposeUiTest)
    fun clickZoomIn() {
        onNodeWithTag(CanvasTopToolbarZoomInTestTag)
            .performClick()
        screenTestRule.coroutinesDispatcherRule.advanceUntilIdle()
    }

    context(DesktopComposeUiTest)
    fun clickScreenBuilderTab() {
        onNodeWithTag(UiBuilderScreenBuilderTabTestTag)
            .performClick()
        screenTestRule.coroutinesDispatcherRule.advanceUntilIdle()
    }

    context(DesktopComposeUiTest)
    fun clickToggleTopAppBarButton() {
        onNodeWithTag(ToggleTopAppBarButtonTestTag)
            .performClick()
        screenTestRule.coroutinesDispatcherRule.advanceUntilIdle()
    }

    context(DesktopComposeUiTest)
    fun clickToggleNavigationButton() {
        onNodeWithTag(ToggleNavButtonTestTag)
            .performClick()
        screenTestRule.coroutinesDispatcherRule.advanceUntilIdle()
    }
}
