package com.example.pocketmoney.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.pocketmoney.ui.screens.childedit.ChildEditScreen
import com.example.pocketmoney.ui.screens.childdetail.ChildDetailScreen
import com.example.pocketmoney.ui.screens.childlist.ChildListScreen
import com.example.pocketmoney.ui.screens.settings.SettingsScreen
import com.example.pocketmoney.ui.screens.transactionedit.TransactionEditScreen

@Composable
fun PocketMoneyNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = ChildListRoute
    ) {
        composable<ChildListRoute> {
            ChildListScreen(
                onChildClick = { childId ->
                    navController.navigate(ChildDetailRoute(childId))
                },
                onAddChild = {
                    navController.navigate(ChildEditRoute())
                },
                onSettingsClick = {
                    navController.navigate(SettingsRoute)
                }
            )
        }

        composable<ChildDetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ChildDetailRoute>()
            ChildDetailScreen(
                childId = route.childId,
                onNavigateBack = { navController.popBackStack() },
                onEditChild = { childId ->
                    navController.navigate(ChildEditRoute(childId))
                },
                onAddTransaction = { childId, sign ->
                    navController.navigate(TransactionEditRoute(childId, sign))
                }
            )
        }

        composable<ChildEditRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ChildEditRoute>()
            ChildEditScreen(
                childId = route.childId,
                onNavigateBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable<TransactionEditRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<TransactionEditRoute>()
            TransactionEditScreen(
                childId = route.childId,
                initialSign = route.initialSign,
                onDismiss = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable<SettingsRoute> {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
