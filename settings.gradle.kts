pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        mavenCentral()
    }

}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "cm-bs-helper"

include(
    ":common",
    ":android",
    ":desktop",
    ":bsmg",
    ":SysService",
    ":Model",
    ":repository",
    ":utils"
)
include("common")
include("android")
include("desktop")
include(":SysService")
include(":Model")
include(":repository")
include(":utils")
include(":bsmg")
