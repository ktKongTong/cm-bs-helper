package io.ktlab.bshelper.api

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.MapFilterParam
import io.ktlab.bshelper.model.dto.response.APIRespResult
import io.ktlab.bshelper.model.dto.response.BSRespDTO
import io.ktlab.bshelper.paging.BSMapPagingSource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.request
import kotlinx.coroutines.flow.Flow

class ToolAPI(private val httpClient: HttpClient) {
    private val BASE_PATH = "https://api.beatsaver.com"


}