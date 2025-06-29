package io.composeflow

import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.InternalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.provideRoborazziContext
import org.junit.rules.TestWatcher
import org.junit.runner.Description

// TODO: Create ScreenshotTestRule using this
@OptIn(ExperimentalRoborazziApi::class, InternalRoborazziApi::class)
class RoborazziOptionsRule : TestWatcher() {
    override fun starting(description: Description?) {
        provideRoborazziContext().setRuleOverrideRoborazziOptions(
            RoborazziOptions(
                compareOptions = RoborazziOptions.CompareOptions(changeThreshold = 0F),
                recordOptions =
                    RoborazziOptions.RecordOptions(
                        // For saving money
                        resizeScale = 0.8,
                    ),
            ),
        )
    }

    override fun finished(description: Description?) {
        provideRoborazziContext().clearRuleOverrideDescription()
    }
}
