import java.io.ByteArrayOutputStream

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.build.config)
    id("dev.icerock.mobile.multiplatform-resources")
}
sqldelight {
    databases {
        create("BSHelperDatabase") {
            packageName.set("io.ktlab.bshelper.model")
        }
    }
}
val commitShortId: String by lazy {
    val stdout = ByteArrayOutputStream()
    rootProject.exec {
        commandLine("git","rev-parse", "--short", "HEAD")
        standardOutput = stdout
    }
    stdout.toString().trim()
}

buildConfig {
    packageName("io.ktlab.bshelper")
    buildConfigField("APP_NAME", rootProject.name)
    buildConfigField("APP_VERSION", rootProject.version.toString())
    buildConfigField("BUILD_TIME", System.currentTimeMillis())
    buildConfigField("COMMIT_ID", commitShortId)
    buildConfigField("TOOL_API_URL", "https://kv-store-five.vercel.app")
    buildConfigField("BS_API_URL", "https://api.beatsaver.com")
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
                implementation("net.lingala.zip4j:zip4j:2.11.5")
                implementation(libs.kown)
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlin.reflect)
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
                implementation(libs.kmpalette.core)
                implementation(libs.material.kolor)
//                implementation(libs.kmpalette)
                // resources
                api(libs.moko.resources)
                api(libs.moko.resources.compose)

                // paging
                implementation(libs.cash.paging.common)
                implementation(libs.cash.paging.compose.common)

                // dataStore
                implementation(libs.androidx.annotation)
                implementation(libs.androidx.datastore.preferences.core)
                // ktor
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                // paging
                implementation(libs.cash.paging.common)
                implementation(libs.cash.paging.compose.common)

                // di
                api(libs.koin.core)
                implementation(libs.koin.core.coroutines)
                implementation(libs.koin.compose)
                // logger
                implementation(libs.kotlin.logging)
                // dataStore
                implementation(libs.androidx.datastore.preferences.core)
                // beatmap-io
                implementation(libs.beatmap.io)

                implementation(libs.sqldelight.coroutines.extensions)
                implementation(libs.okio)
                implementation(libs.semver)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.koin.androidx.compose)
                implementation(libs.slf4j.api)
                implementation(libs.logback.android)
                implementation(libs.sqldelight.android.driver)
                implementation(compose.preview)
                implementation(libs.ktor.client.okhttp)
            }
        }
        val jvmMain by getting {
            // see https://github.com/icerockdev/moko-resources/issues/477
            dependsOn(commonMain)
            dependencies {
                implementation(compose.desktop.common)
                implementation(compose.preview)
                // see https://github.com/JetBrains/compose-multiplatform/releases/tag/v1.1.1
                implementation(libs.kotlinx.coroutines.swing)

                implementation(libs.jlayer)
                implementation(libs.sqldelight.jvm.driver)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.kotlin.logging.jvm)
                implementation(libs.slf4j.api)
                implementation(libs.logback.classic)
            }
        }
    }
}

android {
    compileSdk = 34
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    // see https://github.com/icerockdev/moko-resources/issues/510#issuecomment-1700670810
    sourceSets["main"].java.srcDirs("build/generated/moko/androidMain/src")
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
