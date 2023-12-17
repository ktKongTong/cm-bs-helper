import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
}

kotlin {
    jvmToolchain(17)
    jvm()
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(project(":common"))
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "CM-BS-Helper"
            packageVersion = "1.0.0"
            // see https://github.com/cashapp/sqldelight/issues/2151
            // https://github.com/JetBrains/compose-multiplatform/issues/381
            // for sqldelight and ktor
            modules("java.sql", "java.naming")

            windows {
                menu = true
                // see https://wixtoolset.org/documentation/manual/v3/howtos/general/generate_guids.html
                upgradeUuid = "6565BEAD-713A-4DE7-A469-6B10FC4A6861"
            }
        }

        buildTypes.release {
            proguard {
                configurationFiles.from(project.file("compose-desktop.pro"))
            }
        }
    }
}
