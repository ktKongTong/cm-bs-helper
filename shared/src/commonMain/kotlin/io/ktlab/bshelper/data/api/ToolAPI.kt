package io.ktlab.bshelper.data.api

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktlab.bshelper.BuildConfig
import io.ktlab.bshelper.model.AppVersionChangeLog
import io.ktlab.bshelper.model.dto.ExportPlaylist
import io.ktlab.bshelper.model.dto.request.KVSetRequest
import io.ktlab.bshelper.model.dto.response.APIRespResult
import io.ktlab.bshelper.model.dto.response.ToolAPIResponse
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
    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val basePath = BuildConfig.TOOL_API_URL
    init {
        logger.info { "init ToolAPI, basePath = $basePath" }
    }

    suspend fun setKV(setRequest: KVSetRequest<ExportPlaylist>): APIRespResult<String> {
        val url = "$basePath/key"
        return try {
            logger.debug { "setKV: url:$url, body:$setRequest" }
            val response =
                httpClient.post(url) {
                    contentType(ContentType.Application.Json)
                    setBody(setRequest)
                }
            val resp = response.body<ToolAPIResponse<String>>()

            if (resp.data == null) {
                return APIRespResult.Error(Exception(resp.message))
            }
            APIRespResult.Success(resp.data)
        } catch (e: Exception) {
            logger.error { "setKV: url:$url, body:$setRequest, error:${e.message}" }
            APIRespResult.Error(e)
        }
    }

    suspend fun getKV(key: String): APIRespResult<ExportPlaylist> {

        val url = "$basePath/key/$key"
        return try {
            logger.debug { "getKV: key:$key, url:$url" }
            val response = httpClient.get(url)
            val resp = response.body<ToolAPIResponse<KVSetRequest<ExportPlaylist>>>()
            if (resp.data == null) {
                return APIRespResult.Error(Exception(resp.message))
            }
            APIRespResult.Success(resp.data.value)
        } catch (e: Exception) {
            logger.error { "getKV: key:$key, url:$url, error:${e.message}" }
            APIRespResult.Error(e)
        }
    }

    suspend fun getLatestVersion(): APIRespResult<String> {
//        val url = "https://api.github.com/repos/ktKongTong/cm-bs-helper/releases/latest"
        val url = "$basePath/release/latest"
        return try {
            logger.debug { "getLatestVersion: url:$url" }
            val response = httpClient.get(url)
            val resp = response.body<ToolAPIResp<String>>()
            if (resp.code != 200) {
                return APIRespResult.Error(Exception(resp.message))
            }
            APIRespResult.Success(resp.data!!)
        } catch (e: Exception) {
            logger.error { "getLatestVersion: error, url:$url, error:${e.message}" }
            APIRespResult.Error(e)
        }
    }

    suspend fun checkHealthy() : APIRespResult<String> {
        val url = "$basePath/healthy"
        return try {
            logger.debug { "checkHealthy: url:$url" }
            val response = httpClient.get(url)
            val resp = response.body<ToolAPIResp<ExportPlaylist>>()
            if (resp.code != 200) {
                return APIRespResult.Error(Exception(resp.message))
            }
            APIRespResult.Success(resp.message)
        } catch (e: Exception) {
            logger.error { "checkHealthy: error, url:$url, error:${e.message}" }
            APIRespResult.Error(e)
        }
    }
    // onstartUp, check version, check healthy, check changelog
    suspend fun getRecentVersionChangeLog() : APIRespResult<List<AppVersionChangeLog>> {
        val url = "$basePath/release/changelog"
        return try {
            logger.debug { "getRecentVersionChangeLog: url:$url" }
            val response = httpClient.get(url)
            val resp = response.body<ToolAPIResp<List<AppVersionChangeLog>>>()
            if (resp.code != 200) {
                return APIRespResult.Error(Exception(resp.message))
            }
            APIRespResult.Success(resp.data!!)
        } catch (e: Exception) {
            logger.error { "getRecentVersionChangeLog: error, url:$url, error:${e.message}" }
            APIRespResult.Error(e)
        }
    }

    suspend fun getRecommendPlaylist() : APIRespResult<List<AppVersionChangeLog>> {
        val url = "$basePath/recommend/playlist"
        return try {
            logger.debug { "getRecommendPlaylist: url:$url" }
            val response = httpClient.get(url)
            val resp = response.body<ToolAPIResp<List<AppVersionChangeLog>>>()
            if (resp.code != 200) {
                return APIRespResult.Error(Exception(resp.message))
            }
            APIRespResult.Success(resp.data!!)
        } catch (e: Exception) {
            logger.error { "getRecentVersionChangeLog: error, url:$url, error:${e.message}" }
            APIRespResult.Error(e)
        }
    }
}

@Serializable
data class ToolAPIResp<T>(
    val message: String,
    val data: T? = null,
    val code: Int,
)
