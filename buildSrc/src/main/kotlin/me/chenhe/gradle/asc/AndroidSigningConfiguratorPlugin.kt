package me.chenhe.gradle.asc

import com.android.build.api.dsl.ApkSigningConfig
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.ApplicationVariant
import com.android.build.gradle.AppPlugin
import me.chenhe.gradle.asc.internal.DefaultAndroidSigningConfiguratorExtension
import me.chenhe.gradle.asc.internal.DefaultKeyProfileScope
import me.chenhe.gradle.asc.internal.SigningTargetManager
import me.chenhe.gradle.asc.internal.keysrc.PropertiesFileKeySource
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger

class AndroidSigningConfiguratorPlugin : Plugin<Project> {
    companion object {
        private const val DEFAULT_KEY_PROFILE_NAME = "auto_detected"
        private const val DEFAULT_PROPERTIES_FILE = "signing.properties"
    }

    private lateinit var logger: Logger

    override fun apply(project: Project) {
        logger = project.logger

        val ext = project.extensions
            .create("androidSigning", DefaultAndroidSigningConfiguratorExtension::class.java)
        ext.enabled.convention(true)
        if (ext.enabled.orNull != true) {
            return
        }

        project.plugins.withType(AppPlugin::class.java) {
            val androidComponentsExt =
                project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)

            @Suppress("UnstableApiUsage")
            androidComponentsExt.finalizeDsl { appExt ->
                setDefault(project, ext)

                // create android signing configs
                appExt.signingConfigs {
                    createAndroidSigningConfigs(ext.keyProfiles)
                }

                // apply siging config
                androidComponentsExt.onVariants { variant ->
                    ext.targetManager
                    modifyArtifactsSigningConfig(appExt, variant, ext.targetManager)
                }
            }
        }
    }

    private fun setDefault(project: Project, ext: DefaultAndroidSigningConfiguratorExtension) {
        if (ext.keyProfiles.isEmpty()) {
            // add default key profile
            ext.keyProfiles.create(DEFAULT_KEY_PROFILE_NAME) {
                fromPropertiesFile()
                fromEnvironmentVariable()
            }
        }

        // set default signing properties file
        for (keyProfile in ext.keyProfiles) {
            keyProfile.eachKeySource { keySource ->
                if (keySource is PropertiesFileKeySource) {
                    val f = project.layout.projectDirectory.file(DEFAULT_PROPERTIES_FILE)
                    keySource.propertiesFile.convention(f)
                }
            }
        }

        if (!ext.targetManager.targets.isPresent || ext.targetManager.targets.get().isEmpty()) {
            // add default target
            ext.targetManager.buildType("release") {
                abortOnError.set(false)
                keyProfile(DEFAULT_KEY_PROFILE_NAME)
            }
        }

        ext.targetManager.eachTarget {
            it.abortOnError.convention(true)
        }
    }

    /**
     * Reads cert configurations from configured sources and add them to android plugin.
     */
    private fun NamedDomainObjectContainer<out ApkSigningConfig>.createAndroidSigningConfigs(
        signingConfigs: NamedDomainObjectContainer<DefaultKeyProfileScope>
    ) {
        for (signingConfig in signingConfigs) {
            signingConfig.findFirstKeyConfiguration()?.also { cert ->
                create(signingConfig.name) {
                    storeFile = cert.storeFile
                    storePassword = cert.storePwd
                    keyAlias = cert.keyAlias
                    keyPassword = cert.keyPwd
                }
            }
        }
    }

    private fun modifyArtifactsSigningConfig(
        appExt: ApplicationExtension,
        variant: ApplicationVariant,
        targetManager: SigningTargetManager,
    ) {
        val t = targetManager.matchArtifact(variant.buildType, variant.flavorName) ?: return
        val names = t.keyProfiles.orNull
        if (names.isNullOrEmpty()) {
            if (t.abortOnError.get()) {
                val message = """
                        You must specify which signing profile to use for signing, for example:
                        androidSigning {
                            targets {
                                buildType("release") {
                                    keyProfile("xxx") /* <--- add this */
                                }
                            }
                        }
                    """.trimIndent()
                throw IllegalStateException(message)
            }
            logger.warn(
                "Ignore to config signing '{}' beacause no keyProfiles provided.",
                variant.name
            )
            return
        }

        var configured = false
        for (name in names) {
            val conf = appExt.signingConfigs.findByName(name)
            if (conf != null) {
                @Suppress("UnstableApiUsage")
                variant.signingConfig.setConfig(conf)
                configured = true
                break
            }
        }

        if (!configured) {
            if (t.abortOnError.get()) {
                val message = """
                        Failed to find at least one signing profile in [${names.joinToString()}], did you add it? Or the profile (key source) is invalid.
                        You can ignore this error by configuring it in module's build.gradle:
                        androidSigning {
                            targets {
                                buildType("release") {
                                    abortOnError.set(false) /* <--- add this */
                                }
                            }
                        }
                    """.trimIndent()
                throw IllegalStateException(message)

            }
            logger.warn(
                "Ignore to config signing '{}' because no valid key profile.",
                variant.name
            )
        }
    }
}