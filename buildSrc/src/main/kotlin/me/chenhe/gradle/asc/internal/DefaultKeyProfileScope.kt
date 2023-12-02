package me.chenhe.gradle.asc.internal

import me.chenhe.gradle.asc.api.KeyProfileScope
import me.chenhe.gradle.asc.internal.keysrc.EnvironmentVariableKeySource
import me.chenhe.gradle.asc.internal.keysrc.KeySource
import me.chenhe.gradle.asc.internal.keysrc.PropertiesFileKeySource
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.kotlin.dsl.invoke
import javax.inject.Inject

internal abstract class DefaultKeyProfileScope @Inject constructor(
    private val name: String,
    private val objectFactory: ObjectFactory,
) : KeyProfileScope {
    protected abstract val keySources: ListProperty<KeySource>

    override fun getName(): String = name

    private fun <T : KeySource> Action<T>?.configureAndAdd(keySource: T) {
        this?.invoke(keySource)
        keySources.add(keySource)
    }

    override fun fromEnvironmentVariable(configure: Action<EnvironmentVariableKeySource>?) {
        configure.configureAndAdd(EnvironmentVariableKeySource(objectFactory))
    }

    override fun fromPropertiesFile(configure: Action<PropertiesFileKeySource>?) {
        configure.configureAndAdd(PropertiesFileKeySource(objectFactory))
    }

    /**
     * Tries to read signing cert properties from each of [keySources] and returns the first
     * successful one. This method doesn't check the validity of the properties (e.g. existence of
     * the certificate file).
     */
    internal fun findFirstKeyConfiguration(): SigningCert? {
        keySources.orNull?.forEach { keySource ->
            keySource.readSigningCert()?.also { return it }
        }
        return null
    }

    internal fun eachKeySource(action: (KeySource) -> Unit) {
        keySources.orNull?.forEach(action)
    }
}