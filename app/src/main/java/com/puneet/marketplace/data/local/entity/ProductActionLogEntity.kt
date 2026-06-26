package com.puneet.marketplace.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class ProductActionType {
    JOIN_POOL,
    SHARE,
    BUY_NOW,
}

@Entity(
    tableName = "product_action_logs",
    indices = [Index(value = ["productId", "actionType"])],
)
data class ProductActionLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Int,
    val actionType: ProductActionType,
    val productTitle: String,
    val priceAtAction: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val uploaded: Boolean = false,
)
