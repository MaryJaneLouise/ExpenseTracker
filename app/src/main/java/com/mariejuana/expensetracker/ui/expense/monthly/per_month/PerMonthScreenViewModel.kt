package com.mariejuana.expensetracker.ui.expense.monthly.per_month

import android.icu.text.SimpleDateFormat
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
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
import java.util.Date
import java.util.Locale

data class PerMonthScreenUiState(val expenseList: List<Expense> = listOf())

class PerMonthScreenViewModel (savedStateHandle: SavedStateHandle, expenseRepository: ExpenseRepository) : ViewModel() {
    private val selectedMonth: Int = checkNotNull(savedStateHandle[PerMonthScreenDestination.itemIdArg])

    val perMonthScreenUiState: StateFlow<PerMonthScreenUiState> =
        expenseRepository.getAllExpensesStream().map { PerMonthScreenUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = PerMonthScreenUiState()
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

    val totalPriceForSelectedMonth: StateFlow<Double?> = flow {
        val currentMonth = String.format("%02d", selectedMonth)
        val currentYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
        emitAll(expenseRepository.getTotalAmountForMonth(currentMonth, currentYear))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)
}