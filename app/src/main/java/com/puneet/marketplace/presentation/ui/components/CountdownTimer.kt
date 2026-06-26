package com.puneet.marketplace.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.puneet.marketplace.R
import com.puneet.marketplace.domain.model.CountdownState
import com.puneet.marketplace.ui.theme.AppColors
import com.puneet.marketplace.ui.theme.MarketPlaceTheme

@Composable
fun CountdownTimer(
    countdown: CountdownState,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    showDays: Boolean = false,
    boxColor: Color = Color.White,
    digitColor: Color = AppColors.TitleDark,
    labelColor: Color = Color.White.copy(alpha = 0.85f),
) {
    CountdownRow(
        modifier = modifier,
        days = countdown.days,
        hours = countdown.hours,
        minutes = countdown.minutes,
        seconds = countdown.seconds,
        compact = compact,
        showDays = showDays,
        boxColor = boxColor,
        digitColor = digitColor,
        labelColor = labelColor,
    )
}

@Composable
private fun CountdownRow(
    days: Long,
    hours: Long,
    minutes: Long,
    seconds: Long,
    compact: Boolean,
    showDays: Boolean,
    boxColor: Color,
    digitColor: Color,
    labelColor: Color,
    modifier: Modifier = Modifier,
) {
    val units = buildList {
        if (showDays) {
            add(Triple(days, stringResource(R.string.countdown_days), true))
        }
        add(Triple(hours, stringResource(R.string.countdown_hours), showDays))
        add(Triple(minutes, stringResource(R.string.countdown_minutes), true))
        add(Triple(seconds, stringResource(R.string.countdown_seconds), true))
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        units.forEach { (value, label, _) ->
            CountdownUnit(
                value = value,
                label = label,
                compact = compact,
                boxColor = boxColor,
                digitColor = digitColor,
                labelColor = labelColor,
            )
        }
    }
}

@Composable
private fun CountdownUnit(
    value: Long,
    label: String,
    compact: Boolean,
    boxColor: Color,
    digitColor: Color,
    labelColor: Color,
) {
    val boxWidth = if (compact) 32.dp else 44.dp
    val boxHeight = if (compact) 28.dp else 40.dp
    val digitSize = if (compact) 12.sp else 16.sp
    val labelSize = if (compact) 8.sp else 10.sp
    val cornerRadius = if (compact) 6.dp else 10.dp

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = RoundedCornerShape(cornerRadius),
            color = boxColor,
            modifier = Modifier.size(width = boxWidth, height = boxHeight),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = value.toString().padStart(2, '0'),
                    color = digitColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = digitSize,
                )
            }
        }
        Spacer(modifier = Modifier.height(if (compact) 2.dp else 4.dp))
        Text(
            text = label,
            color = labelColor,
            fontSize = labelSize,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF7B4AE2)
@Composable
private fun CountdownTimerPreview() {
    MarketPlaceTheme {
        CountdownTimer(
            countdown = CountdownState(hours = 5, minutes = 30, seconds = 45),
            modifier = Modifier.padding(16.dp),
        )
    }
}
