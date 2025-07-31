package io.composeflow.analytics

/**
 * Desktop implementation of the analytics factory.
 * Returns PostHogAnalytics for desktop platforms.
 */
actual fun createAnalytics(): Analytics = PostHogAnalytics()
