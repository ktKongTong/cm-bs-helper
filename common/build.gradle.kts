
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.kotlin.serialization)
    id("dev.icerock.mobile.multiplatform-resources")
}

sqldelight {
    databases {
        create("BSHelperDatabase") {
            packageName.set("io.ktlab.bshelper.model")
        }
    }
}
kotlin {
    androidTarget("android")
    jvm {

//        compilations.all {
//            kotlinOptions {
//                jvmTarget = JavaVersion.VERSION_17.toString()
//            }
//        }
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

                implementation(libs.androidx.paging)

                api(libs.moko.resources)
                api(libs.moko.resources.compose)

//                api(libs.moko.mvvm)
//                api(libs.moko.flow.compose)
//                implementation(libs.voyager.navigator)
//                implementation(libs.kodein.di)
//                implementation(libs.kodein.di.compose)

                api(libs.precompose)
                api(libs.precompose.koin)
                api(libs.precompose.viewmodel)
                api(libs.precompose.molecule)

                api(libs.koin.core)
                implementation(libs.koin.core.coroutines)
                implementation(libs.koin.compose)


//                implementation(libs.cash.paging.common)
//                implementation(libs.androidx.paging.compose)
//                implementation(libs.sqldelight.paging3.extensions)
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.coroutines.core)

                implementation(libs.kamel)
//                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.core)
//                implementation(libs.ktor.client.content.negotiation)

                implementation(project(":Model"))
                implementation(project(":utils"))
                implementation(project(":repository"))
                implementation(project(":SysService"))
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.koin.android)
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.appcompat)
                implementation(libs.sqldelight.android.driver)
//                implementation(libs.ktor.client.okhttp)
            }
        }
        val jvmMain by getting {
            dependsOn(commonMain)
            dependencies {
//                implementation(libs.ktor.client.okhttp)
                implementation(compose.desktop.common)
//                implementation(libs.sqldelight.jvm.driver)
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