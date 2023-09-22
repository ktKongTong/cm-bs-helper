import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
//    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
}

sqldelight {
    databases {
        create("BSHelperDatabase") {
            packageName.set("io.ktlab.bshelper.model")
        }
    }
}

kotlin {
    jvm()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                implementation(project(":bsmg"))
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization)
            }
        }
    }
}

//android {
//    compileSdk = 34
//    defaultConfig {
//        minSdk = 29
//    }
//
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_17
//        targetCompatibility = JavaVersion.VERSION_17
//    }
//
//    namespace = "io.ktlab.bshelper.model"
//}