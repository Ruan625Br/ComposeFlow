package io.composeflow

import com.hashicorp.cdktf.TerraformIterator
import com.hashicorp.cdktf.TerraformResourceLifecycle
import com.hashicorp.cdktf.TerraformStack
import com.hashicorp.cdktf.Token
import com.hashicorp.cdktf.providers.google.project_service.ProjectService
import com.hashicorp.cdktf.providers.google.provider.GoogleProvider
import com.hashicorp.cdktf.providers.google_beta.google_firebaserules_release.GoogleFirebaserulesRelease
import com.hashicorp.cdktf.providers.google_beta.google_firebaserules_ruleset.GoogleFirebaserulesRuleset
import com.hashicorp.cdktf.providers.google_beta.google_firebaserules_ruleset.GoogleFirebaserulesRulesetSource
import com.hashicorp.cdktf.providers.google_beta.google_firebaserules_ruleset.GoogleFirebaserulesRulesetSourceFiles
import com.hashicorp.cdktf.providers.google_beta.provider.GoogleBetaProvider
import software.constructs.Construct

data class StorageStackConfig(
    val projectId: String
)

class StorageStack(
    scope: Construct,
    id: String,
    props: StorageStackConfig
) : TerraformStack(
    scope,
    id
) {
    init {
        useBackend(props.projectId)

        GoogleProvider.Builder.create(this, "Google").apply {
            project(props.projectId)
            billingProject(props.projectId)
            userProjectOverride(true)
            region("us-central1")
        }.build()

        val provider = GoogleBetaProvider.Builder.create(this, "GoogleBeta").apply {
            project(props.projectId)
            billingProject(props.projectId)
            userProjectOverride(true)
            region("us-central1")
        }.build()

        val apisIterator = TerraformIterator.fromList(
            listOf(
                "firebaserules.googleapis.com"
            )
        )
        val apis = ProjectService.Builder.create(this, "EnableApi").apply {
            forEach(apisIterator)
            service(Token.asString(apisIterator.value))
        }.build()

        val ruleset = GoogleFirebaserulesRuleset.Builder.create(this, "StorageSecurityRule").apply {
            provider(provider)
            project(props.projectId)
            source(GoogleFirebaserulesRulesetSource.Builder().apply {
                files(listOf(
                    GoogleFirebaserulesRulesetSourceFiles.Builder().apply {
                        name("storage.rules")
                        content("""
                            rules_version = '2';

                            service firebase.storage {
                                match /b/{bucket}/o {
                                    match /{userId} {
                                      allow list: if request.auth.uid == userId;
                                      match /{projectId}/{file=**} {
                                        allow read, write: if request.auth.uid == userId;
                                      }
                                    }
                                }
                            }
                        """.trimIndent()
                        )
                    }.build()
                ))
            }.build())
        }.dependsOn(
            listOf(
                apis
            )
        ).build()

        val bucketName = when (props.projectId) {
            "composeflow" -> "composeflow-prod-assets"
            "composeflow-debug" -> "composeflow-assets-debug"
            else -> throw Error("Invalid project id")
        }

        GoogleFirebaserulesRelease.Builder.create(this, "StorageAssets").apply {
            provider(provider)
            project(props.projectId)
            name("firebase.storage/$bucketName")
            rulesetName("projects/${props.projectId}/rulesets/${ruleset.name}")

            lifecycle(TerraformResourceLifecycle.Builder().apply {
                replaceTriggeredBy(listOf("google_firebaserules_ruleset.StorageSecurityRule"))
            }.build())
        }.build()
    }
}