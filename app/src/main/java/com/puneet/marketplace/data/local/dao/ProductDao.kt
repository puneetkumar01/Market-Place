package com.puneet.marketplace.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.puneet.marketplace.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {


    @Query("SELECT * FROM products WHERE discountPercentage < 10 ORDER BY title ASC")
    fun observeAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE categorySlug = :categorySlug AND discountPercentage < 10 ORDER BY title ASC")
    fun observeProductsByCategory(categorySlug: String): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Query("SELECT * FROM products WHERE discountPercentage >= 10 ORDER BY discountPercentage DESC")
    fun observeFlashDealProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :productId LIMIT 1")
    fun observeProductById(productId: Int): Flow<ProductEntity?>
}
