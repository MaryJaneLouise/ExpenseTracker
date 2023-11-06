package com.mariejuana.expensetracker.ui.budget.entry

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariejuana.expensetracker.data.expense.Budget
import java.text.NumberFormat
import java.util.Date
import com.mariejuana.expensetracker.data.expense.Expense
import com.mariejuana.expensetracker.data.expense.ExpenseRepository
import com.mariejuana.expensetracker.data.expense.TransactionHistory
import com.mariejuana.expensetracker.ui.expense.entry.toTransaction
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BudgetEntryScreenViewModel (private val expenseRepository: ExpenseRepository) : ViewModel() {
    var budgetUiState by mutableStateOf(BudgetUiState())
        private set

    private val budgetId: Int = 1

    fun updateUiState(budgetDetails: BudgetDetails, transactionDetails: TransactionDetails) {
        budgetUiState =
            BudgetUiState(
                budgetDetails = budgetDetails,
                transactionDetails = transactionDetails,
                isEntryValid = validateInput(budgetDetails)
            )
    }

    fun loadSettingsForceInsert(context: Context) : Boolean {
        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("forceDialogInsertOff", false)
    }

    suspend fun saveBudget() {
        if (validateInput()) {
            expenseRepository.insertBudget(budgetUiState.budgetDetails.toBudget())
            expenseRepository.insertTransaction(budgetUiState.transactionDetails.toTransaction())
        }
    }

    suspend fun addAndUpdateBudget(newAmount: Double) {
        val currentBudget = expenseRepository.getCurrentBudget(budgetId)
            .filterNotNull()
            .first()
            .toBudgetUiState()

        val currentAmount = currentBudget.budgetDetails.amount.toDouble()

        val updatedBudgetDetails = currentBudget.budgetDetails.copy(
            amount = (currentAmount + newAmount).toString()
        )

        val updatedBudgetUiState = currentBudget.copy(
            budgetDetails = updatedBudgetDetails
        )

        if (validateInput()) {
            expenseRepository.updateBudget(updatedBudgetUiState.budgetDetails.toBudget())
            expenseRepository.insertTransaction(budgetUiState.transactionDetails.toTransaction())
        }
    }


    private fun validateInput(uiState: BudgetDetails = budgetUiState.budgetDetails): Boolean {
        return with(uiState) {
            amount.isNotBlank() && (amount > 0.toString())
        }
    }

    val currentBudget: StateFlow<Budget?> = expenseRepository.getCurrentBudgetStream()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}

// For the Budget
data class BudgetUiState(
    val budgetDetails: BudgetDetails = BudgetDetails(),
    val transactionDetails: TransactionDetails = TransactionDetails(),
    val isEntryValid: Boolean = false
)

data class BudgetDetails(
    val id: Int = 1,
    val amount: String = "",
)

fun BudgetDetails.toBudget(): Budget = Budget(
    id = id,
    amount = amount.toDoubleOrNull() ?: 0.0,
)

fun Budget.toBudgetUiState(isEntryValid: Boolean = false): BudgetUiState = BudgetUiState(
    budgetDetails = this.toBudgetDetails(),
    isEntryValid = isEntryValid
)

fun Budget.toBudgetDetails(): BudgetDetails = BudgetDetails(
    id = id,
    amount = amount.toString(),
)

fun Budget.formattedAmount(): String {
    return NumberFormat.getCurrencyInstance().format(amount)
}


// For the transaction history
data class TransactionUiState(
    val transactionDetails: TransactionDetails = TransactionDetails(),
    val isEntryValid: Boolean = false
)

data class TransactionDetails(
    val id: Int = 0,
    val name: String = "",
    val type: String = "",
    val amount: String = "",
    val date: Date = Date(),
)

fun TransactionDetails.toTransaction(): TransactionHistory = TransactionHistory(
    id = id,
    name = "Added budget",
    type = "budget",
    amount = amount.toDoubleOrNull() ?: 0.0,
    date = date
)

fun TransactionDetails.toTransactionDetails(): TransactionDetails = TransactionDetails(
    id = id,
    name = name,
    type = type,
    amount = amount.toString(),
    date = date
)