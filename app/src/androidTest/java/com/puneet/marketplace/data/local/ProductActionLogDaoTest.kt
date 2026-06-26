package com.puneet.marketplace.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.puneet.marketplace.data.local.dao.ProductActionLogDao
import com.puneet.marketplace.data.local.database.AppDatabase
import com.puneet.marketplace.data.local.entity.ProductActionLogEntity
import com.puneet.marketplace.data.local.entity.ProductActionType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductActionLogDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: ProductActionLogDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = db.productActionLogDao()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun insert_autoGeneratesId_andObserveByProductOrdersByTimestampDesc() = runTest {
        dao.insert(log(productId = 1, timestamp = 100, type = ProductActionType.SHARE))
        dao.insert(log(productId = 1, timestamp = 300, type = ProductActionType.JOIN_POOL))
        dao.insert(log(productId = 1, timestamp = 200, type = ProductActionType.BUY_NOW))

        val logs = dao.observeByProduct(1).first()

        assertEquals(
            listOf(300L, 200L, 100L),
            logs.map { it.timestamp },
        )
        assertTrue(logs.all { it.id != 0L })
    }

    @Test
    fun getAllPending_returnsOnlyNotUploaded_andMarkAsUploadedUpdates() = runTest {
        dao.insert(log(productId = 1, timestamp = 100, uploaded = false))
        dao.insert(log(productId = 2, timestamp = 200, uploaded = true))

        val pending = dao.getAllPending()
        assertEquals(1, pending.size)

        dao.markAsUploaded(pending.map { it.id })

        assertTrue(dao.getAllPending().isEmpty())
    }

    private fun log(
        productId: Int,
        timestamp: Long,
        type: ProductActionType = ProductActionType.JOIN_POOL,
        uploaded: Boolean = false,
    ) = ProductActionLogEntity(
        productId = productId,
        actionType = type,
        productTitle = "P$productId",
        priceAtAction = 9.99,
        timestamp = timestamp,
        uploaded = uploaded,
    )
}
