package com.puneet.marketplace.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.puneet.marketplace.data.local.dao.CategoryDao
import com.puneet.marketplace.data.local.database.AppDatabase
import com.puneet.marketplace.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CategoryDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: CategoryDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = db.categoryDao()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun observeAll_returnsCategoriesOrderedByName() = runTest {
        dao.insertAll(
            listOf(
                CategoryEntity(slug = "phones", name = "Phones", iconUrl = "p"),
                CategoryEntity(slug = "laptops", name = "Laptops", iconUrl = "l"),
                CategoryEntity(slug = "accessories", name = "Accessories", iconUrl = "a"),
            ),
        )

        val result = dao.observeAll().first()

        assertEquals(listOf("Accessories", "Laptops", "Phones"), result.map { it.name })
    }

    @Test
    fun insertAll_replacesOnConflict() = runTest {
        dao.insertAll(listOf(CategoryEntity(slug = "phones", name = "Old", iconUrl = "")))
        dao.insertAll(listOf(CategoryEntity(slug = "phones", name = "New", iconUrl = "icon")))

        val result = dao.observeAll().first()

        assertEquals("New", result.single().name)
        assertEquals("icon", result.single().iconUrl)
    }
}
