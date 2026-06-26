package com.puneet.marketplace.data.remote.dto

/**
 * Response from GET https://dummyjson.com/products
 */
data class ProductsResponseDto(
    val products: List<ProductDto>,
    val total: Int,
    val skip: Int,
    val limit: Int,
)

/**
 * Single product item from the DummyJSON products API.
 * Gson ignores extra JSON fields (reviews, meta, dimensions, etc.).
 */
data class ProductDto(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val discountPercentage: Double = 0.0,
    val stock: Int = 0,
    val availabilityStatus: String = "",
    val minimumOrderQuantity: Int = 0,
    val images: List<String> = emptyList(),
    val thumbnail: String = "",
)
