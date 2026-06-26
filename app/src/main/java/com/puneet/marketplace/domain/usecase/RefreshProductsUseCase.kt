package com.puneet.marketplace.domain.usecase

import com.puneet.marketplace.domain.repository.ProductRepository
import javax.inject.Inject


class RefreshProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {

    suspend operator fun invoke() = repository.refreshProducts()

}