plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.kotlinx.benchmark)
    alias(libs.plugins.shadow)
    alias(libs.plugins.kotlinx.serialization)
}

repositories {
    mavenCentral()
}

kotlin {
    js(IR) {
        browser {
            webpackTask {
                mainOutputFileName = "bundle.js"
            }
        }

        binaries.executable()
    }

    sourceSets {
        jsMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
            implementation("dev.toolkt:core")
            implementation("dev.toolkt:math")
            implementation("dev.toolkt:geometry")
            implementation("dev.toolkt:pureDom")
            implementation("dev.toolkt:reactive")
            implementation("dev.toolkt:domApiCompatExtra")
            implementation("dev.toolkt:reactiveDom")
            implementation(npm("three", "0.178.0"))
            implementation(npm("path-data-polyfill", "1.0.10"))
        }
    }

    compilerOptions {
        freeCompilerArgs.addAll(
            listOf(
                "-Xconsistent-data-class-copy-visibility",
            ),
        )
    }
}
