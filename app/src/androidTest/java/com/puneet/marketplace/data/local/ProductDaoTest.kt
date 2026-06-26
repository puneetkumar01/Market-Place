package com.puneet.marketplace.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.puneet.marketplace.data.local.dao.ProductDao
import com.puneet.marketplace.data.local.database.AppDatabase
import com.puneet.marketplace.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: ProductDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = db.productDao()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun observeAllProducts_returnsOnlyNonDeal_orderedByTitle() = runTest {
        dao.insertAll(
            listOf(
                entity(id = 1, title = "Banana", discount = 5.0),
                entity(id = 2, title = "Apple", discount = 0.0),
                entity(id = 3, title = "Cherry", discount = 25.0),
            ),
        )

        val result = dao.observeAllProducts().first()

        assertEquals(listOf("Apple", "Banana"), result.map { it.title })
    }

    @Test
    fun observeFlashDealProducts_returnsDealsOrderedByDiscountDesc() = runTest {
        dao.insertAll(
            listOf(
                entity(id = 1, title = "A", discount = 10.0),
                entity(id = 2, title = "B", discount = 50.0),
                entity(id = 3, title = "C", discount = 5.0),
            ),
        )

        val result = dao.observeFlashDealProducts().first()

        assertEquals(listOf(2, 1), result.map { it.id })
    }

    @Test
    fun observeProductsByCategory_filtersBySlug() = runTest {
        dao.insertAll(
            listOf(
                entity(id = 1, title = "A", category = "phones", discount = 0.0),
                entity(id = 2, title = "B", category = "laptops", discount = 0.0),
            ),
        )

        val result = dao.observeProductsByCategory("phones").first()

        assertEquals(listOf(1), result.map { it.id })
    }

    @Test
    fun insertAll_replacesOnConflict() = runTest {
        dao.insertAll(listOf(entity(id = 1, title = "Old", discount = 0.0)))
        dao.insertAll(listOf(entity(id = 1, title = "New", discount = 0.0)))

        assertEquals("New", dao.observeProductById(1).first()?.title)
    }

    @Test
    fun observeProductById_returnsNullWhenAbsent() = runTest {
        assertNull(dao.observeProductById(404).first())
    }

    @Test
    fun stringListConverter_roundTrips() = runTest {
        val images = listOf("a", "b", "c")
        dao.insertAll(listOf(entity(id = 1, title = "A", discount = 0.0, images = images)))

        assertEquals(images, dao.observeProductById(1).first()?.images)
    }

    private fun entity(
        id: Int,
        title: String,
        discount: Double,
        category: String = "phones",
        images: List<String> = listOf("img"),
    ) = ProductEntity(
        id = id,
        title = title,
        description = "d",
        categorySlug = category,
        price = 10.0,
        discountPercentage = discount,
        stock = 1,
        availabilityStatus = "In Stock",
        minimumOrderQuantity = 1,
        images = images,
        thumbnail = "thumb",
    )
}
