package com.puneet.marketplace.data.mapper

import com.puneet.marketplace.data.local.entity.ProductActionLogEntity
import com.puneet.marketplace.data.remote.dto.AnalyticsEventDto

fun ProductActionLogEntity.toAnalyticsDto(): AnalyticsEventDto =
    AnalyticsEventDto(
        productId = productId,
        actionType = actionType.name,
        productTitle = productTitle,
        priceAtAction = priceAtAction,
        timestamp = timestamp,
    )
