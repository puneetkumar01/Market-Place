package com.puneet.marketplace.di

import com.puneet.marketplace.data.repository.ProductActionLogRepositoryImpl
import com.puneet.marketplace.data.repository.ProductRepositoryImpl
import com.puneet.marketplace.domain.repository.ProductActionLogRepository
import com.puneet.marketplace.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        impl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindProductActionLogRepository(
        impl: ProductActionLogRepositoryImpl,
    ): ProductActionLogRepository
}