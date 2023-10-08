package com.mariejuana.expensetracker.ui.expense.yearly

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
import java.util.Date
import java.util.Locale

data class YearlyScreenUiState(val expenseList: List<Expense> = listOf())

class YearlyScreenViewModel(expenseRepository: ExpenseRepository) : ViewModel() {
    val yearlyScreenUiState: StateFlow<YearlyScreenUiState> =
        expenseRepository.getAllExpensesStream().map { YearlyScreenUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = YearlyScreenUiState()
            )

    val startYear = 2015
    val endYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date()).toInt()
    val yearlyExpenses = mutableStateListOf<StateFlow<Double?>>()

    init {
        for (year in startYear .. endYear) {
            val totalPricePerYear: StateFlow<Double?> = flow {
                emitAll(expenseRepository.getTotalAmountForYear(year.toString()))
            }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)
            yearlyExpenses.add(totalPricePerYear)
        }

    }

    val totalPriceForCurrentYear: StateFlow<Double?> = flow {
        val currentYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
        emitAll(expenseRepository.getTotalAmountForYear(currentYear))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

}
