package com.puneet.marketplace.data.remote.dto

data class AnalyticsBatchRequestDto(
    val events: List<AnalyticsEventDto>,
)

data class AnalyticsEventDto(
    val productId: Int,
    val actionType: String,
    val productTitle: String,
    val priceAtAction: Double,
    val timestamp: Long,
)
