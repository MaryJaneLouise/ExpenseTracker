package com.mariejuana.expensetracker.application

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mariejuana.expensetracker.ui.expense.entry.EntryScreenViewModel
import com.mariejuana.expensetracker.ui.expense.general_details.GeneralDetailsScreenViewModel
import com.mariejuana.expensetracker.ui.expense.yearly.YearlyScreenViewModel
import com.mariejuana.expensetracker.ui.home.HomeScreenViewModel

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
            GeneralDetailsScreenViewModel(expenseApplication().container.expenseRepository)
        }

        initializer {
            YearlyScreenViewModel(expenseApplication().container.expenseRepository)
        }

        initializer {
            EntryScreenViewModel(expenseApplication().container.expenseRepository)
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