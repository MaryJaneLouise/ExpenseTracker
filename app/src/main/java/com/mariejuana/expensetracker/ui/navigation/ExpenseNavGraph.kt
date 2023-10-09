package com.mariejuana.expensetracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mariejuana.expensetracker.ui.expense.AllExpensesScreen
import com.mariejuana.expensetracker.ui.expense.AllExpensesScreenDestination
import com.mariejuana.expensetracker.ui.expense.entry.ExpenseEntryDestination
import com.mariejuana.expensetracker.ui.expense.entry.ExpenseEntryScreen
import com.mariejuana.expensetracker.ui.expense.details.GeneralDetailsDestination
import com.mariejuana.expensetracker.ui.expense.details.GeneralDetailsScreen
import com.mariejuana.expensetracker.ui.expense.details.item.ExpenseDetailsScreen
import com.mariejuana.expensetracker.ui.expense.details.item.ItemDetailsDestination
import com.mariejuana.expensetracker.ui.expense.monthly.MonthlyDetailsScreen
import com.mariejuana.expensetracker.ui.expense.monthly.MonthlyScreenDestination
import com.mariejuana.expensetracker.ui.expense.monthly.per_month.PerMonthDetailsScreen
import com.mariejuana.expensetracker.ui.expense.monthly.per_month.PerMonthScreenDestination
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
                navigateToExpensePerMonth = { navController. navigate(MonthlyScreenDestination.route)},
                navigateToExpensePerYear = { navController.navigate(YearlyScreenDestination.route) },
                navigateToAllExpense = { navController.navigate(AllExpensesScreenDestination.route) },
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(route = AllExpensesScreenDestination.route) {
            AllExpensesScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                navigateToExpenseDetails = { navController.navigate("${ItemDetailsDestination.route}/${it}")},
            )
        }
        composable(route = MonthlyScreenDestination.route) {
            MonthlyDetailsScreen(
                perMonthNavigate = { navController.navigate("${PerMonthScreenDestination.route}/${it}")},
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(
            route = PerMonthScreenDestination.routeWithArgs,
            arguments = listOf(navArgument(PerMonthScreenDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            PerMonthDetailsScreen(
                onNavigateUp = { navController.navigateUp() },
                navigateBack = { navController.popBackStack() },
                navigateToExpenseDetails = { navController.navigate("${ItemDetailsDestination.route}/${it}")},
                navController = navController
            )
        }
        composable(
            route = ItemDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(ItemDetailsDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            ExpenseDetailsScreen(
                onNavigateUp = { navController.navigateUp() },
                navigateBack = { navController.popBackStack() },
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