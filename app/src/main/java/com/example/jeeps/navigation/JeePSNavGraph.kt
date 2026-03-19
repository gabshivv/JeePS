package com.example.jeeps.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jeeps.ui.screens.HomeScreen
import com.example.jeeps.ui.screens.RouteDetailScreen
import com.example.jeeps.ui.screens.RouteResultsScreen

// ── Screen routes ────────────────────────────────────────

sealed class Screen(val route: String) {
    object Home : Screen("home")

    object RouteResults : Screen("route_results/{origin}/{destination}") {
        fun createRoute(origin: String, destination: String) =
            "route_results/$origin/$destination"
    }

    object RouteDetail : Screen("route_detail/{routeId}") {
        fun createRoute(routeId: Int) = "route_detail/$routeId"
    }
}

// ── Nav graph ────────────────────────────────────────────

@Composable
fun JeePSNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController  = navController,
        startDestination = Screen.Home.route,
    ) {

        composable(Screen.Home.route) {
            HomeScreen(
                onFindRoutes = { origin, destination ->
                    navController.navigate(
                        Screen.RouteResults.createRoute(origin, destination)
                    )
                }
            )
        }

        composable(Screen.RouteResults.route) { backStackEntry ->
            val origin      = backStackEntry.arguments?.getString("origin")      ?: ""
            val destination = backStackEntry.arguments?.getString("destination") ?: ""
            RouteResultsScreen(
                origin      = origin,
                destination = destination,
                onBack      = { navController.popBackStack() },
                onRouteSelected = { routeId ->
                    navController.navigate(Screen.RouteDetail.createRoute(routeId))
                },
            )
        }

        composable(Screen.RouteDetail.route) { backStackEntry ->
            val routeId = backStackEntry.arguments
                ?.getString("routeId")?.toIntOrNull() ?: 0
            RouteDetailScreen(
                routeId = routeId,
                onBack  = { navController.popBackStack() },
            )
        }
    }
}