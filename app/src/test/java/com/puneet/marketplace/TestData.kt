package com.puneet.marketplace

import com.puneet.marketplace.domain.model.Category
import com.puneet.marketplace.domain.model.Product

fun product(
    id: Int = 1,
    title: String = "Product $id",
    description: String = "desc",
    categorySlug: String = "phones",
    price: Double = 100.0,
    discountPercentage: Double = 0.0,
    stock: Int = 10,
    availabilityStatus: String = "In Stock",
    minimumOrderQuantity: Int = 1,
    images: List<String> = listOf("img-$id"),
    thumbnail: String = "thumb-$id",
) = Product(
    id = id,
    title = title,
    description = description,
    categorySlug = categorySlug,
    price = price,
    discountPercentage = discountPercentage,
    stock = stock,
    availabilityStatus = availabilityStatus,
    minimumOrderQuantity = minimumOrderQuantity,
    images = images,
    thumbnail = thumbnail,
)

fun category(
    slug: String = "phones",
    name: String = "Phones",
    iconUrl: String = "icon-$slug",
) = Category(slug = slug, name = name, iconUrl = iconUrl)
