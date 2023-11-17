@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
}
sqldelight {
    databases {
        create("KownDatabase") {
            packageName.set("io.ktlab.kown.model")
        }
    }
}
kotlin{
    androidTarget("android")
    jvm() {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.core)

                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.io.core)
                implementation(libs.okio)

                implementation(libs.kotlin.logging)

                implementation(libs.sqldelight.runtime)
                implementation(libs.sqldelight.coroutines.extensions)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.ktor.client.okhttp)
            }
        }
        val jvmMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.ktor.client.cio)
            }
        }
    }
}

android {
    namespace = "io.ktlab.bshelper.downloader"
    compileSdk = 34
    defaultConfig {
        minSdk = 29
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin{
        jvmToolchain(17)
    }
}
dependencies {
    implementation("io.ktor:ktor-client-core-jvm:2.3.4")
    implementation("io.ktor:ktor-client-apache:2.3.4")
}
