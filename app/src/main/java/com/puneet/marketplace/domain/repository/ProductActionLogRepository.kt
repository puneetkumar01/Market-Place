package com.puneet.marketplace.domain.repository

import com.puneet.marketplace.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductActionLogRepository {

    fun observeHasJoinedPool(productId: Int): Flow<Boolean>

    suspend fun logJoinPool(product: Product)

    suspend fun logShare(product: Product)

    suspend fun logBuyNow(product: Product)

    suspend fun uploadPendingLogs(): Result<Int>
}
