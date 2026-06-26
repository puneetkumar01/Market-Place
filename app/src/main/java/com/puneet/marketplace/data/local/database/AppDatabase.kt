package com.puneet.marketplace.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.puneet.marketplace.data.local.dao.CategoryDao
import com.puneet.marketplace.data.local.dao.ProductActionLogDao
import com.puneet.marketplace.data.local.dao.ProductDao
import com.puneet.marketplace.data.local.entity.CategoryEntity
import com.puneet.marketplace.data.local.entity.ProductActionLogEntity
import com.puneet.marketplace.data.local.entity.ProductEntity

@Database(
    entities = [
        ProductEntity::class,
        CategoryEntity::class,
        ProductActionLogEntity::class,
    ],
    version = 1,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    abstract fun categoryDao(): CategoryDao

    abstract fun productActionLogDao(): ProductActionLogDao
}
