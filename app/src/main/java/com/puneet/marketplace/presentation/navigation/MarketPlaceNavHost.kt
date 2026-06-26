package com.puneet.marketplace.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.puneet.marketplace.presentation.ui.screen.FlashDealsScreen
import com.puneet.marketplace.presentation.ui.screen.ProductDetailScreen
import com.puneet.marketplace.presentation.ui.screen.ProductListScreen

@Composable
fun MarketPlaceNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.PRODUCT_LIST,
        modifier = modifier,
    ) {
        composable(Routes.PRODUCT_LIST) {
            ProductListScreen(
                onFlashDealsClick = { navController.navigate(Routes.FLASH_DEALS) },
                onProductClick = { productId ->
                    navController.navigate(Routes.productDetail(productId))
                },
            )
        }
        composable(Routes.FLASH_DEALS) {
            FlashDealsScreen(
                onProductClick = { productId ->
                    navController.navigate(Routes.productDetail(productId, fromFlashDeals = true))
                },
            )
        }
        composable(
            route = Routes.PRODUCT_DETAIL,
            arguments = listOf(
                navArgument("productId") { type = NavType.IntType },
                navArgument("fromFlashDeals") {
                    type = NavType.BoolType
                    defaultValue = false
                },
            ),
        ) { backStackEntry ->
            val fromFlashDeals = backStackEntry.arguments?.getBoolean("fromFlashDeals") ?: false
            ProductDetailScreen(showDealTimer = fromFlashDeals)
        }
    }
}
