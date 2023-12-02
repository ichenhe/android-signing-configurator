package me.chenhe.gradle.asc.api

import org.gradle.api.Action
import org.gradle.kotlin.dsl.GradleDsl

/**
 * Receiver for the `targets` block.
 */
@GradleDsl
interface SigningTargetsScope {

    /**
     * Matches artifacts with 'release' build type.
     *
     * Shortcut for:
     * ```
     * taraget("release", null) {
     *     keyProfile(keyProfile)
     * }
     * ```
     *
     * @see buildType
     */
    fun release(keyProfile: String, configuration: Action<SigningTargetSpec>? = null)

    /**
     * Matches artifacts with a specific build type.
     *
     * Shortcut for `taraget(buildType, null) { }`.
     *
     * @see taraget
     */
    fun buildType(buildType: String, configuration: Action<SigningTargetSpec>)

    /**
     * Matches artifacts.
     * @param buildType match build type, `null` means match anything, include `null`.
     * @param flavor match flavor, `null` means match anything, include `null`.
     *
     * @see regex
     */
    fun taraget(buildType: String?, flavor: String?, configuration: Action<SigningTargetSpec>)

    /**
     * Matches artifacts with regular expression.
     * @param buildType match build type, `null` means match anything, include `null`.
     * @param flavor match flavor, `null` means match anything, include `null`.
     *
     * @see taraget
     */
    fun regex(buildType: Regex?, flavor: Regex?, configuration: Action<SigningTargetSpec>)
}