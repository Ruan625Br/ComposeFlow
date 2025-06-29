package io.composeflow

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutinesDispatcherRule : TestWatcher() {
    val standardTestDispatcher = StandardTestDispatcher()
    private val unconfinedTestDispatcher = UnconfinedTestDispatcher()

    override fun starting(description: org.junit.runner.Description) {
        Dispatchers.setMain(unconfinedTestDispatcher)
    }

    override fun finished(description: org.junit.runner.Description) {
        Dispatchers.resetMain()
    }

    fun advanceUntilIdle() {
        standardTestDispatcher.scheduler.advanceUntilIdle()
    }
}
