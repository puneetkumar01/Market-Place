package com.puneet.marketplace.presentation.ui.components.common

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.puneet.marketplace.R
import com.puneet.marketplace.ui.theme.AppColors
import com.puneet.marketplace.ui.theme.MarketPlaceTheme

@Composable
fun DiscountBadge(
    discountPercentage: Double,
    modifier: Modifier = Modifier,
) {
    if (discountPercentage <= 0) return

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = AppColors.DiscountYellow,
    ) {
        Text(
            text = stringResource(R.string.discount_percent_off, discountPercentage.toInt()),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DiscountBadgePreview() {
    MarketPlaceTheme {
        DiscountBadge(discountPercentage = 35.0)
    }
}
