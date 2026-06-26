package com.puneet.marketplace.presentation.ui.components.flashDealProduct

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.puneet.marketplace.R
import com.puneet.marketplace.domain.model.CountdownState
import com.puneet.marketplace.domain.model.Product
import com.puneet.marketplace.presentation.ui.components.CountdownTimer
import com.puneet.marketplace.presentation.ui.components.common.DiscountBadge
import com.puneet.marketplace.presentation.ui.components.common.ProductPriceRow
import com.puneet.marketplace.ui.theme.AppColors
import com.puneet.marketplace.ui.theme.MarketPlaceTheme
import com.puneet.marketplace.util.toCategoryLabel

@Composable
fun FlashDealProductCard(
    modifier: Modifier = Modifier,
    product: Product,
    countdown: CountdownState,
    isJoined: Boolean = false,
    onClick: () -> Unit = {},
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = product.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TitleDark,
                        lineHeight = 22.sp,
                    )
                    Text(
                        text = product.categorySlug.toCategoryLabel(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.TitleDark,
                    )
                    ProductPriceRow(product = product)
                    Text(
                        text = stringResource(R.string.deal_ends_in),
                        fontSize = 12.sp,
                        color = AppColors.LabelGray,
                    )
                    CountdownTimer(
                        countdown = countdown,
                        boxColor = AppColors.TimerBox,
                        digitColor = AppColors.Purple,
                        labelColor = AppColors.LabelGray,
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {

                    DiscountBadge(discountPercentage = product.discountPercentage)

                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(product.thumbnail)
                            .crossfade(true)
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.error_image)
                            .build(),
                        contentDescription = product.title,
                        modifier = Modifier.size(120.dp),
                        contentScale = ContentScale.Fit,
                    )

                    if (isJoined) {
                        Text(
                            text = stringResource(R.string.joined),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.Purple,
                        )
                    }
                }
            }


        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FlashDealProductCardPreview() {
    MarketPlaceTheme {
        FlashDealProductCard(
            product = Product(
                id = 1,
                title = "Noise Color fit Pro 4",
                description = "Smart fitness watch with AMOLED display.",
                categorySlug = "smart-watches",
                price = 2999.0,
                discountPercentage = 20.0,
                stock = 50,
                availabilityStatus = "In Stock",
                minimumOrderQuantity = 1,
                images = emptyList(),
                thumbnail = "https://cdn.dummyjson.com/product-images/mobile-accessories/apple-watch-series-9-gold/1.webp",
            ),
            countdown = CountdownState(hours = 1, minutes = 20, seconds = 5),
            modifier = Modifier.padding(16.dp),
        )
    }
}
