package me.chenhe.gradle.asc.api

import me.chenhe.gradle.asc.internal.keysrc.EnvironmentVariableKeySource
import me.chenhe.gradle.asc.internal.keysrc.PropertiesFileKeySource
import org.gradle.api.Action
import org.gradle.api.Named
import org.gradle.kotlin.dsl.GradleDsl

/**
 * Receiver for key profile builder block.
 */
@GradleDsl
interface KeyProfileScope : Named {

    /**
     * Add a source that reads from environment variables. The prefix or name
     * of env variables can be customized by optional configuration block.
     *
     * Example:
     * ```
     * fromEnvironmentVariable {
     *     prefix = "ANDROID_SIGNING_"
     *     storeFileKey = "STORE_PATH"
     *     storePasswordKey = "STORE_PWD"
     *     keyAliasKey = "KEY_ALIAS"
     *     keyPasswordKey = "KEY_PWD"
     * }
     * ```
     */
    fun fromEnvironmentVariable(configure: Action<EnvironmentVariableKeySource>? = null)

    /**
     * Add a source that reads from a properties file. The path of properties file or keys of each
     * property can be customized by optional configuration block. The default is to read from
     * `signing.properties` in the current project (module) directory.
     */
    fun fromPropertiesFile(configure: Action<PropertiesFileKeySource>? = null)
}