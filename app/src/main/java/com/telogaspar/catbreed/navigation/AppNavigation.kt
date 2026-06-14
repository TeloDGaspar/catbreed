package com.telogaspar.catbreed.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.telogaspar.catbreed.breedList.presentation.BreedDetailScreen
import com.telogaspar.catbreed.breedList.presentation.BreedListScreen

private const val ROUTE_LIST   = "breed_list"
private const val ROUTE_DETAIL = "breed_detail/{breedId}"

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ROUTE_LIST,
    ) {
        composable(ROUTE_LIST) {
            BreedListScreen(
                onBreedClick = { breedId ->
                    navController.navigate("breed_detail/$breedId")
                }
            )
        }

        composable(
            route = ROUTE_DETAIL,
            arguments = listOf(navArgument("breedId") { type = NavType.StringType }),
            enterTransition = { slideInHorizontally(animationSpec = tween(340)) { it } },
            exitTransition  = { slideOutHorizontally(animationSpec = tween(340)) { it } },
            popEnterTransition  = { slideInHorizontally(animationSpec = tween(340)) { -it } },
            popExitTransition   = { slideOutHorizontally(animationSpec = tween(340)) { it } },
        ) {
            BreedDetailScreen(onBack = { navController.popBackStack() })
        }
    }
}
