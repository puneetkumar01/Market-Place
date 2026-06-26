package com.puneet.marketplace.domain

import com.puneet.marketplace.domain.model.CountdownState
import com.puneet.marketplace.domain.usecase.ObserveFlashDealCountdownUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlashDealCountdownManager @Inject constructor(
    observeFlashDealCountdownUseCase: ObserveFlashDealCountdownUseCase,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val countdown: StateFlow<CountdownState> = observeFlashDealCountdownUseCase()
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = calculateCountdown(endOfDayMillis()),
        )
}
