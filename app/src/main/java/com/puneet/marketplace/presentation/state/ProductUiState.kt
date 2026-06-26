package com.puneet.marketplace.presentation.state

import com.puneet.marketplace.domain.calculateCountdown
import com.puneet.marketplace.domain.endOfDayMillis
import com.puneet.marketplace.domain.model.CountdownState
import com.puneet.marketplace.domain.model.Product
import com.puneet.marketplace.presentation.ui.model.ALL_CATEGORY_ID
import com.puneet.marketplace.presentation.ui.model.CategoryItem

data class ProductUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val categories: List<CategoryItem> = emptyList(),
    val selectedCategoryId: String = ALL_CATEGORY_ID,
    val countdown: CountdownState = calculateCountdown(endOfDayMillis()),
    val error: String? = null,
)
