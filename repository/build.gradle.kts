@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvmToolchain(17)
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = JavaVersion.VERSION_17.toString()
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":model"))
                implementation(project(":bsmg"))
                implementation(project(":platformService"))
                implementation(project(":utils"))

                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.cash.paging.common)
                implementation(libs.cash.paging.compose.common)
                implementation(libs.okio)

                implementation(libs.beatmap.io)

                implementation(libs.androidx.annotation)
                implementation(libs.androidx.datastore.preferences.core)

                implementation(libs.sqldelight.coroutines.extensions)
            }
        }
    }
}