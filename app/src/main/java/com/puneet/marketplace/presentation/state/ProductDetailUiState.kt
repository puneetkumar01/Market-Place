package com.puneet.marketplace.presentation.state

import com.puneet.marketplace.domain.calculateCountdown
import com.puneet.marketplace.domain.endOfDayMillis
import com.puneet.marketplace.domain.model.CountdownState
import com.puneet.marketplace.domain.model.Product

data class ProductDetailUiState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val countdown: CountdownState = calculateCountdown(endOfDayMillis()),
    val isJoined: Boolean = false,
    val error: String? = null,
)
