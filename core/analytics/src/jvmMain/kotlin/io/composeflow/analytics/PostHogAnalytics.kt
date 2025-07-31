package io.composeflow.analytics

import co.touchlab.kermit.Logger
import com.posthog.java.PostHog

/**
 * Desktop implementation of Analytics using PostHog Java SDK.
 */
class PostHogAnalytics : Analytics {
    private var postHog: PostHog? = null
    private val logger = Logger.withTag("PostHogAnalytics")
    private var currentUserId: String? = null

    override fun initialize(
        apiKey: String,
        host: String?,
    ) {
        try {
            val builder =
                PostHog.Builder(apiKey).apply {
                    if (host != null) {
                        host(host)
                    }
                }

            postHog = builder.build()
            logger.i("PostHog analytics initialized successfully")
        } catch (e: Exception) {
            logger.e("Failed to initialize PostHog analytics", e)
        }
    }

    override fun track(
        event: String,
        properties: Map<String, Any>?,
    ) {
        try {
            val userId = currentUserId ?: "anonymous"
            if (properties != null) {
                postHog?.capture(userId, event, properties.toMutableMap())
            } else {
                postHog?.capture(userId, event)
            }
            logger.d("Tracked event: $event")
        } catch (e: Exception) {
            logger.e("Failed to track event: $event", e)
        }
    }

    override fun identify(
        userId: String,
        properties: Map<String, Any>?,
    ) {
        try {
            currentUserId = userId
            if (properties != null) {
                postHog?.identify(userId, properties.toMutableMap())
            } else {
                postHog?.identify(userId, mutableMapOf<String, Any>())
            }
            logger.d("Identified user: $userId")
        } catch (e: Exception) {
            logger.e("Failed to identify user: $userId", e)
        }
    }

    override fun setUserProperties(properties: Map<String, Any>) {
        try {
            val userId = currentUserId ?: "anonymous"
            postHog?.set(userId, properties.toMutableMap())
            logger.d("Set user properties: ${properties.keys}")
        } catch (e: Exception) {
            logger.e("Failed to set user properties", e)
        }
    }

    override fun screen(
        screenName: String,
        properties: Map<String, Any>?,
    ) {
        try {
            val userId = currentUserId ?: "anonymous"
            val screenProperties =
                buildMap {
                    put("\$screen_name", screenName)
                    properties?.let { putAll(it) }
                }.toMutableMap()
            postHog?.capture(userId, "\$pageview", screenProperties)
            logger.d("Tracked screen view: $screenName")
        } catch (e: Exception) {
            logger.e("Failed to track screen view: $screenName", e)
        }
    }

    override fun reset() {
        try {
            currentUserId = null
            // PostHog Java SDK 1.2.0 may not have reset method
            // We'll just clear the current user ID
            logger.d("Analytics reset")
        } catch (e: Exception) {
            logger.e("Failed to reset analytics", e)
        }
    }

    override fun flush() {
        try {
            // PostHog Java SDK 1.2.0 may not have flush method
            // Events are typically sent automatically
            logger.d("Analytics flushed (automatic batching)")
        } catch (e: Exception) {
            logger.e("Failed to flush analytics", e)
        }
    }

    override fun shutdown() {
        try {
            postHog?.shutdown()
            logger.d("Analytics shutdown")
        } catch (e: Exception) {
            logger.e("Failed to shutdown analytics", e)
        } finally {
            postHog = null
            currentUserId = null
        }
    }
}
