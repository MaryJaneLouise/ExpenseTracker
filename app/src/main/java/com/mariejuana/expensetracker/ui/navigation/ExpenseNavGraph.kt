package com.mariejuana.expensetracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mariejuana.expensetracker.ui.budget.BudgetDestination
import com.mariejuana.expensetracker.ui.budget.BudgetScreen
import com.mariejuana.expensetracker.ui.budget.entry.BudgetEntryDestination
import com.mariejuana.expensetracker.ui.budget.entry.BudgetEntryScreen
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
import com.mariejuana.expensetracker.ui.settings.SettingScreen
import com.mariejuana.expensetracker.ui.settings.SettingsDestination

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
                navigateToViewExpense = { navController.navigate(GeneralDetailsDestination.route) },
                navigateBack = { navController.popBackStack() },
                navigateToCurrentBudget = { navController.navigate(BudgetDestination.route) },
                navigateToViewAllExpense = { navController.navigate(AllExpensesScreenDestination.route) },
                navigateToSettings = { navController.navigate(SettingsDestination.route) }
            )
        }
        composable(route = SettingsDestination.route) {
            SettingScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
            )
        }
        composable(route = BudgetDestination.route) {
            BudgetScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                navigateToAddBudget = { navController.navigate(BudgetEntryDestination.route) }
            )
        }
        composable(route = BudgetEntryDestination.route) {
            BudgetEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
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