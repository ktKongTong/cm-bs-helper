
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.serialization)
    id("dev.icerock.mobile.multiplatform-resources")
}

kotlin {
    jvmToolchain(17)
    androidTarget("android")
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
                implementation(project(":utils"))
                implementation(project(":repository"))
                implementation(project(":platformService"))

                // kotlin & kotlinx
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlinx.datetime)

                // coroutines
                implementation(libs.kotlinx.coroutines.core)

                // compose
                api(compose.runtime)
                api(compose.preview)
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.materialIconsExtended)
                implementation(compose.material3)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)

                // third party compose
                implementation(libs.wsc.multiplatform)
                implementation(libs.mpfilepicker)
                implementation(libs.kamel)
                api(libs.precompose)
                api(libs.precompose.koin)
                api(libs.precompose.viewmodel)
                api(libs.precompose.molecule)

                // resources
                api(libs.moko.resources)
                api(libs.moko.resources.compose)

                // ktor
                implementation(libs.ktor.client.core)
                // paging
                implementation(libs.cash.paging.common)
                implementation(libs.cash.paging.compose.common)

                // di
                api(libs.koin.core)
                implementation(libs.koin.core.coroutines)
                implementation(libs.koin.compose)

                // dataStore
                implementation(libs.androidx.datastore.preferences.core)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.koin.androidx.compose)
            }
        }
        val jvmMain by getting {
            // see https://github.com/icerockdev/moko-resources/issues/477
            dependsOn(commonMain)
            dependencies {
                implementation(compose.desktop.common)
                // see https://github.com/JetBrains/compose-multiplatform/releases/tag/v1.1.1
                implementation(libs.kotlinx.coroutines.swing)
            }
        }
    }
}

android {
    compileSdk = 34

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
    defaultConfig {
        minSdk = 29
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    namespace = "io.ktlab.bshelper"
}

multiplatformResources {
    multiplatformResourcesPackage = "io.ktlab.bshelper"
}