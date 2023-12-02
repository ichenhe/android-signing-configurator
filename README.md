# Android Signing Configurator

This is a Gradle plugin that can configure the android signing based on multiple data sources. Ideal
for unifying and simplifying the configuration of signing in different scenarios (e.g. GitHub
Actions).

**Features:**

- Supports multiple types of source: Environment Variable, Properties File etc..
- Supports specifying the builds to be signed based on *buildType* and *flavor*.

**Please be aware that the only thing this plugin does is configure the signing of AGP**, just as it
is configured manually via `android.signingConfigs{}` block. This plugin does not sign the app
directly.

# Quick start

This plugin was designed with convention-over-configuration, which means it can works correctly
without any configuration - **just apply the plugin**.

By default, this plugin:

- Tries to read signing cert configuration from the following sources in order:
  1. Properties File: `signing.properties`
  2. Environment Variable
- Signs all builds with build type `release`.

> For the names of the default supported environment variables or properties file format, please
> refer to the specific documentation.

The default behavior is equivalent to the following configuration:

```kotlin
androidSigning {
  keyProfiles {
    create("auto_detected") {
      fromPropertiesFile()
      fromEnvironmentVariable()
    }
  }
  targets {
    release("auto_detected") {
      abortOnError.set(false)
    }
  }
}
```

# Cert sources

`keyProfiles` block configures where to read signing key from. The name of each profile will be
passed to `android.signingConfigs`. More than one profile can be configured through
calling `create()`  multiple times.

Each key profile may have multiple sources that attempt to read the configuration in the order of
declaration.

```kotlin
keyProfiles {
  create("xxx") { /* ... */ }
  create("yyy") { /* ... */ }
}
```

## From environment variable

`fromEnvironmentVariable()` adds a source that read information from environment variables. For the
default supported var names, please see the example:

```kotlin
fromEnvironmentVariable {
  prefix = "ANDROID_SIGNING_"
  storeFileKey = "STORE_PATH"
  storePasswordKey = "STORE_PWD"
  keyAliasKey = "KEY_ALIAS"
  keyPasswordKey = "KEY_PWD"
}
```

To make this source valid, the following environment variables should exist:

```bash
ANDROID_SIGNING_STORE_PATH=/path/to/keyStore
ANDROID_SIGNING_STORE_PWD=1232456
ANDROID_SIGNING_KEY_ALIAS=myapp
ANDROID_SIGNING_KEY_PWD=654321
```

## From properties file

`fromPropertiesFile()` adds a source that read information from a specific properties file. For the
default values of the properties of this source, see the example:

```kotlin

val f = project.layout.projectDirectory.file("signing.properties")
keyProfiles {
  create("auto_detected") {
    fromPropertiesFile {
      propertiesFile.set(f)
      storeFileKey = "storePath"
      storePasswordKey = "storePassword"
      keyAliasKey = "keyAlias"
      keyPasswordKey = "keyPassword"
    }
  }
}
```

A valid properties file corresponding to the configuration above should look like this:

```properties
# ./app/signing.properties (app is the module's name)
storePath=/path/to/keyStore
storePassword=1232456
keyAliasKey=myapp
keyPassword=654321
```

# Target

`targets` block configures which builds need to be signed, and which key to sign them with. Multiple
targets can be configured, each using a different key profile.

```kotlin
  targets {
  // sign all builds with buildType 'release' using key profile 'mykey'
  release(keyProfile = "mykey") {
    abortOnError.set(true)
  }
}
```

Currently supports filtering builds based on **build type** and **flavor**.

```kotlin
taraget(buildType = "release", flavor = "normal") {
  keyProfile("xxx")
}
```

Some shortcut functions are also provided:

```kotlin
release(keyProfile = "auto_detected")

buildType(buildType = "release") {
  keyProfile("xxx")
}
```

**Regular expression** can also be used:

```kotlin
regex(buildType = "release".toRegex(), flavor = "store_.+".toRegex()) {
  keyProfile("xxx")
}
```

# Publish

> For plugin developer only

## Signing

Add signing information to Gradle's properties (`~/.gradle/gradle.properties`):

```properties
signing.keyId=xxxxxxxx
signing.password=xxx
signing.secretKeyRingFile=/path/to/keyring.gpg
```

[Signing Plugin Docs](https://docs.gradle.org/current/userguide/signing_plugin.html#sec:signatory_credentials)

## Gradle Portal

Add API keys of gradle plugin portal to Gradle's properties (`~/.gradle/gradle.properties`):

```properties
gradle.publish.key=xxxx
gradle.publish.secret=xxxx
```

