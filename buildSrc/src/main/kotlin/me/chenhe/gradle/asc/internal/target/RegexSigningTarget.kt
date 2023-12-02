package me.chenhe.gradle.asc.internal.target

import org.gradle.api.model.ObjectFactory

/**
 * Filter artifacts to be signed by regex matching.
 */
internal class RegexSigningTarget(
    objectFactory: ObjectFactory,
    private val buildType: Regex?,
    private val flavor: Regex?,
) : BaseSigningTarget(objectFactory) {

    override fun match(buildType: String?, flavor: String?): Boolean {
        return (this.buildType == null || this.buildType.matchEntire(buildType ?: "") != null) &&
                (this.flavor == null || this.flavor.matchEntire(flavor ?: "") != null)
    }
}