package com.mariejuana.expensetracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mariejuana.expensetracker.data.ExpenseDao
import com.mariejuana.expensetracker.ui.expense.entry.ExpenseEntryDestination
import com.mariejuana.expensetracker.ui.expense.entry.ExpenseEntryScreen
import com.mariejuana.expensetracker.ui.expense.general_details.GeneralDetailsDestination
import com.mariejuana.expensetracker.ui.expense.general_details.GeneralDetailsScreen
import com.mariejuana.expensetracker.ui.expense.general_details.GeneralDetailsScreenViewModel
import com.mariejuana.expensetracker.ui.expense.yearly.YearlyDetailsScreen
import com.mariejuana.expensetracker.ui.expense.yearly.YearlyScreenDestination
import com.mariejuana.expensetracker.ui.home.HomeDestination
import com.mariejuana.expensetracker.ui.home.HomeScreen

@Composable
fun ExpenseNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController ,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToExpenseEntry = { navController.navigate(ExpenseEntryDestination.route) },
                navigateToViewExpense = { navController.navigate(GeneralDetailsDestination.route) }
            )
        }
        composable(route = ExpenseEntryDestination.route) {
            ExpenseEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(route = GeneralDetailsDestination.route) {
            GeneralDetailsScreen(
                navigateToExpensePerMonth = { /*TODO*/ },
                navigateToExpensePerYear = { navController.navigate(YearlyScreenDestination.route) },
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(route = YearlyScreenDestination.route) {
            YearlyDetailsScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}