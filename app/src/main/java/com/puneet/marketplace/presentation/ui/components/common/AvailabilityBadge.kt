package com.puneet.marketplace.presentation.ui.components.common

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.puneet.marketplace.ui.theme.AppColors
import com.puneet.marketplace.ui.theme.MarketPlaceTheme

@Composable
fun AvailabilityBadge(
    status: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = AppColors.StockGreenBg,
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = AppColors.StockGreen,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AvailabilityBadgePreview() {
    MarketPlaceTheme {
        AvailabilityBadge(status = "In Stock")
    }
}
