import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.kotlinx.benchmark)
    alias(libs.plugins.shadow)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    jvm()

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
            implementation(libs.kotlinx.benchmark.runtime)
            implementation(libs.kotlinx.serialization.json)
            implementation("dev.toolkt:core")
            implementation("dev.toolkt:math")
            implementation("dev.toolkt:geometry")
            implementation("dev.toolkt:pureDom")
            implementation("dev.toolkt:reactive")
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation("dev.toolkt:coreTestUtils")
        }

        jvmMain.dependencies {
            implementation(libs.clikt)
        }

        jsMain.dependencies {
            implementation("dev.toolkt:domApiCompatExtra")
            implementation("dev.toolkt:reactiveDom")
            implementation("dev.toolkt:reactiveDomExtra")
            implementation(npm("path-data-polyfill", "1.0.10"))
        }
    }
}

tasks.shadowJar {
    mergeServiceFiles()
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("") // Ensures the output JAR has no additional classifier
    manifest {
        attributes["Main-Class"] = "diy.lingerie.pattern_tool.MainKt"
    }
}

benchmark {
    targets {
        register("jvm")
        register("js")
    }

    configurations {
        named("main") {
            warmups = 2
            iterations = 4
            iterationTime = 1
            iterationTimeUnit = "s"
        }
    }
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}
