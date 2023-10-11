package com.mariejuana.expensetracker.ui.budget

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariejuana.expensetracker.data.expense.Budget
import java.text.NumberFormat
import java.util.Date
import com.mariejuana.expensetracker.data.expense.ExpenseRepository
import com.mariejuana.expensetracker.data.expense.TransactionHistory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class BudgetUiState(val currentBudget: List<Budget> = listOf())
data class TransactionUiState(val transactionList: List<TransactionHistory> = listOf())
class BudgetScreenViewModel (expenseRepository: ExpenseRepository) : ViewModel() {
    var budgetScreenUiState: StateFlow<BudgetUiState> =
        expenseRepository.getAllBudgetStream().map { BudgetUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = BudgetUiState()
            )

    var transactionFragmentUiState: StateFlow<TransactionUiState> =
        expenseRepository.getAllTransactionStream().map { TransactionUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = TransactionUiState()
            )
    val currentBudget: StateFlow<Budget?> = expenseRepository.getCurrentBudgetStream()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}

