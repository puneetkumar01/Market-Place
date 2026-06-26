package com.puneet.marketplace.domain.model

data class Category(
    val slug: String,
    val name: String,
    val iconUrl: String = "",
)
