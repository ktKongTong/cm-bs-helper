package io.ktlab.bshelper.api

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.MapFilterParam
import io.ktlab.bshelper.model.dto.response.APIRespResult
import io.ktlab.bshelper.model.dto.response.BSRespDTO
import io.ktlab.bshelper.model.dto.response.MapQueryByHashesDTO
import io.ktlab.bshelper.paging.BSMapPagingSource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.request
import kotlinx.coroutines.flow.Flow

class BeatSaverAPI(private val httpClient: HttpClient) {

    private val basePath = "https://api.beatsaver.com"

    suspend fun searchMap(queryParam: MapFilterParam?,page:Int = 0): APIRespResult<BSRespDTO> {
        return try {
            val response = httpClient.get("$basePath/search/text/$page")
            val resp = response.body<BSRespDTO>()
            APIRespResult.Success(resp)
        }catch (e: Exception){
            APIRespResult.Error(e)
        }
    }
    suspend fun getUser(): String {
        return httpClient.request("https://jsonplaceholder.typicode.com/users/1").body<String>()
    }

    suspend fun getMapsByHashes(hashes : List<String>): MapQueryByHashesDTO {
        val hash = hashes.joinToString(",")
        return httpClient.request("$basePath/maps/hash/$hash").body<MapQueryByHashesDTO>()
    }
    fun getPaging(mapFilterParam:MapFilterParam): Flow<PagingData<IMap>> = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = {
            BSMapPagingSource(this,mapFilterParam)
        }
    ).flow
}