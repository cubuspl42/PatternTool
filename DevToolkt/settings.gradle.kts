pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "DevToolkt"

include(
    "core",
    "coreSystemTests",
    "domApiCompatExtra",
    "math",
    "geometry",
    "pureDom",
    "reactive",
    "reactiveDom",
    "coreTestUtils",
    "geometryTestUtils",
)
