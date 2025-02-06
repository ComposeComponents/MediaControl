import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import com.vanniktech.maven.publish.SonatypeHost
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.JavadocJar
import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.cocoapods)
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.dokka)
    alias(libs.plugins.maven.publish)
}

val desc = "A library to provide customisable media control components"

kotlin {
    val currentOs = OperatingSystem.current()
    val strictBuild: Boolean by project.extra {
        (project.findProperty("build.strictPlatform") as? String)?.toBooleanStrictOrNull() ?: false
    }

    if (!strictBuild || currentOs.isLinux) {
        androidTarget {
            publishLibraryVariants("release")
            compilations.all {
                compileTaskProvider.configure {
                    compilerOptions {
                        jvmTarget.set(JvmTarget.JVM_11)
                    }
                }
            }
        }
        jvm()

        js(IR) {
            browser()
            useCommonJs()
            generateTypeScriptDefinitions()
        }
        @OptIn(ExperimentalWasmDsl::class)
        wasmJs()
    }
    if (!strictBuild || currentOs.isMacOsX) {
        iosX64()
        iosArm64()
        iosSimulatorArm64()
        macosX64()
        macosArm64()
    }


    cocoapods {
        summary = desc
        homepage = findProperty("pom.url") as String
        version = findProperty("version") as String
        ios.deploymentTarget = "16.0"
        framework {
            baseName = "mediacontrol"
            isStatic = true
        }
    }

    androidTarget {
        publishLibraryVariants("release")
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
        }
    }
}

description = desc
group = "cl.emilym.compose"

android {
    compileSdk = 35

    defaultConfig {
        namespace = "cl.emilym.compose.mediacontrol"
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    coordinates("cl.emilym.compose", "mediacontrol", findProperty("version") as String)

    pom {
        name.set("MediaControl")
        description.set(desc)
        url.set(findProperty("pom.url") as String)
        licenses {
            license {
                name.set(findProperty("pom.license.name") as String)
                url.set(findProperty("pom.license.url") as String)
            }
        }
        developers {
            developer {
                name.set(findProperty("pom.developer.name") as String)
                email.set(findProperty("pom.developer.email") as String)
            }
        }
        scm {
            connection.set(findProperty("pom.scm.connection") as String)
            developerConnection.set(findProperty("pom.scm.developerConnection") as String)
            url.set(findProperty("pom.scm.url") as String)
        }
    }

    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Dokka("dokkaHtml"),
            sourcesJar = true
        )
    )

    signAllPublications()
}