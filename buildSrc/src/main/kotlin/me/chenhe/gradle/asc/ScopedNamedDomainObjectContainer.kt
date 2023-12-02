package me.chenhe.gradle.asc

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.kotlin.dsl.GradleDsl

/**
 * This class exists for the sole purpose of marking the block as a GradleDsl thus hiding all
 * members provided by the outer script scope.
 *
 * To make this restriction effective, classes using this container must implement the
 * corresponding DSL method explicitly, rather than relying on Gradle's managed properties feature.
 * That is, don't declare the variable as an abstract.
 */
@GradleDsl
class ScopedNamedDomainObjectContainer<T>(delegate: NamedDomainObjectContainer<T>) :
    NamedDomainObjectContainer<T> by delegate