package com.puneet.marketplace.data.repository

import com.puneet.marketplace.data.local.dao.ProductActionLogDao
import com.puneet.marketplace.data.local.entity.ProductActionLogEntity
import com.puneet.marketplace.data.local.entity.ProductActionType
import com.puneet.marketplace.data.mapper.toAnalyticsDto
import com.puneet.marketplace.data.remote.utils.AnalyticsEndpoints
import com.puneet.marketplace.data.remote.api.AnalyticsApiService
import com.puneet.marketplace.data.remote.dto.AnalyticsBatchRequestDto
import com.puneet.marketplace.domain.model.Product
import com.puneet.marketplace.domain.extensions.priceAfterDiscount
import com.puneet.marketplace.domain.repository.ProductActionLogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductActionLogRepositoryImpl @Inject constructor(
    private val productActionLogDao: ProductActionLogDao,
    private val analyticsApi: AnalyticsApiService,
) : ProductActionLogRepository {

    override fun observeHasJoinedPool(productId: Int): Flow<Boolean> =
        productActionLogDao.observeByProduct(productId)
            .map { logs -> logs.any { it.actionType == ProductActionType.JOIN_POOL } }

    override suspend fun logJoinPool(product: Product) = withContext(Dispatchers.IO) {
        productActionLogDao.insert(product.toActionLogEntity(ProductActionType.JOIN_POOL))
    }

    override suspend fun logShare(product: Product) = withContext(Dispatchers.IO) {
        productActionLogDao.insert(product.toActionLogEntity(ProductActionType.SHARE))
    }

    override suspend fun logBuyNow(product: Product) = withContext(Dispatchers.IO) {
        productActionLogDao.insert(product.toActionLogEntity(ProductActionType.BUY_NOW))
    }

    override suspend fun uploadPendingLogs(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val pending = productActionLogDao.getAllPending()
            if (pending.isEmpty()) {
                return@withContext Result.success(0)
            }

            val response = analyticsApi.uploadAnalytics(
                url = AnalyticsEndpoints.UPLOAD_URL,
                request = AnalyticsBatchRequestDto(
                    events = pending.map { it.toAnalyticsDto() },
                ),
            )
            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    IllegalStateException("Analytics upload failed: HTTP ${response.code()}"),
                )
            }

            productActionLogDao.markAsUploaded(pending.map { it.id })
            Result.success(pending.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun Product.toActionLogEntity(actionType: ProductActionType) =
        ProductActionLogEntity(
            productId = id,
            actionType = actionType,
            productTitle = title,
            priceAtAction = priceAfterDiscount(),
        )
}
