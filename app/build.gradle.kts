import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

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
    jvm()

    js(IR) {
        browser()
        nodejs()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.benchmark.runtime)
            implementation(libs.kotlinx.serialization.json)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        jvmMain.dependencies {
            implementation(libs.batik.anim)
            implementation(libs.batik.svg.dom)
            implementation(libs.batik.css)
            implementation(libs.fop)
            implementation(libs.clikt)
        }

        jvmTest.dependencies {
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
