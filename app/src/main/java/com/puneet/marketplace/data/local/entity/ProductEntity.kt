package com.puneet.marketplace.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val description: String,
    val categorySlug: String,
    val price: Double,
    val discountPercentage: Double,
    val stock: Int,
    val availabilityStatus: String,
    val minimumOrderQuantity: Int,
    val images: List<String>,
    val thumbnail: String
)
