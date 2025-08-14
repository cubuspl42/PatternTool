plugins {
    alias(libs.plugins.kotlin.multiplatform)
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
            implementation("dev.toolkt:core")
            implementation("dev.toolkt:math")
            implementation("dev.toolkt:geometry")
            implementation("dev.toolkt:pureDom")
            implementation("dev.toolkt:reactive")
            implementation("dev.toolkt:reactiveDom")
            implementation("dev.toolkt:domApiCompatExtra")
            implementation("dev.toolkt:reactiveDom")
            implementation("dev.toolkt:reactiveDomExtra")
            implementation(npm("path-data-polyfill", "1.0.10"))
        }
    }
}
