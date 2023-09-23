@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
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

                api(compose.runtime)
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(libs.androidx.paging)

                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.coroutines.core)

                implementation(libs.sqldelight.coroutines.extensions)
                implementation(libs.sqldelight.primitive.adapters)



                api(libs.koin.core)
                implementation(libs.koin.core.coroutines)
                implementation(libs.koin.compose)

                implementation(libs.androidx.annotation)
                implementation(libs.androidx.collection)
                implementation(libs.androidx.datastore.core.okio)
                implementation(libs.androidx.datastore.preferences.core)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(project(":Model"))
                implementation(project(":utils"))
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.appcompat)
                implementation(libs.sqldelight.android.driver)
                implementation(libs.ktor.client.okhttp)
            }
        }
        val desktopMain by getting {
            dependsOn(commonMain)
            dependencies{
                implementation(libs.ktor.client.okhttp)
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

    namespace = "io.ktlab.bshelper.service"
}