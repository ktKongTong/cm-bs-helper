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

                implementation("net.lingala.zip4j:zip4j:2.11.5")
                implementation(libs.kown)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.cash.paging.common)
                implementation(libs.cash.paging.compose.common)
                implementation(libs.okio)
                // logger
                implementation(libs.kotlin.logging)
                implementation(libs.slf4j.api)
                implementation(libs.beatmap.io)

                implementation(libs.androidx.annotation)
                implementation(libs.androidx.datastore.preferences.core)
                implementation(libs.sqldelight.coroutines.extensions)
            }
        }
    }
}
