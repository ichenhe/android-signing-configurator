package me.chenhe.gradle.asc.api

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.GradleDsl

/**
 * The DSL for configuring properties of a sigining target.
 */
@GradleDsl
interface SigningTargetSpec {
    /**
     * If false, signing is ignored if there is no key profile for the given name (usually because
     * the key source is invalid), which means keep orginial android signing config of current
     * target.
     */
    val abortOnError: Property<Boolean>

    /**
     * Which configuration to use for signing. The name here needs to correspond to the name of
     * item in `keyProfiles` block.
     *
     * If multiple profiles are set, try them in order. Please note that only the validity of the key
     * source can be checked here, and it is not possible to determine whether the specific
     * configuration is correct (e.g. whether the certificate password is correct) due to Android
     * Gradle Plugin limitations.
     */
    val keyProfiles: ListProperty<String>

    /**
     * Add a key profile to the candidates used for signing current target.
     * @see keyProfiles
     */
    fun keyProfile(name: String)
}