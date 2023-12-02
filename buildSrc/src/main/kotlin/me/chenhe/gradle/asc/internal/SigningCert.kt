package me.chenhe.gradle.asc.internal

import java.io.File

/**
 * Represents an android signing cert configuration.
 */
data class SigningCert(
    val storeFile: File,
    val storePwd: String,
    val keyAlias: String,
    val keyPwd: String,
)
