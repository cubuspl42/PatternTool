plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

group = "dev.toolkt"

kotlin {
    js(IR) {
        browser {}
    }

    sourceSets {
        jsMain.dependencies {
            implementation(project(":jsApiCompat"))
            implementation(npm("path-data-polyfill", "1.0.10"))
        }
    }
}
