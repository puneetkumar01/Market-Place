package com.puneet.marketplace.domain.extensions

import com.puneet.marketplace.domain.model.Product

fun Product.hasDiscount(): Boolean = discountPercentage > 0

fun Product.priceAfterDiscount(): Double =
    if (hasDiscount()) price * (1 - discountPercentage / 100) else price

fun Product.savingsAmount(): Double =
    if (hasDiscount()) price - priceAfterDiscount() else 0.0

fun Product.displayImage(): String =
    images.firstOrNull { it.isNotBlank() } ?: thumbnail
