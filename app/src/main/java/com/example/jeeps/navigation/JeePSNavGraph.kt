package com.example.jeeps.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jeeps.ui.screens.HomeScreen
import com.example.jeeps.ui.screens.RouteDetailScreen
import com.example.jeeps.ui.screens.RouteResultsScreen
import com.example.jeeps.ui.screens.TerminalsScreen
import com.example.jeeps.ui.viewmodels.HomeViewModel

sealed class Screen(val route: String) {
    object Home      : Screen("home")
    object Terminals : Screen("terminals")

    object RouteResults : Screen("route_results/{originId}/{destId}/{originName}/{destName}") {
        fun createRoute(originId: Int, destId: Int, originName: String, destName: String) =
            "route_results/$originId/$destId/$originName/$destName"
    }

    object RouteDetail : Screen("route_detail/{routeId}") {
        fun createRoute(routeId: Int) = "route_detail/$routeId"
    }
}

@Composable
fun JeePSNavGraph(
    navController  : NavHostController   = rememberNavController(),
    viewModel      : HomeViewModel       = viewModel(),
    darkMode       : Boolean             = false,
    onDarkChange   : (Boolean) -> Unit   = {},
    showSettings   : Boolean             = false,
    onShowSettings : (Boolean) -> Unit   = {},
) {
    NavHost(
        navController       = navController,
        startDestination    = Screen.Home.route,
        enterTransition     = { EnterTransition.None },
        exitTransition      = { ExitTransition.None },
        popEnterTransition  = { EnterTransition.None },
        popExitTransition   = { ExitTransition.None },
    ) {

        composable(Screen.Home.route) {
            HomeScreen(
                viewModel         = viewModel,
                onFindRoutes      = { oId: Int, dId: Int, oName: String, dName: String ->
                    navController.navigate(
                        Screen.RouteResults.createRoute(oId, dId, oName, dName)
                    )
                },
                onSeeAllTerminals = {
                    navController.navigate(Screen.Terminals.route)
                },
                darkMode       = darkMode,
                onDarkChange   = onDarkChange,
                showSettings   = showSettings,
                onShowSettings = onShowSettings,
            )
        }

        composable(Screen.Terminals.route) {
            TerminalsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.RouteResults.route) { backStackEntry ->
            val originId    = backStackEntry.arguments?.getString("originId")?.toIntOrNull() ?: 0
            val destId      = backStackEntry.arguments?.getString("destId")?.toIntOrNull()   ?: 0
            val originName  = backStackEntry.arguments?.getString("originName") ?: ""
            val destName    = backStackEntry.arguments?.getString("destName")   ?: ""
            
            RouteResultsScreen(
                originId        = originId,
                destId          = destId,
                originName      = originName,
                destinationName = destName,
                onBack          = { navController.popBackStack() },
                onRouteSelected = { routeId: Int ->
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
