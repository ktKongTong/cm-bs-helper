package io.ktlab.bshelper.api

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktlab.bshelper.BuildConfig
import io.ktlab.bshelper.model.dto.ExportPlaylist
import io.ktlab.bshelper.model.dto.request.KVSetRequest
import io.ktlab.bshelper.model.dto.response.APIRespResult
import io.ktlab.bshelper.model.dto.response.KVSetResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val logger = KotlinLogging.logger {}
class ToolAPI(private val httpClient: HttpClient) {
    private val json =
        Json {
            ignoreUnknownKeys = true
        }

    private val basePath = BuildConfig.TOOL_API_URL
    init {
        logger.info { "init ToolAPI, basePath = $basePath" }
    }
    suspend fun setKV(setRequest: KVSetRequest<ExportPlaylist>): APIRespResult<KVSetResponse> {
        val url = "$basePath/api"
        return try {
            logger.debug { "setKV: url:$url, body:$setRequest" }
            val response =
                httpClient.post(url) {
                    contentType(ContentType.Application.Json)
                    setBody(setRequest)
                }
            val resp = response.body<KVSetResponse>()
            if (resp.key == null) {
                return APIRespResult.Error(Exception(resp.message))
            }
            APIRespResult.Success(resp)
        } catch (e: Exception) {
            logger.error { "setKV: url:$url, body:$setRequest, error:${e.message}" }
            APIRespResult.Error(e)
        }
    }

    suspend fun getKV(key: String): APIRespResult<ExportPlaylist> {

        val url = "$basePath/api/$key"
        return try {
            logger.debug { "getKV: key:$key, url:$url" }
            val response = httpClient.get(url)
            val resp = response.body<ToolAPIResp>()
            APIRespResult.Success(resp.content)
        } catch (e: Exception) {
            logger.error { "getKV: key:$key, url:$url, error:${e.message}" }
            APIRespResult.Error(e)
        }
    }
}

@Serializable
data class ToolAPIResp(
    val message: String,
    val content: ExportPlaylist,
)
