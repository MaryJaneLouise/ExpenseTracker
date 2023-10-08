package com.mariejuana.expensetracker.data

import android.content.Context
import com.mariejuana.expensetracker.data.expense.ExpenseDatabase
import com.mariejuana.expensetracker.data.expense.ExpenseRepository
import com.mariejuana.expensetracker.data.expense.OfflineExpenseRepository

interface AppContainer {
    val expenseRepository: ExpenseRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val expenseRepository: ExpenseRepository by lazy {
        OfflineExpenseRepository(ExpenseDatabase.getDatabase(context).expenseDao())
    }
}