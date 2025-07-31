package io.composeflow.analytics

/**
 * Factory function to create the appropriate Analytics implementation.
 * This is implemented as an expect/actual pattern to provide platform-specific implementations.
 */
expect fun createAnalytics(): Analytics
