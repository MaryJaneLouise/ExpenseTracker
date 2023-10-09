package com.mariejuana.expensetracker.ui.expense.details.item

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariejuana.expensetracker.data.expense.Expense
import com.mariejuana.expensetracker.data.expense.ExpenseRepository
import com.mariejuana.expensetracker.ui.expense.entry.ExpenseDetails
import com.mariejuana.expensetracker.ui.expense.entry.toExpense
import com.mariejuana.expensetracker.ui.expense.entry.toExpenseDetails
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class ItemDetailsUiState(
    val expenseDetails: ExpenseDetails = ExpenseDetails()
)

class ItemDetailsScreenViewModel (savedStateHandle: SavedStateHandle, private val expenseRepository: ExpenseRepository) : ViewModel() {
    private val itemId: Int = checkNotNull(savedStateHandle[ItemDetailsDestination.itemIdArg])

    val itemDetailsUiState: StateFlow<ItemDetailsUiState> =
        expenseRepository.getExpenseStream(itemId)
            .filterNotNull()
            .map {
                ItemDetailsUiState(expenseDetails = it.toExpenseDetails())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = ItemDetailsUiState()
            )

    val recentExpense: StateFlow<Expense?> = expenseRepository.getRecentExpenseStream()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val totalPriceForCurrentMonth: StateFlow<Double?> = flow {
        val currentMonth = String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1)
        val currentYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
        emitAll(expenseRepository.getTotalAmountForMonth(currentMonth, currentYear))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val totalPriceForCurrentYear: StateFlow<Double?> = flow {
        val currentYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
        emitAll(expenseRepository.getTotalAmountForYear(currentYear))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    suspend fun deleteItem() {
        expenseRepository.deleteItem(itemDetailsUiState.value.expenseDetails.toExpense())
    }
}