package io.ktlab.bshelper.api

import io.ktlab.bshelper.model.annotation.QueryParam
import io.ktlab.bshelper.model.dto.BSMapDTO
import io.ktlab.bshelper.model.dto.request.MapFilterParam
import io.ktlab.bshelper.model.dto.request.PlaylistFilterParam
import io.ktlab.bshelper.model.dto.response.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlin.reflect.full.findAnnotation

class BeatSaverAPI(private val httpClient: HttpClient) {
    private val json =
        Json {
            ignoreUnknownKeys = true
        }

    private val basePath = "https://api.beatsaver.com"

    suspend fun searchMap(
        queryParam: MapFilterParam?,
        page: Int = 0,
    ): APIRespResult<BSRespDTO> {
        return try {
            val url =
                URLBuilder("$basePath/search/text/$page").apply {
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
                }.build()
            val response = httpClient.get(url)
            val resp = response.body<String>()
            val res = json.decodeFromString(BSRespDTO.serializer(), resp)
            APIRespResult.Success(res)
        } catch (e: Exception) {
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
//                    before.
                        val p = it.toString().split(".").first() + "Z"
                        parameters.append("before", p)
                    }
                }.build()
            val response = httpClient.get(url)
            val resp = response.body<BSRespDTO>()
            APIRespResult.Success(resp)
        } catch (e: Exception) {
            APIRespResult.Error(e)
        }
    }

    suspend fun getMapsByIds(ids: List<String>): MapQueryByIdsDTO {
        val idstr = ids.joinToString(",")
        val res = httpClient.request("$basePath/maps/ids/$idstr").body<JsonElement>()
        return if (res.jsonObject.containsKey("error")) {
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
        val res = httpClient.request("$basePath/maps/hash/$hash").body<JsonElement>()
        return if (res.jsonObject.containsKey("error")) {
            throw Exception(res.jsonObject["error"].toString())
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
        return try {
            val url =
                URLBuilder("$basePath/playlists/search/$page").apply {
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
                }.build()
            val response = httpClient.get(url)
            val resp = response.body<BSPlaylistRespDTO>()
            APIRespResult.Success(resp)
        } catch (e: Exception) {
            APIRespResult.Error(e)
        }
    }

    suspend fun getPlaylistDetail(
        playlistId: String,
        page: Int = 0,
    ): APIRespResult<BSPlaylistDetailRespDTO> {
        return try {
            val res = httpClient.request("$basePath/playlists/id/$playlistId/$page").body<BSPlaylistDetailRespDTO>()
            APIRespResult.Success(res)
        } catch (e: Exception) {
            APIRespResult.Error(e)
        }
    }

    // only first page will be returned, cause top maps' reviews count really low
    suspend fun getMapReview(
        mapId: String,
        page: Int = 0,
    ): APIRespResult<List<BSMapReviewDTO>> {
        return try {
            val res = httpClient.request("$basePath/review/map/$mapId/$page").body<BSMapReviewRespDTO>()
            APIRespResult.Success(res.docs)
        } catch (e: Exception) {
            APIRespResult.Error(e)
        }
    }

    suspend fun getMappers(page: Int = 0): APIRespResult<BSMapperListDTO> {
        return try {
            val res = httpClient.request("$basePath/users/list/$page").body<BSMapperListDTO>()
            APIRespResult.Success(res)
        } catch (e: Exception) {
            APIRespResult.Error(e)
        }
    }

    suspend fun getMapperDetail(id: Int): APIRespResult<BSMapperDetailDTO> {
        return try {
            val res = httpClient.request("$basePath/users/id/$id").body<BSMapperDetailDTO>()
            APIRespResult.Success(res)
        } catch (e: Exception) {
            APIRespResult.Error(e)
        }
    }
//    suspend fun getMapperDetail(playlistId:String,page: Int = 0): APIRespResult<BSPlaylistDetailRespDTO> {
//        return try {
//            val res = httpClient.request("$basePath/playlists/id/$playlistId/$page").body<BSPlaylistDetailRespDTO>()
//            APIRespResult.Success(res)
//        }catch (e: Exception){
//            APIRespResult.Error(e)
//        }
//    }
}
