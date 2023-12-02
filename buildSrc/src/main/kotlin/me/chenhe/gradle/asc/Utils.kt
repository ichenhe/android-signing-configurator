package me.chenhe.gradle.asc

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider


/**
 * Shortcut for [ObjectFactory.property] with `String::class.java` type and default value.
 */
fun ObjectFactory.stringProperty(default: String?): Property<String> =
    property(String::class.java).convention(default)

/**
 * Gets the value of the environment variable specified by the wrapped key.
 * @return the value of env var or `null` if no key provided.
 */
fun getEnv(key: Provider<String>): String? = key.orNull?.let { System.getenv(it) }