package com.puneet.marketplace.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneet.marketplace.domain.FlashDealCountdownManager
import com.puneet.marketplace.domain.usecase.GetFlashDealProductsUseCase
import com.puneet.marketplace.domain.usecase.ObserveHasJoinedPoolUseCase
import com.puneet.marketplace.presentation.state.FlashDealsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FlashDealsViewModel @Inject constructor(
    private val getFlashDealProductsUseCase: GetFlashDealProductsUseCase,
    private val observeHasJoinedPoolUseCase: ObserveHasJoinedPoolUseCase,
    flashDealCountdownManager: FlashDealCountdownManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FlashDealsUiState(isLoading = true))
    val uiState: StateFlow<FlashDealsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            flashDealCountdownManager.countdown.collect { countdown ->
                _uiState.update { it.copy(countdown = countdown) }
            }
        }

        viewModelScope.launch {
            getFlashDealProductsUseCase()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                    emit(emptyList())
                }
                .flatMapLatest { products ->
                    if (products.isEmpty()) {
                        flowOf(products to emptySet())
                    } else {
                        combine(
                            products.map { product ->
                                observeHasJoinedPoolUseCase(product.id)
                                    .map { joined -> product.id to joined }
                            },
                        ) { joinedStates ->
                            val joinedProductIds = joinedStates
                                .filter { it.second }
                                .map { it.first }
                                .toSet()
                            products to joinedProductIds
                        }
                    }
                }
                .collect { (products, joinedProductIds) ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            products = products,
                            joinedProductIds = joinedProductIds,
                            error = null,
                        )
                    }
                }
        }
    }
}
