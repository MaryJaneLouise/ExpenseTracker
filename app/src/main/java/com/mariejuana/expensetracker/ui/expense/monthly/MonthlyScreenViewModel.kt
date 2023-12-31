package com.mariejuana.expensetracker.ui.expense.monthly

import android.icu.text.SimpleDateFormat
import androidx.compose.runtime.mutableStateListOf
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

data class MonthlyScreenUiState(val expenseList: List<Expense> = listOf())

class MonthlyScreenViewModel (expenseRepository: ExpenseRepository) : ViewModel() {
    val monthlyScreenUiState: StateFlow<MonthlyScreenUiState> =
        expenseRepository.getAllExpensesStream().map { MonthlyScreenUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = MonthlyScreenUiState()
            )

    val currentYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
    val monthlyExpenses = mutableStateListOf<StateFlow<Double?>>()
    init {
        for (month in 1..12) {
            val perMonth = String.format("%02d", month)
            val totalPricePerMonth: StateFlow<Double?> = flow {
                emitAll(expenseRepository.getTotalAmountForMonth(perMonth, currentYear))
            }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)
            monthlyExpenses.add(totalPricePerMonth)
        }
    }

    val totalPriceForCurrentMonth: StateFlow<Double?> = flow {
        val currentMonth = String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1)
        val currentYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
        emitAll(expenseRepository.getTotalAmountForMonth(currentMonth, currentYear))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)
}