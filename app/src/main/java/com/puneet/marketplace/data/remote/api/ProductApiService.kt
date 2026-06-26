package com.puneet.marketplace.data.remote.api

import com.puneet.marketplace.data.remote.dto.CategoryDto
import com.puneet.marketplace.data.remote.dto.ProductsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductApiService {

  @GET("products")
  suspend fun getProducts(
    @Query("limit") limit: Int = 500,
  ): ProductsResponseDto

  @GET("products/categories")
  suspend fun getCategories(): List<CategoryDto>
}
