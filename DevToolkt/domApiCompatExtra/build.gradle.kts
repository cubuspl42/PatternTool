plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

group = "dev.toolkt"

kotlin {
    js(IR) {
        browser {
        }
    }

    sourceSets {
        jsMain.dependencies {
            implementation(project(":core"))
            implementation(project(":jsApiCompat"))
        }
    }
}
