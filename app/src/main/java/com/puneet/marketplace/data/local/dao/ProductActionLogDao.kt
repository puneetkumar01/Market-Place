package com.puneet.marketplace.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.puneet.marketplace.data.local.entity.ProductActionLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductActionLogDao {

    @Insert
    suspend fun insert(log: ProductActionLogEntity)

    @Query("SELECT * FROM product_action_logs WHERE productId = :productId ORDER BY timestamp DESC")
    fun observeByProduct(productId: Int): Flow<List<ProductActionLogEntity>>

    @Query("SELECT * FROM product_action_logs WHERE uploaded = 0 ORDER BY timestamp ASC")
    suspend fun getAllPending(): List<ProductActionLogEntity>

    @Query("UPDATE product_action_logs SET uploaded = 1 WHERE id IN (:ids)")
    suspend fun markAsUploaded(ids: List<Long>)
}
