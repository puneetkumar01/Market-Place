package com.puneet.marketplace.presentation.navigation

object Routes {
    const val PRODUCT_LIST = "product_list"
    const val FLASH_DEALS = "flash_deals"
    const val PRODUCT_DETAIL = "product_detail/{productId}?fromFlashDeals={fromFlashDeals}"

    fun productDetail(productId: Int, fromFlashDeals: Boolean = false): String =
        "product_detail/$productId?fromFlashDeals=$fromFlashDeals"
}
