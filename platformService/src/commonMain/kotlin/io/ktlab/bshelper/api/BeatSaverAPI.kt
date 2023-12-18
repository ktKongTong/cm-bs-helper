package io.ktlab.bshelper.api

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktlab.bshelper.model.annotation.QueryParam
import io.ktlab.bshelper.model.dto.BSMapDTO
import io.ktlab.bshelper.model.dto.request.MapFilterParam
import io.ktlab.bshelper.model.dto.request.PlaylistFilterParam
import io.ktlab.bshelper.model.dto.response.APIRespResult
import io.ktlab.bshelper.model.dto.response.BSMapReviewDTO
import io.ktlab.bshelper.model.dto.response.BSMapReviewRespDTO
import io.ktlab.bshelper.model.dto.response.BSMapperDetailDTO
import io.ktlab.bshelper.model.dto.response.BSMapperListDTO
import io.ktlab.bshelper.model.dto.response.BSPlaylistDetailRespDTO
import io.ktlab.bshelper.model.dto.response.BSPlaylistRespDTO
import io.ktlab.bshelper.model.dto.response.BSRespDTO
import io.ktlab.bshelper.model.dto.response.MapQueryByHashesDTO
import io.ktlab.bshelper.model.dto.response.MapQueryByIdsDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.request
import io.ktor.http.URLBuilder
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlin.reflect.full.findAnnotation

private val logger = KotlinLogging.logger {}

class BeatSaverAPI(private val httpClient: HttpClient) {
    private val json =
        Json {
            ignoreUnknownKeys = true
        }

    private val basePath = "https://api.beatsaver.com"
    init {
        logger.info { "init BeatSaverAPI, basePath = $basePath" }
    }

    private fun URLBuilder.applyQueryParam(queryParam:Any?) {
        queryParam?.let {
            it::class.members.forEach { member ->
                val anno = member.findAnnotation<QueryParam>()

                if (anno != null) {
                    val v = member.call(it)
                    if (v != null) {
                        val key = anno.key.ifEmpty { member.name }
                        parameters.append(key, member.call(it).toString())
                    }
                }
            }
        }
    }

    suspend fun searchMap(
        queryParam: MapFilterParam?,
        page: Int = 0,
    ): APIRespResult<BSRespDTO> {
        val url = URLBuilder("$basePath/search/text/$page")
            .apply { applyQueryParam(queryParam) }
            .build()
        return try {
            logger.info { "searchMap: param$queryParam, page=$page, url = $url" }
            val response = httpClient.get(url)
            val resp = response.body<String>()
            val res = json.decodeFromString(BSRespDTO.serializer(), resp)
            APIRespResult.Success(res)
        } catch (e: Exception) {
            logger.error { "searchMap: param$queryParam, page=$page, url = $url $e" }
            APIRespResult.Error(e)
        }
    }

//    2023-12-07T04:18:23Z 2023-10-29T14:50:55.841935
    suspend fun getCollaborationMapById(
        id: Int,
        before: LocalDateTime? = null,
    ): APIRespResult<BSRespDTO> {
        return try {
            val url =
                URLBuilder("$basePath/maps/collaborations/$id").apply {
                    before?.let {
                        // format  2018-07-16T03:24:30Z 2023-10-29T14:50:55.841935
                        val p = it.toString().split(".").first() + "Z"
                        parameters.append("before", p)
                    }
                }.build()
            logger.info { "getCollaborationMapById:  mapperId = $id, before${before}, url = $url" }
            val response = httpClient.get(url)
            val resp = response.body<BSRespDTO>()
            APIRespResult.Success(resp)
        } catch (e: Exception) {
            logger.error { "getCollaborationMapById: mapperId = $id, before${before}, error: ${e.message}" }
            APIRespResult.Error(e)
        }
    }

    suspend fun getMapsByIds(ids: List<String>): MapQueryByIdsDTO {
        val idstr = ids.joinToString(",")
        logger.info { "getMapsByIds: $idstr" }
        val res = httpClient.request("$basePath/maps/ids/$idstr").body<JsonElement>()
        return if (res.jsonObject.containsKey("error")) {
            val errorMsg = res.jsonObject["error"]
            logger.error { "getMapsByIds: error: $errorMsg" }
            throw Exception(res.jsonObject["error"].toString())
        } else if (res.jsonObject.containsKey("id")) {
            val map = json.decodeFromJsonElement<BSMapDTO>(res)
            mapOf(map.id to map)
        } else {
            json.decodeFromJsonElement<MapQueryByIdsDTO>(res)
        }
    }

    suspend fun getMapsByHashes(hashes: List<String>): MapQueryByHashesDTO {
        val hash = hashes.joinToString(",")
        logger.info { "getMapsByHashes: $hashes" }
        val res = httpClient.request("$basePath/maps/hash/$hash").body<JsonElement>()
        return if (res.jsonObject.containsKey("error")) {
            val errorMsg = res.jsonObject["error"]
            logger.error { "getMapsByHashes: error: $errorMsg" }
            throw Exception(errorMsg.toString())
        } else if (res.jsonObject.containsKey("id")) {
            val map = json.decodeFromJsonElement<BSMapDTO>(res)
            mapOf(map.versions[0].hash to map)
        } else {
            json.decodeFromJsonElement<MapQueryByHashesDTO>(res)
        }
    }

    suspend fun searchPlaylist(
        queryParam: PlaylistFilterParam?,
        page: Int = 0,
    ): APIRespResult<BSPlaylistRespDTO> {
        val url = URLBuilder("$basePath/playlists/search/$page")
            .apply { applyQueryParam(queryParam) }
            .build()
        return try {
            logger.debug { "searchPlaylist: param$queryParam, page=$page, url= $url"}
            val response = httpClient.get(url)
            val resp = response.body<BSPlaylistRespDTO>()
            APIRespResult.Success(resp)
        } catch (e: Exception) {
            logger.error { "searchPlaylist: param$queryParam, page=$page, url= $url, error: ${e.message}" }
            APIRespResult.Error(e)
        }
    }

    suspend fun getPlaylistDetail(
        playlistId: String,
        page: Int = 0,
    ): APIRespResult<BSPlaylistDetailRespDTO> {
        val url = "$basePath/playlists/id/$playlistId/$page"
        return try {
            logger.debug { "getPlaylistDetail: bsPlaylistId:${playlistId}, page:$page, url:$url" }
            val res = httpClient.request(url).body<BSPlaylistDetailRespDTO>()
            APIRespResult.Success(res)
        } catch (e: Exception) {
            logger.error { "getPlaylistDetail: bsPlaylistId:${playlistId}, page:$page, url:$url, error:${e.message}" }
            APIRespResult.Error(e)
        }
    }

    suspend fun getMapReview(
        mapId: String,
        page: Int = 0,
    ): APIRespResult<List<BSMapReviewDTO>> {
        val url = "$basePath/review/map/$mapId/$page"
        return try {
            logger.debug { "getMapReview: mapId:${mapId},page:$page, url: $url" }
            val res = httpClient.request(url).body<BSMapReviewRespDTO>()
            APIRespResult.Success(res.docs)
        } catch (e: Exception) {
            logger.error { "getMapReview: mapId:${mapId},page:$page, url: $url, error:${e.message}" }
            APIRespResult.Error(e)
        }
    }

    suspend fun getMappers(page: Int = 0): APIRespResult<BSMapperListDTO> {
        val url = "$basePath/users/list/$page"
        return try {
            logger.debug { "getMappers: page:$page, url: $url" }
            val res = httpClient.request(url).body<BSMapperListDTO>()
            APIRespResult.Success(res)
        } catch (e: Exception) {
            logger.error { "getMappers: page:$page, url: $url, error:${e.message}" }
            APIRespResult.Error(e)
        }
    }

    suspend fun getMapperDetail(id: Int): APIRespResult<BSMapperDetailDTO> {
        val url = "$basePath/users/id/$id"
        return try {
            logger.debug { "getMapperDetail: mapperId:$id, url: $url" }
            val res = httpClient.request(url).body<BSMapperDetailDTO>()
            APIRespResult.Success(res)
        } catch (e: Exception) {
            logger.error { "getMapperDetail: mapperId:$id, error:${e.message}" }
            APIRespResult.Error(e)
        }
    }
}
