package com.puneet.marketplace.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneet.marketplace.R
import com.puneet.marketplace.domain.FlashDealCountdownManager
import com.puneet.marketplace.domain.usecase.GetCategoriesUseCase
import com.puneet.marketplace.domain.usecase.GetProductsByCategoryUseCase
import com.puneet.marketplace.domain.usecase.GetProductsUseCase
import com.puneet.marketplace.domain.usecase.RefreshProductsUseCase
import com.puneet.marketplace.presentation.state.ProductUiState
import com.puneet.marketplace.presentation.ui.model.ALL_CATEGORY_ID
import com.puneet.marketplace.presentation.ui.model.CategoryItem
import com.puneet.marketplace.presentation.ui.model.buildCategoriesFromDb
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getProductsUseCase: GetProductsUseCase,
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val refreshProductsUseCase: RefreshProductsUseCase,
    flashDealCountdownManager: FlashDealCountdownManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState(isLoading = true))
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    private val allCategoryLabel: String = context.getString(R.string.category_all)

    private val selectedCategory = MutableStateFlow(
        buildCategoriesFromDb(emptyList(), allCategoryLabel).first { it.id == ALL_CATEGORY_ID },
    )

    init {
        viewModelScope.launch {
            flashDealCountdownManager.countdown.collect { countdown ->
                _uiState.update { it.copy(countdown = countdown) }
            }
        }

        viewModelScope.launch {
            try {
                refreshProductsUseCase()
                _uiState.update { it.copy(isLoading = false, error = null) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = if (it.products.isEmpty()) {
                            context.getString(R.string.error_load_products)
                        } else {
                            null
                        },
                    )
                }
            }
        }

        viewModelScope.launch {
            getCategoriesUseCase().collect { dbCategories ->
                val categories = buildCategoriesFromDb(dbCategories, allCategoryLabel)
                _uiState.update { it.copy(categories = categories) }
            }
        }

        viewModelScope.launch {
            selectedCategory
                .flatMapLatest { category ->
                    val flow = when (val categorySlug = category.filterCategorySlug) {
                        null -> getProductsUseCase()
                        else -> getProductsByCategoryUseCase(categorySlug)
                    }
                    flow.catch {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = if (it.products.isEmpty()) {
                                    context.getString(R.string.error_load_products)
                                } else {
                                    null
                                },
                            )
                        }
                    }
                }
                .collect { products ->
                    _uiState.update {
                        it.copy(
                            products = products,
                            error = if (products.isNotEmpty()) null else it.error,
                        )
                    }
                }
        }
    }

    fun onCategorySelected(category: CategoryItem) {
        _uiState.update { it.copy(selectedCategoryId = category.id) }
        selectedCategory.value = category
    }
}
