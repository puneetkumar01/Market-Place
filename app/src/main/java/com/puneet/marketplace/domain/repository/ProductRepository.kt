package com.puneet.marketplace.domain.repository

import com.puneet.marketplace.domain.model.Category
import com.puneet.marketplace.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    fun observeProducts(): Flow<List<Product>>
    fun observeProductsByCategory(categorySlug: String): Flow<List<Product>>
    fun observeFlashDealProducts(): Flow<List<Product>>
    fun observeProductById(productId: Int): Flow<Product?>
    suspend fun refreshProducts()
    fun observeCategories(): Flow<List<Category>>
}
