package com.puneet.marketplace.domain.usecase

import com.puneet.marketplace.domain.model.Product
import com.puneet.marketplace.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsByCategoryUseCase @Inject constructor(
    private val repository: ProductRepository,
) {
    operator fun invoke(categorySlug: String): Flow<List<Product>> =
        repository.observeProductsByCategory(categorySlug)
}
