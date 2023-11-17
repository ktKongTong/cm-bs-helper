package io.ktlab.kown

import io.ktor.client.*
import io.ktor.client.engine.cio.*

actual fun httpClient(clientConfig: HttpClientConfig<*>.() -> Unit) = HttpClient(CIO) {
    clientConfig(this)
    engine {
        maxConnectionsCount = 1000
        endpoint {
            maxConnectionsPerRoute = 100
            pipelineMaxSize = 20
//            keepAliveTime = 5000
//            connectTimeout = 5000
            requestTimeout = 0
            connectAttempts = 5
        }
    }
}