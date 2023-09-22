package io.ktlab.bshelper.service

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.observer.ResponseObserver


actual class HttpClientModuleProvider actual constructor(
    private val defaultConfigBlock: HttpClientConfig<*>.() -> Unit
) {
    actual fun getPlatformSpecificClient() = HttpClient {
        installFeatures(this@HttpClient)
        defaultConfigBlock()
    }

    private fun installFeatures(config: HttpClientConfig<*>) = with(config) {
        install(ResponseObserver) {
            onResponse { response ->
                println("HTTP status Desktop: ${response.status}")
            }
        }
    }
}