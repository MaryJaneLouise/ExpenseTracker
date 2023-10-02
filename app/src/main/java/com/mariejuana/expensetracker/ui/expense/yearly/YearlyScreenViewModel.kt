package com.mariejuana.expensetracker.ui.expense.yearly

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
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import java.util.Calendar
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

    val startYear = 2000
    val endYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date()).toInt()

    val totalPriceForYears: StateFlow<List<Pair<String, Double?>>> = flow {
        val yearlyTotals = mutableListOf<Pair<String, Double?>>()
        (startYear..endYear).asFlow()
            .flatMapMerge { year ->
                expenseRepository.getTotalAmountForYear(year.toString())
                    .map { totalAmount -> Pair(year.toString(), totalAmount) }
            }
            .collect { yearlyTotal ->
                yearlyTotals.add(yearlyTotal)
            }
        emit(yearlyTotals)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, listOf())


    val totalPriceForCurrentYear: StateFlow<Double?> = flow {
        val currentYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
        emitAll(expenseRepository.getTotalAmountForYear(currentYear))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

}
