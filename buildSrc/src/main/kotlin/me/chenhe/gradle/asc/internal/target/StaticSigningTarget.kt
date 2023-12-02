package me.chenhe.gradle.asc.internal.target

import org.gradle.api.model.ObjectFactory

/**
 * Filter artifacts to be signed by verbatim matching.
 */
internal class StaticSigningTarget(
    objectFactory: ObjectFactory,
    private val buildType: String?,
    private val flavor: String?,
) : BaseSigningTarget(objectFactory) {

    override fun match(buildType: String?, flavor: String?): Boolean {
        return (this.buildType == null || this.buildType == buildType) &&
                (this.flavor == null || this.flavor == flavor)
    }
}