package com.puneet.marketplace.domain.usecase

import com.puneet.marketplace.domain.model.Product
import com.puneet.marketplace.domain.repository.ProductActionLogRepository
import javax.inject.Inject

class LogProductActionUseCase @Inject constructor(
    private val repository: ProductActionLogRepository,
) {

    suspend fun joinPool(product: Product) {
        repository.logJoinPool(product)
    }

    suspend fun share(product: Product) {
        repository.logShare(product)
    }

    suspend fun buyNow(product: Product) {
        repository.logBuyNow(product)
    }
}
