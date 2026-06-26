package com.puneet.marketplace.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneet.marketplace.R
import com.puneet.marketplace.domain.FlashDealCountdownManager
import com.puneet.marketplace.domain.usecase.GetProductByIdUseCase
import com.puneet.marketplace.domain.usecase.LogProductActionUseCase
import com.puneet.marketplace.domain.usecase.ObserveHasJoinedPoolUseCase
import com.puneet.marketplace.presentation.state.ProductDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val logProductActionUseCase: LogProductActionUseCase,
    private val observeHasJoinedPoolUseCase: ObserveHasJoinedPoolUseCase,
    flashDealCountdownManager: FlashDealCountdownManager,
) : ViewModel() {

    private val productId: Int = checkNotNull(savedStateHandle["productId"])
    private val fromFlashDeals: Boolean = savedStateHandle["fromFlashDeals"] ?: false

    private val _uiState = MutableStateFlow(ProductDetailUiState(isLoading = true))
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    init {
        if (fromFlashDeals) {
            viewModelScope.launch {
                flashDealCountdownManager.countdown.collect { countdown ->
                    _uiState.update { it.copy(countdown = countdown) }
                }
            }
        }

        viewModelScope.launch {
            getProductByIdUseCase(productId)
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { product ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            product = product,
                            error = if (product == null) {
                                context.getString(R.string.product_not_found)
                            } else {
                                null
                            },
                        )
                    }
                }
        }

        viewModelScope.launch {
            observeHasJoinedPoolUseCase(productId).collect { joined ->
                _uiState.update { it.copy(isJoined = joined) }
            }
        }
    }

    fun logJoinPool() {
        val product = _uiState.value.product ?: return
        if (_uiState.value.isJoined) return

        viewModelScope.launch {
            logProductActionUseCase.joinPool(product)
        }
    }

    fun logShare() {
        val product = _uiState.value.product ?: return
        viewModelScope.launch {
            logProductActionUseCase.share(product)
        }
    }

    fun logBuyNow() {
        val product = _uiState.value.product ?: return
        viewModelScope.launch {
            logProductActionUseCase.buyNow(product)
        }
    }
}
