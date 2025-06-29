package io.composeflow

import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class ScreenTestRule : TestRule {
    val coroutinesDispatcherRule = CoroutinesDispatcherRule()
    private val dependencyInjectionRule = DependencyInjectionRule(coroutinesDispatcherRule)

    override fun apply(
        base: Statement,
        description: Description,
    ): Statement =
        RuleChain
            .outerRule(RoborazziOptionsRule())
            .around(coroutinesDispatcherRule)
            .around(dependencyInjectionRule)
            .apply(base, description)
}
