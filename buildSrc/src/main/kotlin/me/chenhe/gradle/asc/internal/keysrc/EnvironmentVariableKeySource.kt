package me.chenhe.gradle.asc.internal.keysrc

import me.chenhe.gradle.asc.getEnv
import me.chenhe.gradle.asc.internal.SigningCert
import me.chenhe.gradle.asc.stringProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import java.io.File

/**
 * Read from environment variables.
 */
class EnvironmentVariableKeySource(objectFactory: ObjectFactory) : KeySource {
    /** The prefix that will be added to all keys of environment variable. */
    val prefix: Property<String> = objectFactory.stringProperty("ANDROID_SIGNING_")

    /** Environment varibale that indicates the key store file path. */
    val storeFileKey: Property<String> = objectFactory.stringProperty("STORE_PATH")
    val storePasswordKey: Property<String> = objectFactory.stringProperty("STORE_PWD")
    val keyAliasKey: Property<String> = objectFactory.stringProperty("KEY_ALIAS")
    val keyPasswordKey: Property<String> = objectFactory.stringProperty("KEY_PWD")

    private val _storeFileKey = storeFileKey.addPrefix()
    private val _storePasswordKey = storePasswordKey.addPrefix()
    private val _keyAliasKey = keyAliasKey.addPrefix()
    private val _keyPasswordKey = keyPasswordKey.addPrefix()

    private fun Property<String>.addPrefix(): Provider<String> {
        return this.zip(prefix) { key, p ->
            if (p.isNullOrEmpty()) {
                key
            } else {
                p + key
            }
        }
    }

    override fun readSigningCert(): SigningCert? {
        val path = getEnv(_storeFileKey) ?: return null
        return SigningCert(
            storeFile = File(path),
            storePwd = getEnv(_storePasswordKey) ?: return null,
            keyAlias = getEnv(_keyAliasKey) ?: return null,
            keyPwd = getEnv(_keyPasswordKey) ?: return null
        )
    }
}
