package com.puneet.marketplace.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.puneet.marketplace.R
import com.puneet.marketplace.presentation.ui.components.EmptyList
import com.puneet.marketplace.presentation.ui.components.FlashDealsBanner
import com.puneet.marketplace.presentation.ui.components.category.CategoriesSection
import com.puneet.marketplace.presentation.ui.components.product.ProductListShimmer
import com.puneet.marketplace.presentation.ui.components.product.ProductCard
import com.puneet.marketplace.presentation.viewmodel.ProductViewModel
import kotlinx.coroutines.flow.first

@Composable
fun ProductListScreen(
    modifier: Modifier = Modifier,
    onFlashDealsClick: () -> Unit = {},
    onProductClick: (Int) -> Unit = {},
    viewModel: ProductViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> {
            ProductListShimmer(modifier = modifier)
        }
        uiState.error != null -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = uiState.error!!,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )
            }
        }
        else -> {
            val listState = rememberLazyListState()

            LaunchedEffect(uiState.selectedCategoryId, uiState.products) {
                if (uiState.products.isEmpty()) return@LaunchedEffect
                snapshotFlow { listState.layoutInfo.totalItemsCount }
                    .first { it > 0 }
                listState.scrollToItem(0)
            }

            Column(modifier = modifier.fillMaxSize()) {
                FlashDealsBanner(
                    countdown = uiState.countdown,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                    onViewAllClick = onFlashDealsClick,
                )

                CategoriesSection(
                    categories = uiState.categories,
                    selectedCategoryId = uiState.selectedCategoryId,
                    onCategorySelected = viewModel::onCategorySelected,
                    modifier = Modifier.padding(top = 4.dp),
                )

                Text(
                    text = stringResource(R.string.products),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                )

                if (uiState.products.isEmpty()) {
                    EmptyList(
                        modifier = Modifier.weight(1f),
                        title = stringResource(R.string.empty_no_products_found_title),
                        message = stringResource(R.string.empty_no_products_found_message),
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(uiState.products, key = { it.id }) { product ->
                            ProductCard(
                                product = product,
                                onClick = { onProductClick(product.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}
