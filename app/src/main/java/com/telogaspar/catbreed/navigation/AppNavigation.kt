package com.telogaspar.catbreed.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.telogaspar.catbreed.breedList.presentation.BreedDetailScreen
import com.telogaspar.catbreed.breedList.presentation.BreedListScreen
import com.telogaspar.catbreed.core.theme.LocalAppColors
import com.telogaspar.catbreed.core.theme.LocalAppFonts
import com.telogaspar.catbreed.feature.favourites.presentation.FavouritesScreen

private const val ROUTE_BREEDS     = "breeds"
private const val ROUTE_DETAIL     = "breed_detail/{breedId}"
private const val ROUTE_FAVOURITES = "favourites"

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val colors = LocalAppColors.current
    val fonts = LocalAppFonts.current

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route != ROUTE_DETAIL

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = colors.card) {
                    val breedsSelected = currentDestination?.hierarchy?.any { it.route == ROUTE_BREEDS } == true
                    val favsSelected   = currentDestination?.hierarchy?.any { it.route == ROUTE_FAVOURITES } == true

                    NavigationBarItem(
                        selected = breedsSelected,
                        onClick = {
                            navController.navigate(ROUTE_BREEDS) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                Icons.Rounded.Pets,
                                contentDescription = "Breeds",
                                tint = if (breedsSelected) colors.goldDeep else colors.ink3,
                            )
                        },
                        label = {
                            Text(
                                "Breeds",
                                fontFamily = fonts.sans,
                                fontSize = 11.sp,
                                fontWeight = if (breedsSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (breedsSelected) colors.goldDeep else colors.ink3,
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(indicatorColor = colors.ghost),
                    )

                    NavigationBarItem(
                        selected = favsSelected,
                        onClick = {
                            navController.navigate(ROUTE_FAVOURITES) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                if (favsSelected) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                contentDescription = "Favourites",
                                tint = if (favsSelected) colors.goldDeep else colors.ink3,
                            )
                        },
                        label = {
                            Text(
                                "Favourites",
                                fontFamily = fonts.sans,
                                fontSize = 11.sp,
                                fontWeight = if (favsSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (favsSelected) colors.goldDeep else colors.ink3,
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(indicatorColor = colors.ghost),
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ROUTE_BREEDS,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(ROUTE_BREEDS) {
                BreedListScreen(
                    onBreedClick = { breedId -> navController.navigate("breed_detail/$breedId") }
                )
            }

            composable(
                route = ROUTE_DETAIL,
                arguments = listOf(navArgument("breedId") { type = NavType.StringType }),
                enterTransition    = { slideInHorizontally(animationSpec = tween(340)) { it } },
                exitTransition     = { slideOutHorizontally(animationSpec = tween(340)) { it } },
                popEnterTransition = { slideInHorizontally(animationSpec = tween(340)) { -it } },
                popExitTransition  = { slideOutHorizontally(animationSpec = tween(340)) { it } },
            ) {
                BreedDetailScreen(onBack = { navController.popBackStack() })
            }

            composable(ROUTE_FAVOURITES) {
                FavouritesScreen()
            }
        }
    }
}
