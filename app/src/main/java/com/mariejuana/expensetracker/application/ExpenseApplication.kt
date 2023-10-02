package com.mariejuana.expensetracker.application

import android.app.Application
import com.mariejuana.expensetracker.data.AppContainer
import com.mariejuana.expensetracker.data.AppDataContainer

class ExpenseApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}