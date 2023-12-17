@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvmToolchain(17)
    jvm {}
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(project(":model"))
                implementation(project(":bsmg"))
                implementation("net.lingala.zip4j:zip4j:2.11.5")
                // logger
                implementation(libs.kotlin.logging)
                implementation(libs.okio)
                implementation(libs.beatmap.io)
                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlinx.datetime)
            }
        }
    }
}
