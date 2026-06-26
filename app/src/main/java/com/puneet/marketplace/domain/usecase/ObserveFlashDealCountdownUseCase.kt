package com.puneet.marketplace.domain.usecase

import com.puneet.marketplace.domain.calculateCountdown
import com.puneet.marketplace.domain.endOfDayMillis
import com.puneet.marketplace.domain.model.CountdownState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ObserveFlashDealCountdownUseCase @Inject constructor() {

    operator fun invoke(targetTimeMillis: Long = endOfDayMillis()): Flow<CountdownState> = flow {
        while (true) {
            emit(calculateCountdown(targetTimeMillis))
            delay(1_000)
        }
    }
}
