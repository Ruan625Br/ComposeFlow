package io.composeflow.util

import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import io.composeflow.platform.computeAdaptiveWindowInfo

@Composable
fun calculateCustomNavSuiteType(isTopLevelDestination: Boolean): NavigationSuiteType {
    val adaptiveInfo = computeAdaptiveWindowInfo()
    val customNavSuiteType = with(adaptiveInfo) {
        val navSuiteType = NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(adaptiveInfo)
        if (navSuiteType == NavigationSuiteType.NavigationBar && !isTopLevelDestination) {
            NavigationSuiteType.None
        } else {
            navSuiteType
        }
    }
    return customNavSuiteType
}