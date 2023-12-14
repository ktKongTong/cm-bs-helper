package io.ktlab.bshelper.api

import io.ktlab.bshelper.model.dto.ExportPlaylist
import io.ktlab.bshelper.model.dto.request.KVSetRequest
import io.ktlab.bshelper.model.dto.response.APIRespResult
import io.ktlab.bshelper.model.dto.response.KVSetResponse
import io.ktlab.bshelper.utils.Constants
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class ToolAPI(private val httpClient: HttpClient) {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val BASE_PATH = Constants.TOOL_BASE_URL
    suspend fun setKV(setRequest: KVSetRequest<ExportPlaylist>): APIRespResult<KVSetResponse> {
        return try {
            val response = httpClient.post("$BASE_PATH/api") {
                contentType(ContentType.Application.Json)
                setBody(setRequest)
            }
            val resp = response.body<KVSetResponse>()
            if (resp.key == null) {
                return APIRespResult.Error(Exception(resp.message))
            }
            APIRespResult.Success(resp)
        }catch (e: Exception){
            APIRespResult.Error(e)
        }
    }

    suspend fun getKV(key: String): APIRespResult<ExportPlaylist> {
        return try {
            val response = httpClient.get("$BASE_PATH/api/$key")
            val resp = response.body<ToolAPIResp>()
            APIRespResult.Success(resp.content)
        }catch (e: Exception){
            APIRespResult.Error(e)
        }
    }
}
@Serializable
data class ToolAPIResp(
    val message: String,
    val content: ExportPlaylist,
)