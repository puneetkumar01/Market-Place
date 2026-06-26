package com.puneet.marketplace.data.repository

import com.puneet.marketplace.data.image.ImagePrefetcher
import com.puneet.marketplace.data.local.dao.CategoryDao
import com.puneet.marketplace.data.local.dao.ProductDao
import com.puneet.marketplace.data.local.entity.CategoryEntity
import com.puneet.marketplace.data.local.entity.ProductEntity
import com.puneet.marketplace.data.remote.api.ProductApiService
import com.puneet.marketplace.data.remote.dto.CategoryDto
import com.puneet.marketplace.data.remote.dto.ProductDto
import com.puneet.marketplace.data.remote.dto.ProductsResponseDto
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductRepositoryImplTest {

    private lateinit var api: ProductApiService
    private lateinit var productDao: ProductDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var imagePrefetcher: ImagePrefetcher
    private lateinit var repository: ProductRepositoryImpl

    @Before
    fun setUp() {
        api = mockk()
        productDao = mockk()
        categoryDao = mockk()
        imagePrefetcher = mockk(relaxed = true)
        repository = ProductRepositoryImpl(api, productDao, categoryDao, imagePrefetcher)
    }

    @Test
    fun `refreshProducts persists categories with derived thumbnails and prefetches images`() =
        runTest {
            coEvery { api.getCategories() } returns listOf(
                CategoryDto(slug = "phones", name = "Phones", url = "u1"),
                CategoryDto(slug = "laptops", name = "Laptops", url = "u2"),
            )
            coEvery { api.getProducts() } returns ProductsResponseDto(
                products = listOf(
                    productDto(id = 1, category = "phones", thumbnail = "", images = listOf("p1a")),
                    productDto(
                        id = 2,
                        category = "phones",
                        thumbnail = "phoneThumb",
                        images = listOf("p2a"),
                    ),
                    productDto(
                        id = 3,
                        category = "laptops",
                        thumbnail = "laptopThumb",
                        images = emptyList(),
                    ),
                ),
                total = 3,
                skip = 0,
                limit = 500,
            )

            val categorySlot = slot<List<CategoryEntity>>()
            val productSlot = slot<List<ProductEntity>>()
            val imageSlot = slot<List<String>>()
            coEvery { categoryDao.insertAll(capture(categorySlot)) } just Runs
            coEvery { productDao.insertAll(capture(productSlot)) } just Runs
            every { imagePrefetcher.prefetch(capture(imageSlot)) } just Runs

            repository.refreshProducts()

            val byCat = categorySlot.captured.associateBy { it.slug }
            assertEquals("phoneThumb", byCat["phones"]?.iconUrl)
            assertEquals("laptopThumb", byCat["laptops"]?.iconUrl)
            assertEquals(listOf(1, 2, 3), productSlot.captured.map { it.id })
            assertTrue(imageSlot.captured.contains("phoneThumb"))
            assertTrue(imageSlot.captured.contains("p1a"))
            coVerify(exactly = 1) { categoryDao.insertAll(any()) }
            coVerify(exactly = 1) { productDao.insertAll(any()) }
        }

    @Test
    fun `observeProducts maps entities to domain models`() = runTest {
        every { productDao.observeAllProducts() } returns flowOf(
            listOf(
                productEntity(id = 7, title = "Z"),
                productEntity(id = 8, title = "A"),
            ),
        )

        val result = repository.observeProducts().first()

        assertEquals(listOf(7, 8), result.map { it.id })
        assertEquals("Z", result.first().title)
    }

    @Test
    fun `observeProductsByCategory delegates to dao with slug`() = runTest {
        every { productDao.observeProductsByCategory("phones") } returns
            flowOf(listOf(productEntity(id = 1)))

        val result = repository.observeProductsByCategory("phones").first()

        assertEquals(1, result.single().id)
        coVerify { productDao.observeProductsByCategory("phones") }
    }

    @Test
    fun `observeProductById maps null entity to null product`() = runTest {
        every { productDao.observeProductById(99) } returns flowOf(null)

        assertNull(repository.observeProductById(99).first())
    }

    private fun productDto(
        id: Int,
        category: String,
        thumbnail: String,
        images: List<String>,
    ) = ProductDto(
        id = id,
        title = "P$id",
        description = "d",
        category = category,
        price = 10.0,
        discountPercentage = 0.0,
        stock = 1,
        availabilityStatus = "In Stock",
        minimumOrderQuantity = 1,
        images = images,
        thumbnail = thumbnail,
    )

    private fun productEntity(id: Int, title: String = "P$id") = ProductEntity(
        id = id,
        title = title,
        description = "d",
        categorySlug = "phones",
        price = 10.0,
        discountPercentage = 0.0,
        stock = 1,
        availabilityStatus = "In Stock",
        minimumOrderQuantity = 1,
        images = listOf("img"),
        thumbnail = "thumb",
    )
}
