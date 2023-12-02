package me.chenhe.gradle.asc.internal

import me.chenhe.gradle.asc.ScopedNamedDomainObjectContainer
import me.chenhe.gradle.asc.api.AndroidSigningConfiguratorExtension
import me.chenhe.gradle.asc.api.SigningTargetsScope
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Nested

internal abstract class DefaultAndroidSigningConfiguratorExtension(
    override val project: Project, objectFactory: ObjectFactory
) : AndroidSigningConfiguratorExtension {

    override val keyProfiles: ScopedNamedDomainObjectContainer<DefaultKeyProfileScope> =
        ScopedNamedDomainObjectContainer(objectFactory.domainObjectContainer(DefaultKeyProfileScope::class.java))

    @get:Nested
    internal abstract val targetManager: SigningTargetManager

    override fun targets(signingTargetsConfiguration: Action<SigningTargetsScope>) {
        signingTargetsConfiguration.execute(targetManager)
    }
}