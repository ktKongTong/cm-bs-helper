plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.compose)
}

android {
    namespace = "io.ktlab.bshelper"
    compileSdk = 34
    buildTypes {
        getByName("debug") {
            // code shrink enable will cause unknown issue, filter changed, but query result is the same
            // so disable now
//            isDebuggable=false
//            isMinifyEnabled = true
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
        }
    }
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
    implementation(project(":shared"))
    implementation(libs.koin.androidx.compose)
    implementation(libs.androidx.activity.compose)
}
