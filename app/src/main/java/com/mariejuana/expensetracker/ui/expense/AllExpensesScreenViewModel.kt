package com.mariejuana.expensetracker.ui.expense

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

data class AllExpensesScreenUiState(val expenseList: List<Expense> = listOf())

class AllExpensesScreenViewModel (expenseRepository: ExpenseRepository) : ViewModel() {

    val perMonthScreenUiState: StateFlow<AllExpensesScreenUiState> =
        expenseRepository.getAllExpensesStream().map { AllExpensesScreenUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = AllExpensesScreenUiState()
            )
}