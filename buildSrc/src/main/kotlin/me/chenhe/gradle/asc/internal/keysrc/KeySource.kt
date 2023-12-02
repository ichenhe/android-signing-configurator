package me.chenhe.gradle.asc.internal.keysrc

import me.chenhe.gradle.asc.internal.SigningCert

/**
 * Represents a source from which the signing cert/key properties are read.
 */
interface KeySource {
    fun readSigningCert(): SigningCert?
}