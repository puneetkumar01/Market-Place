package com.puneet.marketplace.presentation.ui.screen

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.puneet.marketplace.R
import com.puneet.marketplace.domain.model.CountdownState
import com.puneet.marketplace.domain.model.Product
import com.puneet.marketplace.domain.model.displayImage
import com.puneet.marketplace.domain.model.hasDiscount
import com.puneet.marketplace.domain.model.priceAfterDiscount
import com.puneet.marketplace.domain.model.savingsAmount
import com.puneet.marketplace.presentation.ui.components.CountdownTimer
import com.puneet.marketplace.presentation.ui.components.common.DiscountBadge
import com.puneet.marketplace.presentation.ui.components.common.LoadingIndicator
import com.puneet.marketplace.presentation.ui.components.common.ProductPriceRow
import com.puneet.marketplace.presentation.viewmodel.ProductDetailViewModel
import com.puneet.marketplace.ui.theme.AppColors
import com.puneet.marketplace.ui.theme.MarketPlaceTheme
import com.puneet.marketplace.util.toCategoryLabel

@Composable
fun ProductDetailScreen(
    modifier: Modifier = Modifier,
    showDealTimer: Boolean = false,
    viewModel: ProductDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> LoadingIndicator(modifier = modifier)
        uiState.error != null -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(uiState.error!!)
            }
        }
        uiState.product != null -> {
            ProductDetailContent(
                product = uiState.product!!,
                showDealTimer = showDealTimer,
                countdown = uiState.countdown,
                isJoined = uiState.isJoined,
                onJoinPoolClick = viewModel::logJoinPool,
                onBuyNowClick = viewModel::logBuyNow,
                onShareClick = viewModel::logShare,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun ProductDetailContent(
    modifier: Modifier = Modifier,
    product: Product,
    showDealTimer: Boolean = false,
    countdown: CountdownState,
    isJoined: Boolean = false,
    onJoinPoolClick: () -> Unit,
    onBuyNowClick: () -> Unit,
    onShareClick: () -> Unit,
) {
    val context = LocalContext.current
    val addedToCartMessage = stringResource(R.string.added_to_cart)
    val productLinkSharedMessage = stringResource(R.string.product_link_shared)
    val shareChooserTitle = stringResource(R.string.share_product_chooser_title)
    val shareText = stringResource(
        R.string.product_share_text,
        product.title,
        product.priceAfterDiscount(),
    )
    val handleBuyNowClick = {
        onBuyNowClick()
        Toast.makeText(context, addedToCartMessage, Toast.LENGTH_SHORT).show()
    }
    val handleShareClick = {
        onShareClick()
        Toast.makeText(context, productLinkSharedMessage, Toast.LENGTH_SHORT).show()
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        context.startActivity(Intent.createChooser(shareIntent, shareChooserTitle))
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
        bottomBar = {
            ProductDetailBottomBar(
                showJoinPool = showDealTimer,
                isJoined = isJoined,
                onJoinPoolClick = onJoinPoolClick,
                onBuyNowClick = handleBuyNowClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                contentAlignment = Alignment.Center,
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.displayImage())
                        .crossfade(true)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .build(),
                    contentDescription = product.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    contentScale = ContentScale.Fit,
                )

                DiscountBadge(
                    discountPercentage = product.discountPercentage,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 24.dp, top = 8.dp),
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                IconButton(onClick = handleShareClick) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(R.string.share),
                        tint = AppColors.TitleDark,
                    )
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TitleDark,
                )
                Text(
                    text = product.categorySlug.toCategoryLabel(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.TitleDark,
                )
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    color = AppColors.BodyGray,
                )

                if (showDealTimer) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.deal_ends_in),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TitleDark,
                    )
                    CountdownTimer(
                        countdown = countdown,
                        boxColor = Color.Black,
                        digitColor = Color.White,
                        labelColor = AppColors.TitleDark,
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                ProductPriceRow(
                    product = product,
                    showAvailability = true,
                )

                if (product.hasDiscount()) {
                    Text(
                        text = stringResource(R.string.you_save, product.savingsAmount()),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TitleDark,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ProductDetailBottomBar(
    showJoinPool: Boolean,
    isJoined: Boolean,
    onJoinPoolClick: () -> Unit,
    onBuyNowClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (showJoinPool) {
                if (isJoined) {
                    Button(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = AppColors.Purple.copy(alpha = 0.6f),
                            disabledContentColor = Color.White,
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.joined_checked),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                } else {
                    Button(
                        onClick = onJoinPoolClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.Purple,
                            contentColor = Color.White,
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.join_pool),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            val buyNowModifier = Modifier
                .then(if (showJoinPool) Modifier.weight(1f) else Modifier.fillMaxWidth())
                .height(52.dp)

            if (showJoinPool) {
                OutlinedButton(
                    onClick = onBuyNowClick,
                    modifier = buyNowModifier,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AppColors.Purple,
                    ),
                ) {
                    BuyNowLabel()
                }
            } else {
                Button(
                    onClick = onBuyNowClick,
                    modifier = buyNowModifier,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Purple,
                        contentColor = Color.White,
                    ),
                ) {
                    BuyNowLabel()
                }
            }
        }
    }
}

@Composable
private fun BuyNowLabel() {
    Text(
        text = stringResource(R.string.buy_now),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
    )
}

@Preview(showBackground = true)
@Composable
private fun ProductDetailContentPreview() {
    MarketPlaceTheme {
        ProductDetailContent(
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
                images = listOf(
                    "https://cdn.dummyjson.com/product-images/mobile-accessories/apple-watch-series-9-gold/1.webp",
                ),
                thumbnail = "https://cdn.dummyjson.com/product-images/mobile-accessories/apple-watch-series-9-gold/1.webp",
            ),
            showDealTimer = true,
            countdown = CountdownState(hours = 3, minutes = 12, seconds = 8),
            isJoined = false,
            onJoinPoolClick = {},
            onBuyNowClick = {},
            onShareClick = {},
        )
    }
}
