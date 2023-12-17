plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.compose)
}

android {
    namespace = "io.ktlab.bshelper"
    compileSdk = 34

    defaultConfig {
//        compileSdkPreview = "UpsideDownCake"
        minSdk = 29
        versionCode = 1
        versionName = "0.0.1"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    packaging {
        resources {
            excludes.add("META-INF/AL2.0")
            excludes.add("mozilla/public-suffix-list.txt")
            excludes.add("META-INF/LGPL2.1")
            excludes.add("META-INF/DEPENDENCIES")
            excludes.add("META-INF/gradle/incremental.annotation.processors")
            excludes.add("META-INF/versions/9/previous-compilation-data.bin")
        }
    }
    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
    implementation(project(":common"))
    implementation(libs.koin.androidx.compose)
}
