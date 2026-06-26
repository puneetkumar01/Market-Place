package com.puneet.marketplace.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.puneet.marketplace.MainDispatcherRule
import com.puneet.marketplace.R
import com.puneet.marketplace.domain.FlashDealCountdownManager
import com.puneet.marketplace.domain.model.CountdownState
import com.puneet.marketplace.domain.usecase.GetProductByIdUseCase
import com.puneet.marketplace.domain.usecase.LogProductActionUseCase
import com.puneet.marketplace.domain.usecase.ObserveHasJoinedPoolUseCase
import com.puneet.marketplace.product
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var context: Context
    private lateinit var getProductByIdUseCase: GetProductByIdUseCase
    private lateinit var logProductActionUseCase: LogProductActionUseCase
    private lateinit var observeHasJoinedPoolUseCase: ObserveHasJoinedPoolUseCase
    private lateinit var flashDealCountdownManager: FlashDealCountdownManager

    private val notFound = "Product not found"

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        every { context.getString(R.string.product_not_found) } returns notFound

        getProductByIdUseCase = mockk()
        logProductActionUseCase = mockk(relaxed = true)
        observeHasJoinedPoolUseCase = mockk()
        flashDealCountdownManager = mockk()

        every { observeHasJoinedPoolUseCase(any()) } returns flowOf(false)
        every { flashDealCountdownManager.countdown } returns MutableStateFlow(CountdownState())
    }

    private fun createViewModel(
        productId: Int = 1,
        fromFlashDeals: Boolean = false,
    ) = ProductDetailViewModel(
        context = context,
        savedStateHandle = SavedStateHandle(
            mapOf("productId" to productId, "fromFlashDeals" to fromFlashDeals),
        ),
        getProductByIdUseCase = getProductByIdUseCase,
        logProductActionUseCase = logProductActionUseCase,
        observeHasJoinedPoolUseCase = observeHasJoinedPoolUseCase,
        flashDealCountdownManager = flashDealCountdownManager,
    )

    @Test
    fun `product loaded maps into ui state and clears loading`() = runTest {
        val p = product(id = 1)
        every { getProductByIdUseCase(1) } returns flowOf(p)

        val vm = createViewModel(productId = 1)
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals(p, state.product)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `null product maps to not-found error`() = runTest {
        every { getProductByIdUseCase(1) } returns flowOf(null)

        val vm = createViewModel(productId = 1)
        advanceUntilIdle()

        val state = vm.uiState.value
        assertNull(state.product)
        assertFalse(state.isLoading)
        assertEquals(notFound, state.error)
    }

    @Test
    fun `flow error maps exception message into error`() = runTest {
        every { getProductByIdUseCase(1) } returns flow { throw RuntimeException("boom") }

        val vm = createViewModel(productId = 1)
        advanceUntilIdle()

        assertEquals("boom", vm.uiState.value.error)
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun `joined pool state is reflected in ui state`() = runTest {
        every { getProductByIdUseCase(1) } returns flowOf(product(id = 1))
        every { observeHasJoinedPoolUseCase(1) } returns flowOf(true)

        val vm = createViewModel(productId = 1)
        advanceUntilIdle()

        assertTrue(vm.uiState.value.isJoined)
    }

    @Test
    fun `countdown collected only when navigated from flash deals`() = runTest {
        every { getProductByIdUseCase(1) } returns flowOf(product(id = 1))

        createViewModel(productId = 1, fromFlashDeals = false)
        advanceUntilIdle()
        verify(exactly = 0) { flashDealCountdownManager.countdown }

        every { flashDealCountdownManager.countdown } returns
            MutableStateFlow(CountdownState(minutes = 5))
        val vm = createViewModel(productId = 1, fromFlashDeals = true)
        advanceUntilIdle()
        assertEquals(5L, vm.uiState.value.countdown.minutes)
    }

    @Test
    fun `logJoinPool is a no-op when already joined`() = runTest {
        every { getProductByIdUseCase(1) } returns flowOf(product(id = 1))
        every { observeHasJoinedPoolUseCase(1) } returns flowOf(true)

        val vm = createViewModel(productId = 1)
        advanceUntilIdle()
        vm.logJoinPool()
        advanceUntilIdle()

        coVerify(exactly = 0) { logProductActionUseCase.joinPool(any()) }
    }

    @Test
    fun `logJoinPool logs action when product present and not joined`() = runTest {
        val p = product(id = 1)
        every { getProductByIdUseCase(1) } returns flowOf(p)
        every { observeHasJoinedPoolUseCase(1) } returns flowOf(false)

        val vm = createViewModel(productId = 1)
        advanceUntilIdle()
        vm.logJoinPool()
        advanceUntilIdle()

        coVerify(exactly = 1) { logProductActionUseCase.joinPool(p) }
    }

    @Test
    fun `logShare and logBuyNow delegate to use case`() = runTest {
        val p = product(id = 1)
        every { getProductByIdUseCase(1) } returns flowOf(p)

        val vm = createViewModel(productId = 1)
        advanceUntilIdle()
        vm.logShare()
        vm.logBuyNow()
        advanceUntilIdle()

        coVerify(exactly = 1) { logProductActionUseCase.share(p) }
        coVerify(exactly = 1) { logProductActionUseCase.buyNow(p) }
    }

    @Test
    fun `action logging is skipped when product is null`() = runTest {
        every { getProductByIdUseCase(1) } returns flowOf(null)

        val vm = createViewModel(productId = 1)
        advanceUntilIdle()
        vm.logShare()
        advanceUntilIdle()

        coVerify(exactly = 0) { logProductActionUseCase.share(any()) }
    }
}
