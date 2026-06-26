package com.puneet.marketplace.data.remote.api

import com.puneet.marketplace.data.remote.dto.AnalyticsBatchRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface AnalyticsApiService {

    @POST
    suspend fun uploadAnalytics(
        @Url url: String,
        @Body request: AnalyticsBatchRequestDto,
    ): Response<Unit>
}
