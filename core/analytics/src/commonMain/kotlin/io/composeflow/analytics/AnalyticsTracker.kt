package io.composeflow.analytics

import io.composeflow.di.ServiceLocator

/**
 * Helper object for comprehensive analytics tracking operations.
 * Provides convenient methods for tracking all user interactions in ComposeFlow.
 */
object AnalyticsTracker {
    private val analytics: Analytics
        get() = ServiceLocator.get<Analytics>()

    fun trackAppStartup() {
        analytics.track("app_startup")
    }

    fun trackProjectSaved(
        saveTrigger: String? = null,
        autoSave: Boolean = false,
    ) {
        analytics.track(
            "project_saved",
            buildMap {
                saveTrigger?.let { put("save_trigger", it) }
                put("auto_save", autoSave)
            },
        )
    }

    fun trackComposeNodeAdded(
        nodeType: String,
        containerType: String? = null,
        source: String? = null,
    ) {
        analytics.track(
            "compose_node_added",
            buildMap {
                put("node_type", nodeType)
                containerType?.let { put("container_type", it) }
                source?.let { put("source", it) } // palette/paste/ai
            },
        )
    }

    fun trackComposeNodeDeleted(
        nodeType: String,
        count: Int = 1,
    ) {
        analytics.track(
            "compose_node_deleted",
            mapOf(
                "node_type" to nodeType,
                "count" to count,
            ),
        )
    }

    fun trackComposeNodeCopied(
        nodeType: String,
        count: Int = 1,
    ) {
        analytics.track(
            "compose_node_copied",
            mapOf(
                "node_type" to nodeType,
                "count" to count,
            ),
        )
    }

    fun trackComposeNodePasted(
        nodeType: String,
        count: Int = 1,
    ) {
        analytics.track(
            "compose_node_pasted",
            mapOf(
                "node_type" to nodeType,
                "count" to count,
            ),
        )
    }

    fun trackScreenCreated(
        screenType: String? = null,
        templateUsed: String? = null,
        creationMethod: String? = null,
    ) {
        analytics.track(
            "screen_created",
            buildMap {
                screenType?.let { put("screen_type", it) }
                templateUsed?.let { put("template_used", it) }
                creationMethod?.let { put("creation_method", it) } // manual/ai
            },
        )
    }

    fun trackScreenDeleted(
        screenType: String? = null,
        hadComponents: Boolean = false,
    ) {
        analytics.track(
            "screen_deleted",
            buildMap {
                screenType?.let { put("screen_type", it) }
                put("had_components", hadComponents)
            },
        )
    }

    fun trackScreenSwitched(
        fromScreen: String? = null,
        toScreen: String? = null,
    ) {
        analytics.track(
            "screen_switched",
            buildMap {
                fromScreen?.let { put("from_screen", it) }
                toScreen?.let { put("to_screen", it) }
            },
        )
    }

    fun trackUserLogin(method: String) {
        analytics.track("user_login", mapOf("method" to method))
    }

    fun trackUserLogout() {
        analytics.track("user_logout")
    }
}
