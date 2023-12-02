plugins {
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "1.2.1"
    signing
}

repositories {
    google()
    mavenCentral()
}

version = "0.1.0"
group = "me.chenhe"

dependencies {
    implementation("com.android.tools.build:gradle-api:8.2.0")
}

@Suppress("UnstableApiUsage")
gradlePlugin {
    website = "https://github.com/ichenhe/android-signing-configurator"
    vcsUrl = "https://github.com/ichenhe/android-signing-configurator"

    plugins {
        create("androidSigningConfiguratorPlugin") {
            id = "me.chenhe.gradle.android-signing-configurator"
            implementationClass = "me.chenhe.gradle.asc.AndroidSigningConfiguratorPlugin"
            displayName = "Android Signing Configurator"
            description =
                "Configure the android signing based on multiple  sources: env var, properties file..."
            tags.set(listOf("android", "signing"))
        }
    }
}
