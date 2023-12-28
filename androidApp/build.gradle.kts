import java.io.ByteArrayOutputStream

plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.compose)
}
val commitShortId: String by lazy {
    val stdout = ByteArrayOutputStream()
    rootProject.exec {
        commandLine("git","rev-parse", "--short", "HEAD")
        standardOutput = stdout
    }
    stdout.toString().trim()
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
        versionCode = 5
        versionName = rootProject.version.toString()
        setProperty("archivesBaseName", "${rootProject.name}_v${versionName}_$commitShortId")
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
    implementation(libs.kotlin.logging)
    implementation(libs.androidx.activity.ktx)
//    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.12")
}
