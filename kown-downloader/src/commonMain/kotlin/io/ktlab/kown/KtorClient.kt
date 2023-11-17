package io.ktlab.kown

import io.ktor.client.*
import io.ktor.client.plugins.*


fun ktorClient(config: KownConfig) :HttpClient = httpClient{
    followRedirects = true
    expectSuccess = false
    if (config.retryEnabled){
        install(HttpRequestRetry){
            retryOnException(config.retryCount, retryOnTimeout = true)
        }
    }
}

expect fun httpClient(clientConfig: HttpClientConfig<*>.() -> Unit = {}): HttpClient