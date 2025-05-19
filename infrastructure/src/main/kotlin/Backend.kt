package io.composeflow

import com.hashicorp.cdktf.GcsBackend
import com.hashicorp.cdktf.GcsBackendConfig
import com.hashicorp.cdktf.TerraformStack

fun TerraformStack.useBackend(projectId: String) {
    GcsBackend(this, GcsBackendConfig.Builder().apply {
        bucket("$projectId-terraform")
        prefix(this@useBackend::class.simpleName)
    }.build())
}