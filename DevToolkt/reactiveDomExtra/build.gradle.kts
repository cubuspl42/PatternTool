plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

group = "dev.toolkt"

kotlin {
    js(IR) {
        browser()
    }

    sourceSets {
        jsMain.dependencies {
            implementation(project(":domApiCompatExtra"))
            implementation(project(":core"))
            implementation(project(":math"))
            implementation(project(":geometry"))
            implementation(project(":pureDom"))
            implementation(project(":reactive"))
            implementation(project(":reactiveDom"))
        }
    }
}
