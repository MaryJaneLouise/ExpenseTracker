package com.mariejuana.expensetracker.data.expense

import kotlinx.coroutines.flow.Flow

class OfflineExpenseRepository(private val expenseDao: ExpenseDao) : ExpenseRepository {
    override fun getAllExpensesStream(): Flow<List<Expense>> = expenseDao.getAllExpenses()

    override fun getExpenseStream(id: Int): Flow<Expense?> = expenseDao.getExpense(id)

    override fun getTotalAmountForMonth(month: String, year: String): Flow<Double?> = expenseDao.getTotalAmountForMonth(month, year)

    override fun getTotalAmountForYear(year: String): Flow<Double?> = expenseDao.getTotalAmountForYear(year)

    override fun getRecentExpenseStream(): Flow<Expense?> = expenseDao.getRecentExpense()

    override suspend fun insertItem(expense: Expense) = expenseDao.insert(expense)

    override suspend fun deleteItem(expense: Expense) = expenseDao.delete(expense)

    override suspend fun updateItem(expense: Expense) = expenseDao.update(expense)

}