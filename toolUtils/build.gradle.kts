plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

group = "dev.toolkt"

repositories {
    mavenCentral()
}

kotlin {
    js(IR) {
        browser()
    }

    sourceSets {
        jsMain.dependencies {
            implementation(npm("path-data-polyfill", "1.0.10"))

            implementation("dev.toolkt:domApiCompatExtra")
            implementation("dev.toolkt:core")
            implementation("dev.toolkt:geometry")
            implementation("dev.toolkt:math")
            implementation("dev.toolkt:pureDom")
            implementation("dev.toolkt:reactive")
            implementation("dev.toolkt:reactiveDom")
        }
    }
}
