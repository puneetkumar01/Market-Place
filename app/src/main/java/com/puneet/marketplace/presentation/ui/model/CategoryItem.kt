package com.puneet.marketplace.presentation.ui.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import com.puneet.marketplace.domain.model.Category

const val ALL_CATEGORY_ID = "all"

data class CategoryItem(
    val id: String,
    val label: String,
    val iconUrl: String?,
    val filterCategorySlug: String? = null,
)

fun buildCategoriesFromDb(dbCategories: List<Category>, allLabel: String): List<CategoryItem> {
    val all = CategoryItem(
        id = ALL_CATEGORY_ID,
        label = allLabel,
        iconUrl = null,
        filterCategorySlug = null,
    )
    val fromDb = dbCategories.map { category ->
        CategoryItem(
            id = category.slug,
            label = category.name,
            iconUrl = category.iconUrl.takeIf { it.isNotBlank() },
            filterCategorySlug = category.slug,
        )
    }
    return listOf(all) + fromDb
}

val AllCategoryIcon = Icons.Outlined.Apps
