import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

group = "dev.toolkt"

kotlin {
    jvm {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        binaries {
            executable {
                mainClass.set("dev.toolkt.reactive.system_tests.MainKt")
            }
        }
    }

    js(IR) {
        browser {
            webpackTask {
                mainOutputFileName = "bundle.js"
            }
        }

        nodejs()

        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(project(":core"))
            implementation(project(":coreTestUtils"))
            implementation(project(":reactive"))
            implementation(project(":reactiveTestUtils"))
        }
    }
}
