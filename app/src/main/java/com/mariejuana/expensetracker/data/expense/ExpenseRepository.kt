package com.mariejuana.expensetracker.data.expense

import androidx.room.Update
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    // Getting the expenses
    fun getAllExpensesStream(): Flow<List<Expense>>

    fun getExpenseStream(id: Int): Flow<Expense?>

    fun getTotalAmountForMonth(month: String, year: String): Flow<Double?>

    fun getTotalAmountForYear(year: String): Flow<Double?>

    fun getRecentExpenseStream(): Flow<Expense?>

    suspend fun insertItem(expense: Expense)

    suspend fun deleteItem(expense: Expense)

    suspend fun deleteAllItem()

    suspend fun updateItem(expense: Expense)

    // Getting the budget
    fun getAllBudgetStream(): Flow<List<Budget>>

    fun getCurrentBudgetStream(): Flow<Budget?>

    fun getCurrentBudget(id: Int): Flow<Budget>

    suspend fun insertBudget(budget: Budget)

    suspend fun updateBudget(budget: Budget)
}