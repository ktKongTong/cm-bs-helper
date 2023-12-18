@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.jetbrains.compose)
}

kotlin {
    jvmToolchain(17)
    androidTarget("android")
    jvm("desktop") {
        compilations.all {
            kotlinOptions {
                jvmTarget = JavaVersion.VERSION_17.toString()
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {

                implementation(kotlin("test"))
                implementation(project(":model"))
                implementation(project(":utils"))
                api(compose.runtime)
                api(compose.ui)
                // kotlin & kotlinx
                implementation(libs.kotlin.reflect)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.coroutines.core)
                // paging
                implementation(libs.cash.paging.common)
                implementation(libs.cash.paging.compose.common)

                // dataStore
                implementation(libs.androidx.annotation)
                implementation(libs.androidx.datastore.preferences.core)
                // logger
                implementation(libs.kotlin.logging)
                // ktor
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.core.ktx)
                implementation(libs.logback.android)
                implementation(libs.sqldelight.android.driver)
                implementation(libs.ktor.client.okhttp)
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(libs.ktor.client.okhttp)
                implementation(libs.logback.classic)
                implementation(libs.kotlin.logging.jvm)
                implementation(libs.sqldelight.jvm.driver)
            }
        }
    }
}

android {
    compileSdk = 34
    defaultConfig {
        minSdk = 29
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
    namespace = "io.ktlab.bshelper.service"
}
