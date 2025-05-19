package io.composeflow

import com.hashicorp.cdktf.App

fun main() {
    val app = App()
    val projectId = System.getenv("GOOGLE_CLOUD_PROJECT") ?: "composeflow-debug"

    FirestoreStack(
        app, "firestore", FirestoreStackConfig(
            projectId
        )
    )

    MainStack(
        app, "main", MainStackConfig(
            projectId
        )
    )

    StorageStack(
        app, "storage", StorageStackConfig(
            projectId
        )
    )

    app.synth()
}