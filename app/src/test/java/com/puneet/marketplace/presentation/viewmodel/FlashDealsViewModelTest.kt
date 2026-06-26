package com.puneet.marketplace.presentation.viewmodel

import com.puneet.marketplace.MainDispatcherRule
import com.puneet.marketplace.domain.FlashDealCountdownManager
import com.puneet.marketplace.domain.model.CountdownState
import com.puneet.marketplace.domain.usecase.GetFlashDealProductsUseCase
import com.puneet.marketplace.domain.usecase.ObserveHasJoinedPoolUseCase
import com.puneet.marketplace.product
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FlashDealsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getFlashDealProductsUseCase: GetFlashDealProductsUseCase
    private lateinit var observeHasJoinedPoolUseCase: ObserveHasJoinedPoolUseCase
    private lateinit var flashDealCountdownManager: FlashDealCountdownManager

    @Before
    fun setUp() {
        getFlashDealProductsUseCase = mockk()
        observeHasJoinedPoolUseCase = mockk()
        flashDealCountdownManager = mockk()
        every { flashDealCountdownManager.countdown } returns MutableStateFlow(CountdownState())
        every { observeHasJoinedPoolUseCase(any()) } returns flowOf(false)
    }

    private fun createViewModel() = FlashDealsViewModel(
        getFlashDealProductsUseCase = getFlashDealProductsUseCase,
        observeHasJoinedPoolUseCase = observeHasJoinedPoolUseCase,
        flashDealCountdownManager = flashDealCountdownManager,
    )

    @Test
    fun `empty deals produce empty state without loading`() = runTest {
        every { getFlashDealProductsUseCase() } returns flowOf(emptyList())

        val vm = createViewModel()
        advanceUntilIdle()

        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.products.isEmpty())
        assertTrue(state.joinedProductIds.isEmpty())
    }

    @Test
    fun `products map to state and joined ids are derived per product`() = runTest {
        val products = listOf(product(id = 1), product(id = 2), product(id = 3))
        every { getFlashDealProductsUseCase() } returns flowOf(products)
        every { observeHasJoinedPoolUseCase(1) } returns flowOf(true)
        every { observeHasJoinedPoolUseCase(2) } returns flowOf(false)
        every { observeHasJoinedPoolUseCase(3) } returns flowOf(true)

        val vm = createViewModel()
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals(products, state.products)
        assertEquals(setOf(1, 3), state.joinedProductIds)
        assertFalse(state.isLoading)
    }

    @Test
    fun `flow error sets error message then recovers to empty list`() = runTest {
        every { getFlashDealProductsUseCase() } returns flow { throw RuntimeException("net") }

        val vm = createViewModel()
        advanceUntilIdle()

        assertTrue(vm.uiState.value.products.isEmpty())
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun `countdown updates flow into ui state`() = runTest {
        every { getFlashDealProductsUseCase() } returns flowOf(emptyList())
        every { flashDealCountdownManager.countdown } returns
            MutableStateFlow(CountdownState(hours = 1, minutes = 10))

        val vm = createViewModel()
        advanceUntilIdle()

        assertEquals(1L, vm.uiState.value.countdown.hours)
        assertEquals(10L, vm.uiState.value.countdown.minutes)
    }
}
