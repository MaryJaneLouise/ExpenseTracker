package com.mariejuana.expensetracker.data.expense

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "expense")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val type: String,
    val amount: Double,
    val date_added: Date,
)

@Entity(tableName = "budget")
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
)

@Entity(tableName = "transaction_history")
data class TransactionHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val type: String,
    val amount: Double,
    val date: Date,
)