package com.puneet.marketplace.presentation.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.puneet.marketplace.R
import com.puneet.marketplace.domain.model.Product
import com.puneet.marketplace.domain.extensions.hasDiscount
import com.puneet.marketplace.domain.extensions.priceAfterDiscount
import com.puneet.marketplace.ui.theme.AppColors
import com.puneet.marketplace.ui.theme.MarketPlaceTheme

@Composable
fun ProductPriceRow(
    product: Product,
    modifier: Modifier = Modifier,
    showAvailability: Boolean = false,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.price_format, product.priceAfterDiscount()),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = AppColors.Purple,
        )
        if (product.hasDiscount()) {
            Text(
                text = stringResource(R.string.price_format, product.price),
                style = MaterialTheme.typography.bodyMedium.copy(
                    textDecoration = TextDecoration.LineThrough,
                ),
                color = AppColors.LabelGray,
            )
        }
        if (showAvailability) {
            Spacer(modifier = Modifier.weight(1f))
            AvailabilityBadge(status = product.availabilityStatus)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductPriceRowPreview() {
    MarketPlaceTheme {
        ProductPriceRow(
            product = Product(
                id = 1,
                title = "boAt Airdopes 141",
                description = "True Wireless Earbuds",
                categorySlug = "mobile-accessories",
                price = 49.99,
                discountPercentage = 35.0,
                stock = 100,
                availabilityStatus = "In Stock",
                minimumOrderQuantity = 1,
                images = emptyList(),
                thumbnail = "",
            ),
            showAvailability = true,
        )
    }
}
