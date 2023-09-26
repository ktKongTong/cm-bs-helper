
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
                api(compose.runtime)
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.materialIconsExtended)
                implementation(compose.material3)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)

                implementation(libs.wsc.multiplatform)
                implementation(libs.mpfilepicker)

                implementation(libs.androidx.annotation)
                implementation(libs.androidx.collection)
                implementation(libs.androidx.datastore.core.okio)
                implementation(libs.androidx.datastore.preferences.core)

                api(libs.moko.resources)
                api(libs.moko.resources.compose)

                api(libs.precompose)
                api(libs.precompose.koin)
                api(libs.precompose.viewmodel)
                api(libs.precompose.molecule)

                api(libs.koin.core)
                implementation(libs.koin.core.coroutines)
                implementation(libs.koin.compose)
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.coroutines.core)

                implementation(libs.kamel)
                implementation(libs.ktor.client.core)

                implementation(project(":model"))
                implementation(project(":utils"))
                implementation(project(":repository"))
                implementation(project(":platformService"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.koin.android)
                implementation(libs.koin.androidx.compose)
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.appcompat)
            }
        }
        val jvmMain by getting {
            // see https://github.com/icerockdev/moko-resources/issues/477
            dependsOn(commonMain)
            dependencies {
                api(libs.moko.resources)
                api(libs.moko.resources.compose)
                implementation(compose.desktop.common)
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