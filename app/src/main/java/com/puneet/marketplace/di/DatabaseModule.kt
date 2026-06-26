package com.puneet.marketplace.di

import android.content.Context
import androidx.room.Room
import com.puneet.marketplace.data.local.dao.CategoryDao
import com.puneet.marketplace.data.local.dao.ProductActionLogDao
import com.puneet.marketplace.data.local.dao.ProductDao
import com.puneet.marketplace.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "marketplace_db",
        )
            .build()

    @Provides
    fun provideProductDao(database: AppDatabase): ProductDao =
        database.productDao()

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao =
        database.categoryDao()

    @Provides
    fun provideProductActionLogDao(database: AppDatabase): ProductActionLogDao =
        database.productActionLogDao()
}
