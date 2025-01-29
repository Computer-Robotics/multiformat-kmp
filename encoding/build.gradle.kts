import android.databinding.tool.ext.capitalizeUS
import java.net.URI
import java.time.Year
import java.util.Base64
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.dokka.versioning.VersioningConfiguration
import org.jetbrains.dokka.versioning.VersioningPlugin
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.dokka")
    id("org.jlleitschuh.gradle.ktlint")
    id("maven-publish")
    id("signing")
}

val currentModuleName: String = "encoding"

/**
 * The `javadocJar` variable is used to register a `Jar` task to generate a Javadoc JAR file.
 * The Javadoc JAR file is created with the classifier "javadoc" and it includes the HTML documentation generated
 * by the `dokkaHtml` task.
 */
val javadocJar by tasks.registering(Jar::class) {
    from(tasks.named("dokkaGenerate"))
    archiveClassifier.set("javadoc")
}

kotlin {
    androidTarget {
        publishLibraryVariants("release")
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_20)
                }
            }
        }
    }
    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()
    jvm {
        publishing {
            withSourcesJar()
            publications {
                withType<MavenPublication> {
                    artifact(javadocJar)
                }
            }
        }
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_20)
                }
            }
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
        macosArm64(),
        macosX64(),
        watchosArm32(),
        watchosArm64(),
        watchosX64(),
        watchosSimulatorArm64(),
        tvosArm64(),
        tvosX64(),
        tvosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = currentModuleName
        }
    }
    linuxX64()
    linuxArm64()
    mingwX64()
    js {
        browser {

        }
        nodejs {

        }
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
        d8()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmWasi {
        nodejs()
    }
    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(kotlin("stdlib"))
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
            compilations.all {
                compileTaskProvider.configure {
                    compilerOptions {
                        // Enable the export of KDoc (Experimental feature) to Generated Native targets (Apple, Linux, etc.)
                        this.freeCompilerArgs.add("-Xexport-kdoc")
                    }
                }
            }
        }
    }

    targets.all {
        compilations.all {
            compileTaskProvider {
                compilerOptions {
                    // Disabling the expect/actual classes usage warning
                    freeCompilerArgs.add("-Xexpect-actual-classes")
                }
            }
        }
    }
}

// Android
android {
    compileSdk = 34
    namespace = "tech.compubotics.kmp.encoding"
    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_20
        targetCompatibility = JavaVersion.VERSION_20
    }
    publishing {
        multipleVariants {
            withSourcesJar()
            withJavadocJar()
            allVariants()
        }
    }
}

// Documentation
tasks.withType<DokkaTask>().configureEach {
    moduleName.set(currentModuleName.capitalizeUS())
    moduleVersion.set(rootProject.version.toString())
    description = "Encodings in Kotlin Multiplatform (KMP)"
    pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
        customAssets = listOf(rootDir.resolve("Logo.png"))
        footerMessage = "(c) ${Year.now().value} Computer Robotics Copyright"
    }
    pluginConfiguration<VersioningPlugin, VersioningConfiguration> {
        version = rootProject.version.toString()
        renderVersionsNavigationOnAllPages = true
    }
    dokkaSourceSets.configureEach {
        jdkVersion.set(20)
        languageVersion.set("2.1.0")
        apiVersion.set("2.0")
        sourceLink {
            localDirectory.set(projectDir.resolve("src"))
            remoteUrl.set(URI.create("https://github.com/Computer-Robotics/multiformats-kmp/tree/main/src").toURL())
            remoteLineSuffix.set("#L")
        }
        reportUndocumented.set(true)
    }
}

publishing {
    publications {
        withType<MavenPublication> {
            groupId = rootProject.group.toString()
            artifactId = currentModuleName
            version = rootProject.version.toString()
            pom {
                name.set("$currentModuleName KMP")
                description.set("encoding implementation in Kotlin Multiplatform (KMP)")
                url.set("https://github.com/Computer-Robotics/multiformats-kmp/tree/main/src")
                organization {
                    name.set("Computer Robotics")
                    url.set("https://compubotics.tech/")
                }
                issueManagement {
                    system.set("Github")
                    url.set("https://github.com/Computer-Robotics/multiformats-kmp")
                }
                licenses {
                    license {
                        name.set("GNU GPLv3")
                        url.set("https://github.com/Computer-Robotics/multiformats-kmp/blob/main/LICENSE")
                    }
                }
                scm {
                    connection.set("scm:git:git://Computer-Robotics/multiformats-kmp.git")
                    developerConnection.set("scm:git:ssh://Computer-Robotics/multiformats-kmp.git")
                    url.set("https://github.com/Computer-Robotics/multiformats-kmp")
                }
                developers {
                    developer {
                        id.set("hamada147")
                        name.set("Ahmed Moussa")
                        email.set("ahmed.moussa@compubotics.tech")
                        organization.set("Computer Robotics")
                        roles.add("developer")
                        organizationUrl.set("https://compubotics.tech/")
                        timezone.set("Africa/Cairo")
                    }
                }
            }
            signing {
                val base64EncodedAsciiArmoredSigningKey: String = System.getenv("CR_BASE64_ARMORED_GPG_SIGNING_KEY_MAVEN") ?: ""
                val signingKeyPassword: String = System.getenv("CR_SIGNING_KEY_PASSWORD") ?: ""
                useInMemoryPgpKeys(
                    String(
                        Base64.getDecoder().decode(base64EncodedAsciiArmoredSigningKey.toByteArray())
                    ),
                    signingKeyPassword
                )
                sign(this@withType)
            }
        }
        repositories {
            // GitHub Maven Repo
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/Computer-Robotics/multiformats-kmp")
                credentials {
                    username = System.getenv("GITHUB_USERNAME")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}
