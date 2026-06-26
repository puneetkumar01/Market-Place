package com.puneet.marketplace.data.repository

import com.puneet.marketplace.data.image.ImagePrefetcher
import com.puneet.marketplace.data.local.dao.CategoryDao
import com.puneet.marketplace.data.local.dao.ProductDao
import com.puneet.marketplace.data.mapper.toDomain
import com.puneet.marketplace.data.mapper.toEntity
import com.puneet.marketplace.data.remote.api.ProductApiService
import com.puneet.marketplace.domain.model.Category
import com.puneet.marketplace.domain.model.Product
import com.puneet.marketplace.domain.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val api: ProductApiService,
    private val productDao: ProductDao,
    private val categoryDao: CategoryDao,
    private val imagePrefetcher: ImagePrefetcher,
) : ProductRepository {

    override suspend fun refreshProducts() = withContext(Dispatchers.IO) {
        val remoteCategories = api.getCategories()
        val response = api.getProducts()

        val categoryThumbnails = response.products
            .groupBy { it.category }
            .mapValues { (_, products) ->
                products.firstOrNull { it.thumbnail.isNotBlank() }?.thumbnail.orEmpty()
            }

        categoryDao.insertAll(
            remoteCategories.map { dto ->
                dto.toEntity(iconUrl = categoryThumbnails[dto.slug].orEmpty())
            },
        )

        val remoteProducts = response.products.map { it.toEntity() }
        productDao.insertAll(remoteProducts)

        val imageUrls = buildList {
            remoteProducts.forEach { product ->
                add(product.thumbnail)
                addAll(product.images)
            }
            addAll(categoryThumbnails.values)
        }
        imagePrefetcher.prefetch(imageUrls)
    }

    override fun observeCategories(): Flow<List<Category>> =
        categoryDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun observeProducts(): Flow<List<Product>> =
        productDao.observeAllProducts().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun observeProductsByCategory(categorySlug: String): Flow<List<Product>> =
        productDao.observeProductsByCategory(categorySlug).map { entities ->
            entities.map { it.toDomain() }
        }

    override fun observeFlashDealProducts(): Flow<List<Product>> =
        productDao.observeFlashDealProducts().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun observeProductById(productId: Int): Flow<Product?> =
        productDao.observeProductById(productId).map { entity ->
            entity?.toDomain()
        }
}
