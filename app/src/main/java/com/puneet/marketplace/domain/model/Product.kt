package com.puneet.marketplace.domain.model

data class Product(
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
    val thumbnail: String,
)
