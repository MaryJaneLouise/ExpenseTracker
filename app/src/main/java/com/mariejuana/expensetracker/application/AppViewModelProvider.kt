package com.mariejuana.expensetracker.application

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mariejuana.expensetracker.ui.budget.BudgetScreenViewModel
import com.mariejuana.expensetracker.ui.budget.entry.BudgetEntryScreenViewModel
import com.mariejuana.expensetracker.ui.expense.AllExpensesScreenViewModel
import com.mariejuana.expensetracker.ui.expense.entry.EntryScreenViewModel
import com.mariejuana.expensetracker.ui.expense.details.GeneralDetailsScreenViewModel
import com.mariejuana.expensetracker.ui.expense.details.item.ItemDetailsScreenViewModel
import com.mariejuana.expensetracker.ui.expense.monthly.MonthlyScreenViewModel
import com.mariejuana.expensetracker.ui.expense.monthly.per_month.PerMonthScreenViewModel
import com.mariejuana.expensetracker.ui.expense.yearly.YearlyScreenViewModel
import com.mariejuana.expensetracker.ui.home.HomeScreenViewModel
import com.mariejuana.expensetracker.ui.settings.SettingScreenViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
//        // Initializer for ItemEditViewModel
//        initializer {
//            EditScreenViewModel(
//                this.createSavedStateHandle(),
//                inventoryApplication().container.itemsRepository
//            )
//        }
        initializer {
            BudgetScreenViewModel(expenseApplication().container.expenseRepository)
        }

        initializer {
            BudgetEntryScreenViewModel(expenseApplication().container.expenseRepository)
        }

        initializer {
            GeneralDetailsScreenViewModel(expenseApplication().container.expenseRepository)
        }

        initializer {
            AllExpensesScreenViewModel(expenseApplication().container.expenseRepository)
        }

        initializer {
            YearlyScreenViewModel(expenseApplication().container.expenseRepository)
        }

        initializer {
            MonthlyScreenViewModel(expenseApplication().container.expenseRepository)
        }

        initializer {
            PerMonthScreenViewModel(this.createSavedStateHandle(), expenseApplication().container.expenseRepository)
        }

        initializer {
            ItemDetailsScreenViewModel(this.createSavedStateHandle(), expenseApplication().container.expenseRepository)
        }

        initializer {
            EntryScreenViewModel(expenseApplication().container.expenseRepository)
        }
        initializer {
            SettingScreenViewModel(expenseApplication())
        }

//        // Initializer for ItemDetailsViewModel
//        initializer {
//            ItemDetailsViewModel(
//                this.createSavedStateHandle(),
//                inventoryApplication().container.itemsRepository
//            )
//        }

        // Initializer for HomeViewModel
        initializer {
            HomeScreenViewModel(expenseApplication().container.expenseRepository)
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [InventoryApplication].
 */
fun CreationExtras.expenseApplication(): ExpenseApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as ExpenseApplication)