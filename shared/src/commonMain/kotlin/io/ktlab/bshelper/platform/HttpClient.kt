package io.ktlab.bshelper.platform

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

expect class HttpClientModuleProvider(
    defaultConfigBlock: HttpClientConfig<*>.() -> Unit,
) {
    fun getPlatformSpecificClient(): HttpClient
}

class HttpClientModuleProviderBase {
    fun configureClient(): HttpClient {
        return HttpClientModuleProvider {
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 5)
            }
            install(ContentNegotiation) {
                json(
                    Json {
//                    prettyPrint = true
                        ignoreUnknownKeys = true
                    },
                )
            }
        }.getPlatformSpecificClient()
    }

    fun configureToolAPIClient(): HttpClient {
        return HttpClientModuleProvider {
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 3)
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 3000
                connectTimeoutMillis = 3000
            }
            install(ContentNegotiation) {
                json(
                    Json {
//                    prettyPrint = true
                        ignoreUnknownKeys = true
                    },
                )
            }
        }.getPlatformSpecificClient()
    }
}