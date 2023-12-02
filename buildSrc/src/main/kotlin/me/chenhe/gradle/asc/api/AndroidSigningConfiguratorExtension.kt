package me.chenhe.gradle.asc.api

import me.chenhe.gradle.asc.ScopedNamedDomainObjectContainer
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.GradleDsl


@GradleDsl
interface AndroidSigningConfiguratorExtension {
    /**
     * If false this plugin is totally disabled, as if without it but you can keep the
     * configuration block.
     */
    val enabled: Property<Boolean>

    val keyProfiles: ScopedNamedDomainObjectContainer<out KeyProfileScope>

    val project: Project

    /**
     * Configures the signing keys, including where to read signing key from.
     *
     * The name of each configuration item will be passed to `android.signingConfigs`, as if manually
     * configured.
     *
     * Multiple sources can be added to each profile and attempts to read from these sources will
     * be made in order. Please note that it is impossible to verify the correctness of perperties
     * themself. e.g. whether the password for the key is correct.
     *
     * Example:
     * ```
     * keyProfiles {
     *     create("release") {
     *         fromPropertiesFile()
     *         fromEnvironmentVariable()
     *     }
     * }
     * ```
     */
    fun keyProfiles(configure: Action<ScopedNamedDomainObjectContainer<out KeyProfileScope>>) {
        configure.execute(keyProfiles)
    }

    /**
     * Configures the target artifacts to be signed.
     * Multiple targets can be added, each independently configurable.
     *
     * The following functions can be used to add target in this block:
     * - [SigningTargetsScope.buildType]: match specific build-type
     * - [SigningTargetsScope.taraget]: match specific build-type *and* flavor
     * - [SigningTargetsScope.regex]: match build-type *and* flavor with regex
     *
     * For a full list of avaliable functions, please see [SigningTargetsScope].
     *
     * Example:
     * ```
     * targets {
     *     buildType("release") {
     *         keyProfile("normal")
     *     }
     *
     *     taraget(buildType = "release", flavor = "play_store") {
     *         keyProfile("play")
     *     }
     * }
     * ```
     */
    fun targets(signingTargetsConfiguration: Action<SigningTargetsScope>)
}