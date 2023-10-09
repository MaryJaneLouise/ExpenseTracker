package com.mariejuana.expensetracker.ui.budget.entry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mariejuana.expensetracker.data.expense.Budget
import java.text.NumberFormat
import java.util.Date
import com.mariejuana.expensetracker.data.expense.Expense
import com.mariejuana.expensetracker.data.expense.ExpenseRepository

class BudgetScreenViewModel (private val expenseRepository: ExpenseRepository) : ViewModel() {
    var budgetUiState by mutableStateOf(BudgetUiState())
        private set

    fun updateUiState(budgetDetails: BudgetDetails) {
        budgetUiState =
            BudgetUiState(
                budgetDetails = budgetDetails,
                isEntryValid = validateInput(budgetDetails)
            )
    }

    suspend fun saveBudget() {
        if (validateInput()) {
            expenseRepository.insertBudget(budgetUiState.budgetDetails.toBudget())
        }
    }

    private fun validateInput(uiState: BudgetDetails = budgetUiState.budgetDetails): Boolean {
        return with(uiState) {
            amount.isNotBlank()
        }
    }
}

data class BudgetUiState(
    val budgetDetails: BudgetDetails = BudgetDetails(),
    val isEntryValid: Boolean = false
)

data class BudgetDetails(
    val id: Int = 0,
    val amount: String = "",
    val date_added: Date = Date(),
    val date_updated: Date = Date()
)

fun BudgetDetails.toBudget(): Budget = Budget(
    id = id,
    amount = amount.toDoubleOrNull() ?: 0.0,
    date_added = date_added,
    date_updated = date_updated
)

fun Budget.toBudgetUiState(isEntryValid: Boolean = false): BudgetUiState = BudgetUiState(
    budgetDetails = this.toBudgetDetails(),
    isEntryValid = isEntryValid
)

fun Budget.toBudgetDetails(): BudgetDetails = BudgetDetails(
    id = id,
    amount = amount.toString(),
    date_added = date_added,
    date_updated = date_updated
)

fun Budget.formattedAmount(): String {
    return NumberFormat.getCurrencyInstance().format(amount)
}