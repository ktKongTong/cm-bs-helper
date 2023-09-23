package io.ktlab.bshelper.api

import io.ktlab.bshelper.model.dto.request.MapFilterParam
import io.ktlab.bshelper.model.annotation.QueryParam
import io.ktlab.bshelper.model.dto.BSMapDTO
import io.ktlab.bshelper.model.dto.response.APIRespResult
import io.ktlab.bshelper.model.dto.response.BSRespDTO
import io.ktlab.bshelper.model.dto.response.MapQueryByHashesDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.request
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlin.reflect.full.findAnnotation

class BeatSaverAPI(private val httpClient: HttpClient) {


    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val basePath = "https://api.beatsaver.com"

    suspend fun searchMap(queryParam: MapFilterParam?, page:Int = 0): APIRespResult<BSRespDTO> {
        return try {
           val url = URLBuilder("$basePath/search/text/$page").apply {
                queryParam?.let {
                    it::class.members.forEach { member ->
                        val anno = member.findAnnotation<QueryParam>()

                        if (anno != null) {
                            val v = member.call(it)
                            if (v!= null) {
                                val key = anno.key.ifEmpty { member.name }
                                parameters.append(key, member.call(it).toString())
                            }
                        }
                    }
                }
            }.build()
            val response = httpClient.get(url)
            val resp = response.body<BSRespDTO>()
            APIRespResult.Success(resp)
        }catch (e: Exception){
            APIRespResult.Error(e)
        }
    }

    suspend fun getMapsByHashes(hashes : List<String>): MapQueryByHashesDTO {
        val hash = hashes.joinToString(",")
        val res = httpClient.request("$basePath/maps/hash/$hash").body<JsonElement>()
        return if (res.jsonObject.containsKey("error")) {
            throw Exception(res.jsonObject["error"].toString())
        }else if (res.jsonObject.containsKey("id")) {
            val map = json.decodeFromJsonElement<BSMapDTO>(res)
            mapOf(map.versions[0].hash to map)
        }else {
            json.decodeFromJsonElement<MapQueryByHashesDTO>(res)
        }
    }

}