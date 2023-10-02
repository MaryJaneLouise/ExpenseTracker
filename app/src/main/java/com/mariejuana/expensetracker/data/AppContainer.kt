package com.mariejuana.expensetracker.data

import android.content.Context

interface AppContainer {
    val expenseRepository: ExpenseRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val expenseRepository: ExpenseRepository by lazy {
        OfflineExpenseRepository(ExpenseDatabase.getDatabase(context).expenseDao())
    }
}