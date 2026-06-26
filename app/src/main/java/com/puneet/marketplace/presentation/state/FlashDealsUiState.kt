package com.puneet.marketplace.presentation.state

import com.puneet.marketplace.domain.calculateCountdown
import com.puneet.marketplace.domain.endOfDayMillis
import com.puneet.marketplace.domain.model.CountdownState
import com.puneet.marketplace.domain.model.Product

data class FlashDealsUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val joinedProductIds: Set<Int> = emptySet(),
    val countdown: CountdownState = calculateCountdown(endOfDayMillis()),
    val error: String? = null,
)
