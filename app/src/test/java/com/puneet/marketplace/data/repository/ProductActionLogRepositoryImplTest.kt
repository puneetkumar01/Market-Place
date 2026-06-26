package com.puneet.marketplace.data.repository

import com.puneet.marketplace.data.local.dao.ProductActionLogDao
import com.puneet.marketplace.data.local.entity.ProductActionLogEntity
import com.puneet.marketplace.data.local.entity.ProductActionType
import com.puneet.marketplace.data.remote.api.AnalyticsApiService
import com.puneet.marketplace.product
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class ProductActionLogRepositoryImplTest {

    private lateinit var dao: ProductActionLogDao
    private lateinit var analyticsApi: AnalyticsApiService
    private lateinit var repository: ProductActionLogRepositoryImpl

    @Before
    fun setUp() {
        dao = mockk()
        analyticsApi = mockk()
        repository = ProductActionLogRepositoryImpl(dao, analyticsApi)
    }

    @Test
    fun `observeHasJoinedPool is true only when a JOIN_POOL log exists`() = runTest {
        every { dao.observeByProduct(1) } returns flowOf(
            listOf(log(actionType = ProductActionType.SHARE)),
        )
        assertFalse(repository.observeHasJoinedPool(1).first())

        every { dao.observeByProduct(2) } returns flowOf(
            listOf(log(actionType = ProductActionType.JOIN_POOL)),
        )
        assertTrue(repository.observeHasJoinedPool(2).first())
    }

    @Test
    fun `logJoinPool inserts entity with discounted price and correct type`() = runTest {
        val captured = slot<ProductActionLogEntity>()
        coEvery { dao.insert(capture(captured)) } just Runs

        repository.logJoinPool(
            product(
                id = 5,
                title = "Gizmo",
                price = 200.0,
                discountPercentage = 10.0,
            ),
        )

        with(captured.captured) {
            assertEquals(5, productId)
            assertEquals("Gizmo", productTitle)
            assertEquals(ProductActionType.JOIN_POOL, actionType)
            assertEquals(180.0, priceAtAction, 0.0001)
        }
    }

    @Test
    fun `uploadPendingLogs returns zero and skips api when nothing pending`() = runTest {
        coEvery { dao.getAllPending() } returns emptyList()

        val result = repository.uploadPendingLogs()

        assertEquals(0, result.getOrNull())
        coVerify(exactly = 0) { analyticsApi.uploadAnalytics(any(), any()) }
    }

    @Test
    fun `uploadPendingLogs marks uploaded on success`() = runTest {
        val pending = listOf(log(id = 1), log(id = 2))
        coEvery { dao.getAllPending() } returns pending
        coEvery { analyticsApi.uploadAnalytics(any(), any()) } returns Response.success(Unit)
        coEvery { dao.markAsUploaded(any()) } just Runs

        val result = repository.uploadPendingLogs()

        assertEquals(2, result.getOrNull())
        coVerify(exactly = 1) { dao.markAsUploaded(listOf(1L, 2L)) }
    }

    @Test
    fun `uploadPendingLogs fails and does not mark uploaded on http error`() = runTest {
        coEvery { dao.getAllPending() } returns listOf(log(id = 1))
        coEvery { analyticsApi.uploadAnalytics(any(), any()) } returns
            Response.error(500, "".toResponseBody(null))

        val result = repository.uploadPendingLogs()

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { dao.markAsUploaded(any()) }
    }

    @Test
    fun `uploadPendingLogs wraps exceptions into failure`() = runTest {
        coEvery { dao.getAllPending() } throws RuntimeException("db down")

        assertTrue(repository.uploadPendingLogs().isFailure)
    }

    private fun log(
        id: Long = 0,
        actionType: ProductActionType = ProductActionType.JOIN_POOL,
    ) = ProductActionLogEntity(
        id = id,
        productId = 1,
        actionType = actionType,
        productTitle = "P",
        priceAtAction = 10.0,
        timestamp = 1000L,
        uploaded = false,
    )
}
