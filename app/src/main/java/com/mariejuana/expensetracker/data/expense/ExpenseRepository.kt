package com.mariejuana.expensetracker.data.expense

import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getAllExpensesStream(): Flow<List<Expense>>

    fun getExpenseStream(id: Int): Flow<Expense?>

    fun getTotalAmountForMonth(month: String, year: String): Flow<Double?>

    fun getTotalAmountForYear(year: String): Flow<Double?>

    fun getRecentExpenseStream(): Flow<Expense?>

    /**
     * Insert item in the data source
     */
    suspend fun insertItem(expense: Expense)

    /**
     * Delete item from the data source
     */
    suspend fun deleteItem(expense: Expense)

    /**
     * Update item in the data source
     */
    suspend fun updateItem(expense: Expense)
}