import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

val kotlinMultiplatformPluginId = "org.jetbrains.kotlin.multiplatform"

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
}

subprojects {
    repositories {
        mavenCentral()

        plugins.withId(kotlinMultiplatformPluginId) {
            configure<KotlinMultiplatformExtension> {
                compilerOptions {
                    freeCompilerArgs.addAll(
                        listOf(
                            "-Xconsistent-data-class-copy-visibility",
                        ),
                    )
                }
            }
        }
    }
}
