package com.puneet.marketplace.domain

import com.puneet.marketplace.domain.model.CountdownState
import java.util.Calendar
import java.util.concurrent.TimeUnit

fun endOfDayMillis(): Long =
    Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.timeInMillis

fun remainingMillisUntil(targetTimeMillis: Long): Long =
    (targetTimeMillis - System.currentTimeMillis()).coerceAtLeast(0)

fun calculateCountdown(targetTimeMillis: Long): CountdownState {
    val remainingMillis = remainingMillisUntil(targetTimeMillis)
    if (remainingMillis == 0L) {
        return CountdownState(isExpired = true)
    }
    return CountdownState(
        days = TimeUnit.MILLISECONDS.toDays(remainingMillis),
        hours = TimeUnit.MILLISECONDS.toHours(remainingMillis) % 24,
        minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMillis) % 60,
        seconds = TimeUnit.MILLISECONDS.toSeconds(remainingMillis) % 60,
        isExpired = false,
    )
}
