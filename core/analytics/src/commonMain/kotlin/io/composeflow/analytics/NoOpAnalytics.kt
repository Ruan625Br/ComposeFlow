package io.composeflow.analytics

/**
 * No-operation implementation of Analytics interface.
 * Used as a fallback when no analytics implementation is available.
 */
class NoOpAnalytics : Analytics {
    override fun initialize(
        apiKey: String,
        host: String?,
    ) {
        // No-op
    }

    override fun track(
        event: String,
        properties: Map<String, Any>?,
    ) {
        // No-op
    }

    override fun identify(
        userId: String,
        properties: Map<String, Any>?,
    ) {
        // No-op
    }

    override fun setUserProperties(properties: Map<String, Any>) {
        // No-op
    }

    override fun screen(
        screenName: String,
        properties: Map<String, Any>?,
    ) {
        // No-op
    }

    override fun reset() {
        // No-op
    }

    override fun flush() {
        // No-op
    }

    override fun shutdown() {
        // No-op
    }
}
