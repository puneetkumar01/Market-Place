package com.puneet.marketplace.data.mapper

import com.puneet.marketplace.data.local.entity.CategoryEntity
import com.puneet.marketplace.data.local.entity.ProductEntity
import com.puneet.marketplace.data.remote.dto.CategoryDto
import com.puneet.marketplace.data.remote.dto.ProductDto
import com.puneet.marketplace.domain.model.Category
import com.puneet.marketplace.domain.model.Product

fun CategoryDto.toEntity(iconUrl: String = ""): CategoryEntity = CategoryEntity(
    slug = slug,
    name = name,
    iconUrl = iconUrl,
)

fun CategoryEntity.toDomain(): Category = Category(
    slug = slug,
    name = name,
    iconUrl = iconUrl,
)

fun ProductDto.toEntity(): ProductEntity = ProductEntity(
    id = id,
    title = title,
    description = description,
    price = price,
    discountPercentage = discountPercentage,
    stock = stock,
    categorySlug = category,
    availabilityStatus = availabilityStatus,
    minimumOrderQuantity = minimumOrderQuantity,
    images = images,
    thumbnail = thumbnail,
)

fun ProductEntity.toDomain(): Product = Product(
    id = id,
    title = title,
    description = description,
    price = price,
    discountPercentage = discountPercentage,
    stock = stock,
    categorySlug = categorySlug,
    availabilityStatus = availabilityStatus,
    minimumOrderQuantity = minimumOrderQuantity,
    images = images,
    thumbnail = thumbnail,
)
