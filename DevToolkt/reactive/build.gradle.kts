import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsSubTargetDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

group = "dev.toolkt"

repositories {
    mavenCentral()
}

kotlin {
    jvm()

    js(IR) {
        browser {
            testWithExtendedTimeout()
        }

        nodejs {
            testWithExtendedTimeout()
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))
        }

        commonTest.dependencies {
            implementation(project(":coreTestUtils"))
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

fun KotlinJsSubTargetDsl.testWithExtendedTimeout() {
    testTask {
        useMocha {
            timeout = "10s"
        }
    }
}
