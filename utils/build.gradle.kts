@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvm{}
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.androidx.paging)
                implementation(libs.sqldelight.coroutines.extensions)
                implementation(libs.sqldelight.primitive.adapters)
                implementation(libs.precompose)
                implementation(libs.precompose.viewmodel)
                api(libs.koin.core)
                implementation(libs.koin.core.coroutines)
                implementation(libs.koin.compose)
                implementation(project(":Model"))
                implementation(project(":bsmg"))
                implementation(libs.okio)
                implementation(libs.beatmap.io)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.kotlinx.datetime)
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

    namespace = "io.ktlab.bshelper.utils"
}