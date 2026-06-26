package com.puneet.marketplace.domain.usecase

import com.puneet.marketplace.domain.model.Product
import com.puneet.marketplace.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFlashDealProductsUseCase @Inject constructor(
    private val repository: ProductRepository,
) {
    operator fun invoke(): Flow<List<Product>> = repository.observeFlashDealProducts()
}
