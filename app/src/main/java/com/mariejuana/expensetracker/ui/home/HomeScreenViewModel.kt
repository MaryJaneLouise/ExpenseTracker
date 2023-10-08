package com.mariejuana.expensetracker.ui.home

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariejuana.expensetracker.data.expense.Expense
import com.mariejuana.expensetracker.data.expense.ExpenseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class HomeUiState(val expenseList: List<Expense> = listOf())

class HomeScreenViewModel(expenseRepository: ExpenseRepository) : ViewModel() {
    val homeUiState: StateFlow<HomeUiState> =
        expenseRepository.getAllExpensesStream().map { HomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = HomeUiState()
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