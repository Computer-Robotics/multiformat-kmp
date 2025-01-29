val publishedMavenId: String = "tech.compubotics"

plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("multiplatform") version "2.1.0" apply false
    id("com.android.library") version "8.7.3" apply false
    id("org.jetbrains.dokka") version "2.0.0"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.2"
    id("maven-publish")
    id("signing")
    id("org.jetbrains.kotlinx.kover") version "0.9.1"
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0-rc-1"
}

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.0")
        classpath("com.android.tools.build:gradle:8.7.3")
        classpath("org.jetbrains.dokka:dokka-base:2.0.0")
        classpath("org.jetbrains.dokka:versioning-plugin:2.0.0")
    }
}

group = publishedMavenId
version = "1.0.0"

allprojects {
    group = publishedMavenId
    version = rootProject.version

    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

kotlin {
    jvmToolchain(20)
}

subprojects {
    apply(plugin = "org.gradle.maven-publish")
    apply(plugin = "org.gradle.signing")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    ktlint {
        verbose.set(true)
        outputToConsole.set(true)
    }
    apply(plugin = "org.jetbrains.kotlinx.kover")
    apply(plugin = "org.jetbrains.dokka")
    dependencies {
        dokkaPlugin("org.jetbrains.dokka:dokka-base:2.0.0")
        dokkaPlugin("org.jetbrains.dokka:versioning-plugin:2.0.0")
    }
}

dependencies {
    // Code Documentation
    dokka(project(":encoding"))

    // Code Coverage
    kover(project(":encoding"))
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://oss.sonatype.org/content/repositories/snapshots/"))
            username.set(System.getenv("CR_SONATYPE_USERNAME"))
            password.set(System.getenv("CR_SONATYPE_PASSWORD"))
        }
    }
}
