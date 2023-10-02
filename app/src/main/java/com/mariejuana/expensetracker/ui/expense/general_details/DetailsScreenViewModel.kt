package com.mariejuana.expensetracker.ui.expense.general_details

import android.icu.text.SimpleDateFormat
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mariejuana.expensetracker.data.Expense
import com.mariejuana.expensetracker.data.ExpenseDao
import com.mariejuana.expensetracker.data.ExpenseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class GeneralDetailsUiState(val expenseList: List<Expense> = listOf())

class GeneralDetailsScreenViewModel (expenseRepository: ExpenseRepository) : ViewModel() {
    val generalDetailsUiState: StateFlow<GeneralDetailsUiState> =
        expenseRepository.getAllExpensesStream().map { GeneralDetailsUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = GeneralDetailsUiState()
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
}