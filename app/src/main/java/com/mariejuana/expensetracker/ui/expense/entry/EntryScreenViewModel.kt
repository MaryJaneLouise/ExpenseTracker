package com.mariejuana.expensetracker.ui.expense.entry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.text.NumberFormat
import java.util.Date
import com.mariejuana.expensetracker.data.expense.Expense
import com.mariejuana.expensetracker.data.expense.ExpenseRepository

class EntryScreenViewModel (private val expenseRepository: ExpenseRepository) : ViewModel() {
    var expenseUiState by mutableStateOf(ExpenseUiState())
        private set

    fun updateUiState(expenseDetails: ExpenseDetails) {
        expenseUiState =
            ExpenseUiState(
                expenseDetails = expenseDetails,
                isEntryValid = validateInput(expenseDetails)
            )
    }

    suspend fun saveExpense() {
        if (validateInput()) {
            expenseRepository.insertItem(expenseUiState.expenseDetails.toExpense())
        }
    }

    private fun validateInput(uiState: ExpenseDetails = expenseUiState.expenseDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() &&
            type.isNotBlank() &&
            amount.isNotBlank()
        }
    }
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