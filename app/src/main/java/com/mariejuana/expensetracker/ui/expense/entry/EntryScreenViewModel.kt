package com.mariejuana.expensetracker.ui.expense.entry

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariejuana.expensetracker.data.expense.Budget
import java.text.NumberFormat
import java.util.Date
import com.mariejuana.expensetracker.data.expense.Expense
import com.mariejuana.expensetracker.data.expense.ExpenseRepository
import com.mariejuana.expensetracker.ui.budget.entry.BudgetDetails
import com.mariejuana.expensetracker.ui.budget.entry.BudgetUiState
import com.mariejuana.expensetracker.ui.budget.entry.toBudget
import com.mariejuana.expensetracker.ui.budget.entry.toBudgetUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn

class EntryScreenViewModel (private val expenseRepository: ExpenseRepository) : ViewModel() {
    var expenseUiState by mutableStateOf(ExpenseUiState())
        private set

    var budgetUiState by mutableStateOf(BudgetUiState())
        private set

    private val budgetId: Int = 1

    fun updateUiState(expenseDetails: ExpenseDetails) {
        expenseUiState =
            ExpenseUiState(
                expenseDetails = expenseDetails,
                isEntryValid = validateInput(expenseDetails)
            )
    }

    suspend fun saveExpense(newAmount: Double) {
        val currentBudget = expenseRepository.getCurrentBudget(budgetId)
            .filterNotNull()
            .first()
            .toBudgetUiState()

        val currentAmount = currentBudget.budgetDetails.amount.toDouble()

        val updatedBudgetDetails = currentBudget.budgetDetails.copy(
            amount = (currentAmount - newAmount).toString()
        )

        val updatedBudgetUiState = currentBudget.copy(
            budgetDetails = updatedBudgetDetails
        )

        if (validateInput()) {
            expenseRepository.insertItem(expenseUiState.expenseDetails.toExpense())
            expenseRepository.updateBudget(updatedBudgetUiState.budgetDetails.toBudget())
        }
    }

    suspend fun showError() {

    }

    private fun validateInput(uiState: ExpenseDetails = expenseUiState.expenseDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() &&
            type.isNotBlank() &&
            amount.isNotBlank()
        }
    }

    val currentBudget: StateFlow<Budget?> = expenseRepository.getCurrentBudgetStream()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}

data class ExpenseUiState(
    val expenseDetails: ExpenseDetails = ExpenseDetails(),
    val isEntryValid: Boolean = false
)

data class ExpenseDetails(
    val id: Int = 0,
    val name: String = "",
    val type: String = "",
    val amount: String = "",
    val date_added: Date = Date(),
)

fun ExpenseDetails.toExpense(): Expense = Expense(
    id = id,
    name = name,
    type = type,
    amount = amount.toDoubleOrNull() ?: 0.0,
    date_added = date_added
)

fun Expense.toExpenseUiState(isEntryValid: Boolean = false): ExpenseUiState = ExpenseUiState(
    expenseDetails = this.toExpenseDetails(),
    isEntryValid = isEntryValid
)

fun Expense.toExpenseDetails(): ExpenseDetails = ExpenseDetails(
    id = id,
    name = name,
    type = type,
    amount = amount.toString(),
    date_added = date_added
)

fun Expense.formattedAmount(): String {
    return NumberFormat.getCurrencyInstance().format(amount)
}