package com.puneet.marketplace.domain.model

data class CountdownState(
    val days: Long = 0,
    val hours: Long = 0,
    val minutes: Long = 0,
    val seconds: Long = 0,
    val isExpired: Boolean = false,
)
