package io.composeflow

import com.hashicorp.cdktf.DataResource
import com.hashicorp.cdktf.LocalExecProvisioner
import com.hashicorp.cdktf.TerraformIterator
import com.hashicorp.cdktf.TerraformResourceLifecycle
import com.hashicorp.cdktf.TerraformStack
import com.hashicorp.cdktf.Token
import com.hashicorp.cdktf.providers.google.artifact_registry_repository.ArtifactRegistryRepository
import com.hashicorp.cdktf.providers.google.cloud_run_v2_service.CloudRunV2Service
import com.hashicorp.cdktf.providers.google.cloud_run_v2_service.CloudRunV2ServiceTemplate
import com.hashicorp.cdktf.providers.google.cloud_run_v2_service.CloudRunV2ServiceTemplateContainers
import com.hashicorp.cdktf.providers.google.cloud_run_v2_service.CloudRunV2ServiceTemplateContainersEnv
import com.hashicorp.cdktf.providers.google.cloud_run_v2_service.CloudRunV2ServiceTemplateContainersEnvValueSource
import com.hashicorp.cdktf.providers.google.cloud_run_v2_service.CloudRunV2ServiceTemplateContainersEnvValueSourceSecretKeyRef
import com.hashicorp.cdktf.providers.google.cloud_run_v2_service.CloudRunV2ServiceTemplateContainersResources
import com.hashicorp.cdktf.providers.google.cloud_run_v2_service_iam_binding.CloudRunV2ServiceIamBinding
import com.hashicorp.cdktf.providers.google.data_google_secret_manager_secret.DataGoogleSecretManagerSecret
import com.hashicorp.cdktf.providers.google.data_google_secret_manager_secret.DataGoogleSecretManagerSecretConfig
import com.hashicorp.cdktf.providers.google.project_service.ProjectService
import com.hashicorp.cdktf.providers.google.provider.GoogleProvider
import com.hashicorp.cdktf.providers.google.secret_manager_secret_iam_member.SecretManagerSecretIamMember
import com.hashicorp.cdktf.providers.google.secret_manager_secret_iam_member.SecretManagerSecretIamMemberConfig
import com.hashicorp.cdktf.providers.google.service_account.ServiceAccount
import com.hashicorp.cdktf.providers.google_beta.google_project_iam_member.GoogleProjectIamMember
import com.hashicorp.cdktf.providers.google_beta.provider.GoogleBetaProvider
import software.constructs.Construct
import java.nio.file.Paths

data class MainStackConfig(
    val projectId: String
)

class MainStack(
    scope: Construct,
    id: String,
    props: MainStackConfig
) : TerraformStack(
    scope,
    id
) {
    init {
        useBackend(props.projectId)

        GoogleProvider.Builder.create(this, "Google").apply {
            project(props.projectId)
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
                "artifactregistry.googleapis.com",
                "run.googleapis.com"
            )
        )
        val apis = ProjectService.Builder.create(this, "EnableApi").apply {
            forEach(apisIterator)
            service(Token.asString(apisIterator.value))
        }.build()

        val repo =
            ArtifactRegistryRepository.Builder.create(this, "ArtifactRegistryRepository").apply {
                format("docker")
                location("us-central1")
                repositoryId("composeflow-server")
            }.dependsOn(listOf(apis))
                .build()

        val dirHash = listOf(
            "../gradle/server.libs.versions.toml",
            "../server/**/**",
        ).toFilesHexString()
        DataResource.Builder.create(this, "ServerDirChanges").apply {
            input(
                mapOf(
                    "hash" to dirHash
                )
            )
        }.build()

        val workingDir = Paths.get("../server").toAbsolutePath().toString()

        val dockerImage = DataResource.Builder.create(this, "DockerBuildServer").apply {
            provisioners(
                listOf(
                    LocalExecProvisioner.Builder().apply {
                        type("local-exec")
                        command("../gradlew publishImage")
                        workingDir(workingDir)
                        environment(
                            mapOf(
                                "GOOGLE_CLOUD_PROJECT" to repo.project,
                                "REGION" to repo.location,
                                "REPO_NAME" to repo.repositoryId,
                                "REPO_IMAGE_TAG" to dirHash,
                            )
                        )
                        lifecycle(TerraformResourceLifecycle.Builder().apply {
                            replaceTriggeredBy(
                                listOf(
                                    "terraform_data.ServerDirChanges"
                                )
                            )
                        }.build())
                    }.build(),
                )
            )
        }.dependsOn(listOf(repo))
            .build()

        val serviceAccount = ServiceAccount.Builder.create(this, "ServerServiceAccount").apply {
            accountId("server")
            displayName("ComposeFlow server Service Account")
        }.build()

        val googleClientId = DataGoogleSecretManagerSecret(
            this,
            "GoogleClientId",
            DataGoogleSecretManagerSecretConfig.Builder().apply {
                secretId("google_client_id")
            }.build()
        )

        val googleClientIdSecretAccess = SecretManagerSecretIamMember(
            this,
            "SecretAccessGoogleClientId",
            SecretManagerSecretIamMemberConfig.Builder().apply {
                secretId(googleClientId.secretId)
                role("roles/secretmanager.secretAccessor")
                member("serviceAccount:${serviceAccount.email}")
            }.build()
        )

        val googleClientSecret = DataGoogleSecretManagerSecret(
            this,
            "GoogleClientSecret",
            DataGoogleSecretManagerSecretConfig.Builder().apply {
                secretId("google_client_secret")
            }.build()
        )

        val stripeApiKey = DataGoogleSecretManagerSecret(
            this,
            "StripeApiKey",
            DataGoogleSecretManagerSecretConfig.Builder().apply {
                secretId("STRIPE_API_KEY")
            }.build()
        )

        val googleClientSecretSecretAccess = SecretManagerSecretIamMember(
            this,
            "SecretAccessGoogleClientSecret",
            SecretManagerSecretIamMemberConfig.Builder().apply {
                secretId(googleClientSecret.secretId)
                role("roles/secretmanager.secretAccessor")
                member("serviceAccount:${serviceAccount.email}")
            }.build()
        )

        val stripeApiKeySecretAccess = SecretManagerSecretIamMember(
            this,
            "SecretAccessStripeApiKey",
            SecretManagerSecretIamMemberConfig.Builder().apply {
                secretId(stripeApiKey.secretId)
                role("roles/secretmanager.secretAccessor")
                member("serviceAccount:${serviceAccount.email}")
            }.build()
        )

        GoogleProjectIamMember.Builder.create(this, "ServerServiceAccountIamMember").apply {
            provider(provider)
            project(props.projectId)
            role("roles/firebase.sdkAdminServiceAgent")
            member("serviceAccount:${serviceAccount.email}")
        }.build()

        val serverRun = CloudRunV2Service.Builder.create(this, "Server").apply {
            name("server")
            location("us-central1")
            deletionProtection(false)
            ingress("INGRESS_TRAFFIC_ALL")
            template(CloudRunV2ServiceTemplate.Builder().apply {
                serviceAccount(serviceAccount.email)
                containers(
                    listOf(
                        CloudRunV2ServiceTemplateContainers.Builder().apply {
                            image("us-central1-docker.pkg.dev/${props.projectId}/composeflow-server/composeflow/server:$dirHash")
                            resources(CloudRunV2ServiceTemplateContainersResources.Builder().apply {
                                startupCpuBoost(true)
                                limits(
                                    mapOf(
                                        "cpu" to "1",
                                        "memory" to "1024Mi"
                                    )
                                )
                            }.build())
                            env(
                                listOf(
                                    CloudRunV2ServiceTemplateContainersEnv.Builder().apply {
                                        name("GOOGLE_CLOUD_PROJECT")
                                        value(props.projectId)
                                    }.build(),
                                    CloudRunV2ServiceTemplateContainersEnv.Builder().apply {
                                        name("GOOGLE_CLIENT_ID")
                                        valueSource(
                                            CloudRunV2ServiceTemplateContainersEnvValueSource.Builder()
                                                .apply {
                                                    secretKeyRef(
                                                        CloudRunV2ServiceTemplateContainersEnvValueSourceSecretKeyRef.Builder()
                                                            .apply {
                                                                secret("google_client_id")
                                                                version("latest")
                                                            }
                                                            .build())
                                                }.build()
                                        )
                                    }.build(),
                                    CloudRunV2ServiceTemplateContainersEnv.Builder().apply {
                                        name("GOOGLE_CLIENT_SECRET")
                                        valueSource(
                                            CloudRunV2ServiceTemplateContainersEnvValueSource.Builder()
                                                .apply {
                                                    secretKeyRef(
                                                        CloudRunV2ServiceTemplateContainersEnvValueSourceSecretKeyRef.Builder()
                                                            .apply {
                                                                secret("google_client_secret")
                                                                version("latest")
                                                            }
                                                            .build())
                                                }.build()
                                        )
                                    }.build(),
                                    CloudRunV2ServiceTemplateContainersEnv.Builder().apply {
                                        name("STRIPE_API_KEY")
                                        valueSource(
                                            CloudRunV2ServiceTemplateContainersEnvValueSource.Builder()
                                                .apply {
                                                    secretKeyRef(
                                                        CloudRunV2ServiceTemplateContainersEnvValueSourceSecretKeyRef.Builder()
                                                            .apply {
                                                                secret("STRIPE_API_KEY")
                                                                version("latest")
                                                            }
                                                            .build())
                                                }.build()
                                        )
                                    }.build()
                                )
                            )
                        }.build()
                    )
                )
            }.build())
        }.dependsOn(
            listOf(
                dockerImage,
                googleClientIdSecretAccess,
                googleClientSecretSecretAccess,
                stripeApiKeySecretAccess
            )
        ).build()

        CloudRunV2ServiceIamBinding.Builder.create(this, "ServerIamBinding").apply {
            location(serverRun.location)
            name(serverRun.name)
            role("roles/run.invoker")
            members(listOf("allUsers"))
        }.build()
    }
}
