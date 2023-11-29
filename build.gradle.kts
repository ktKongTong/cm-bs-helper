
plugins {
    alias(libs.plugins.gradle.versions)
    alias(libs.plugins.version.catalog.update)

    alias(libs.plugins.kotlin.jvm)  apply false
    alias(libs.plugins.kotlin.multiplatform)  apply false
    alias(libs.plugins.kotlin.android)  apply false

    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.compose) apply false
}


allprojects {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://jitpack.io")
        maven("https://artifactory.kirkstall.top-cat.me")
    }
}

buildscript{
    dependencies {
        classpath("dev.icerock.moko:resources-generator:0.23.0")
    }
}

task("clean") {
    group = "build"
    description = "Deletes the build directory"
    doLast {
//        project.delete(project.buildDir)
        allprojects.forEach{
            it.delete(it.buildDir)
        }
    }
}

//apply("${project.rootDir}/buildscripts/toml-updater-config.gradle")

group = "io.ktlab"
version = "1.0-SNAPSHOT"