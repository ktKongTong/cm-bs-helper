package io.ktlab.bshelper.platform

enum class Platform {
    JVM,
    ANDROID,
}

expect fun currentPlatform(): Platform