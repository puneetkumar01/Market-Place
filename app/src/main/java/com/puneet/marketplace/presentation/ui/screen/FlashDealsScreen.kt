package com.puneet.marketplace.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.puneet.marketplace.R
import com.puneet.marketplace.presentation.ui.components.EmptyList
import com.puneet.marketplace.presentation.ui.components.flashDealProduct.FlashDealProductCard
import com.puneet.marketplace.presentation.ui.components.common.LoadingIndicator
import com.puneet.marketplace.presentation.viewmodel.FlashDealsViewModel

@Composable
fun FlashDealsScreen(
    modifier: Modifier = Modifier,
    onProductClick: (Int) -> Unit = {},
    viewModel: FlashDealsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> {
            LoadingIndicator(modifier = modifier)
        }
        uiState.error != null -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(uiState.error!!)
            }
        }
        uiState.products.isEmpty() -> {
            EmptyList(
                modifier = modifier,
                title = stringResource(R.string.empty_no_flash_deals_title),
                message = stringResource(R.string.empty_no_flash_deals_message),
            )
        }
        else -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(uiState.products, key = { it.id }) { product ->
                    FlashDealProductCard(
                        product = product,
                        countdown = uiState.countdown,
                        isJoined = product.id in uiState.joinedProductIds,
                        onClick = { onProductClick(product.id) },
                    )
                }
            }
        }
    }
}
