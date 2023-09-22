@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
//    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    jvm()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization)
            }
        }
    }
}