package me.chenhe.gradle.asc.internal

import me.chenhe.gradle.asc.api.SigningTargetSpec
import me.chenhe.gradle.asc.api.SigningTargetsScope
import me.chenhe.gradle.asc.internal.target.BaseSigningTarget
import me.chenhe.gradle.asc.internal.target.RegexSigningTarget
import me.chenhe.gradle.asc.internal.target.StaticSigningTarget
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.kotlin.dsl.GradleDsl
import javax.inject.Inject

@GradleDsl
internal abstract class SigningTargetManager @Inject constructor(
    private val objectFactory: ObjectFactory,
) : SigningTargetsScope {
    internal abstract val targets: ListProperty<BaseSigningTarget>

    override fun release(keyProfile: String, configuration: Action<SigningTargetSpec>?) {
        buildType("release") {
            keyProfiles.add(keyProfile)
            configuration?.execute(this)
        }
    }

    override fun buildType(buildType: String, configuration: Action<SigningTargetSpec>) {
        val t = StaticSigningTarget(objectFactory, buildType = buildType, flavor = null)
        configuration.execute(t)
        targets.add(t)
    }

    override fun taraget(
        buildType: String?,
        flavor: String?,
        configuration: Action<SigningTargetSpec>
    ) {
        val t = StaticSigningTarget(objectFactory, buildType, flavor)
        configuration.executeAndAdd(t)
    }

    override fun regex(
        buildType: Regex?,
        flavor: Regex?,
        configuration: Action<SigningTargetSpec>
    ) {
        val t = RegexSigningTarget(objectFactory, buildType, flavor)
        configuration.executeAndAdd(t)
    }

    private fun Action<SigningTargetSpec>.executeAndAdd(t: BaseSigningTarget) {
        execute(t)
        targets.add(t)
    }

    internal fun eachTarget(action: (BaseSigningTarget) -> Unit) {
        targets.orNull?.forEach(action)
    }

    /**
     * Check whether given artifact should be signed with current cert/key.
     */
    internal fun matchArtifact(buildType: String?, flavor: String?): SigningTargetSpec? {
        targets.orNull?.forEach { target ->
            if (target.match(buildType, flavor)) return target
        }
        return null
    }
}