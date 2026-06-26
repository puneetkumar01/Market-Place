package com.puneet.marketplace.domain.usecase

import com.puneet.marketplace.domain.repository.ProductActionLogRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveHasJoinedPoolUseCase @Inject constructor(
    private val repository: ProductActionLogRepository,
) {
    operator fun invoke(productId: Int): Flow<Boolean> =
        repository.observeHasJoinedPool(productId)
}
