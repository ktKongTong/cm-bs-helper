package io.ktlab.bshelper.service

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

expect class HttpClientModuleProvider(
    defaultConfigBlock: HttpClientConfig<*>.() -> Unit
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
                json(Json {
                    prettyPrint = true
//                        isLenient = true
//                    useAlternativeNames = false
                    ignoreUnknownKeys = true
                })
            }
        }.getPlatformSpecificClient()
    }
}