package me.chenhe.gradle.asc.internal.keysrc

import me.chenhe.gradle.asc.internal.SigningCert
import me.chenhe.gradle.asc.stringProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import java.io.File
import java.util.Properties

/**
 * Read from properties file.
 */
class PropertiesFileKeySource(objectFactory: ObjectFactory) : KeySource {
    val propertiesFile: RegularFileProperty = objectFactory.fileProperty()

    val storeFileKey: Property<String> = objectFactory.stringProperty("storePath")
    val storePasswordKey: Property<String> = objectFactory.stringProperty("storePassword")
    val keyAliasKey: Property<String> = objectFactory.stringProperty("keyAlias")
    val keyPasswordKey: Property<String> = objectFactory.stringProperty("keyPassword")

    override fun readSigningCert(): SigningCert? {
        val f = propertiesFile.orNull?.asFile ?: return null
        if (!f.isFile) return null
        val props = Properties()
        f.inputStream().use { props.load(it) }

        val path = props.getProperty(storeFileKey) ?: return null
        return SigningCert(
            storeFile = File(path),
            storePwd = props.getProperty(storePasswordKey) ?: return null,
            keyAlias = props.getProperty(keyAliasKey) ?: return null,
            keyPwd = props.getProperty(keyPasswordKey) ?: return null
        )
    }

    private fun Properties.getProperty(key: Property<String>): String? =
        key.orNull?.let { getProperty(it) }
}