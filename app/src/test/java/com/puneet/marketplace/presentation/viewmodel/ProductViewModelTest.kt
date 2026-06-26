package com.puneet.marketplace.presentation.viewmodel

import android.content.Context
import com.puneet.marketplace.MainDispatcherRule
import com.puneet.marketplace.R
import com.puneet.marketplace.domain.FlashDealCountdownManager
import com.puneet.marketplace.domain.model.Category
import com.puneet.marketplace.domain.model.CountdownState
import com.puneet.marketplace.domain.usecase.GetCategoriesUseCase
import com.puneet.marketplace.domain.usecase.GetProductsByCategoryUseCase
import com.puneet.marketplace.domain.usecase.GetProductsUseCase
import com.puneet.marketplace.domain.usecase.RefreshProductsUseCase
import com.puneet.marketplace.presentation.ui.model.ALL_CATEGORY_ID
import com.puneet.marketplace.presentation.ui.model.CategoryItem
import com.puneet.marketplace.product
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var context: Context
    private lateinit var getProductsUseCase: GetProductsUseCase
    private lateinit var getProductsByCategoryUseCase: GetProductsByCategoryUseCase
    private lateinit var getCategoriesUseCase: GetCategoriesUseCase
    private lateinit var refreshProductsUseCase: RefreshProductsUseCase
    private lateinit var flashDealCountdownManager: FlashDealCountdownManager

    private val allLabel = "All"
    private val loadError = "Couldn't load products. Check your connection and try again."

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        every { context.getString(R.string.category_all) } returns allLabel
        every { context.getString(R.string.error_load_products) } returns loadError

        getProductsUseCase = mockk()
        getProductsByCategoryUseCase = mockk()
        getCategoriesUseCase = mockk()
        refreshProductsUseCase = mockk()
        flashDealCountdownManager = mockk()

        every { flashDealCountdownManager.countdown } returns MutableStateFlow(CountdownState())
        every { getCategoriesUseCase() } returns flowOf(emptyList())
        every { getProductsUseCase() } returns flowOf(emptyList())
        every { getProductsByCategoryUseCase(any()) } returns flowOf(emptyList())
        coEvery { refreshProductsUseCase() } returns Unit
    }

    private fun createViewModel() = ProductViewModel(
        context = context,
        getProductsUseCase = getProductsUseCase,
        getProductsByCategoryUseCase = getProductsByCategoryUseCase,
        getCategoriesUseCase = getCategoriesUseCase,
        refreshProductsUseCase = refreshProductsUseCase,
        flashDealCountdownManager = flashDealCountdownManager,
    )

    @Test
    fun `initial state is loading before any work runs`() {
        val viewModel = createViewModel()

        val state = viewModel.uiState.value
        assertTrue(state.isLoading)
        assertTrue(state.products.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun `products are loaded and loading flag cleared on success`() = runTest {
        val products = listOf(product(id = 1), product(id = 2))
        every { getProductsUseCase() } returns flowOf(products)

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(products, state.products)
        assertTrue(!state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `refresh is triggered on init`() = runTest {
        createViewModel()
        advanceUntilIdle()

        coVerify(exactly = 1) { refreshProductsUseCase() }
    }

    @Test
    fun `categories include All option prepended to db categories`() = runTest {
        every { getCategoriesUseCase() } returns flowOf(
            listOf(
                Category(slug = "phones", name = "Phones", iconUrl = "icon-url"),
                Category(slug = "laptops", name = "Laptops", iconUrl = ""),
            ),
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        val categories = viewModel.uiState.value.categories
        assertEquals(3, categories.size)
        assertEquals(ALL_CATEGORY_ID, categories.first().id)
        assertEquals(allLabel, categories.first().label)
        assertNull(categories.first().filterCategorySlug)
        assertEquals("phones", categories[1].filterCategorySlug)
        assertNull(categories[2].iconUrl)
    }

    @Test
    fun `countdown updates are reflected in ui state`() = runTest {
        val countdown = CountdownState(hours = 2, minutes = 30, seconds = 15)
        every { flashDealCountdownManager.countdown } returns MutableStateFlow(countdown)

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(countdown, viewModel.uiState.value.countdown)
    }

    @Test
    fun `error is shown when refresh fails and product list is empty`() = runTest {
        coEvery { refreshProductsUseCase() } throws RuntimeException("network down")
        every { getProductsUseCase() } returns flowOf(emptyList())

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(!state.isLoading)
        assertEquals(loadError, state.error)
    }

    @Test
    fun `no error shown when refresh fails but cached products exist`() = runTest {
        val products = listOf(product(id = 1))
        coEvery { refreshProductsUseCase() } throws RuntimeException("network down")
        every { getProductsUseCase() } returns flowOf(products)

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(products, state.products)
        assertNull(state.error)
    }

    @Test
    fun `error is set when products flow throws and list is empty`() = runTest {
        every { getProductsUseCase() } returns flow { throw RuntimeException("db error") }

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(!state.isLoading)
        assertEquals(loadError, state.error)
    }

    @Test
    fun `selecting a category updates selectedCategoryId and queries by category`() = runTest {
        val categoryProducts = listOf(product(id = 1))
        every { getProductsByCategoryUseCase("phones") } returns flowOf(categoryProducts)

        val viewModel = createViewModel()
        advanceUntilIdle()

        val phones = CategoryItem(
            id = "phones",
            label = "Phones",
            iconUrl = null,
            filterCategorySlug = "phones",
        )
        viewModel.onCategorySelected(phones)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("phones", state.selectedCategoryId)
        assertEquals(categoryProducts, state.products)
        coVerify { getProductsByCategoryUseCase("phones") }
    }

    @Test
    fun `selecting All category queries all products`() = runTest {
        val products = listOf(product(id = 1), product(id = 2))
        every { getProductsUseCase() } returns flowOf(products)

        val viewModel = createViewModel()
        advanceUntilIdle()

        val all = CategoryItem(
            id = ALL_CATEGORY_ID,
            label = allLabel,
            iconUrl = null,
            filterCategorySlug = null,
        )
        viewModel.onCategorySelected(all)
        advanceUntilIdle()

        assertEquals(ALL_CATEGORY_ID, viewModel.uiState.value.selectedCategoryId)
        assertEquals(products, viewModel.uiState.value.products)
    }
}
