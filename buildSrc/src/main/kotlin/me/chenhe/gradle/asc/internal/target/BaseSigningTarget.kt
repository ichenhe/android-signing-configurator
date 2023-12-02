package me.chenhe.gradle.asc.internal.target

import me.chenhe.gradle.asc.api.SigningTargetSpec
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

internal abstract class BaseSigningTarget @Inject constructor(objectFactory: ObjectFactory) :
    SigningTargetSpec {

    abstract fun match(buildType: String?, flavor: String?): Boolean

    override val abortOnError: Property<Boolean> = objectFactory.property(Boolean::class.java)

    override val keyProfiles: ListProperty<String> =
        objectFactory.listProperty(String::class.java)

    override fun keyProfile(name: String) {
        keyProfiles.add(name)
    }
}