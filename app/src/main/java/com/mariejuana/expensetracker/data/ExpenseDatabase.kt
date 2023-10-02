package com.mariejuana.expensetracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Database(entities = [Expense::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class ExpenseDatabase :  RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var Instance : ExpenseDatabase? = null

        fun getDatabase(context: Context): ExpenseDatabase {
            return Instance ?: synchronized(Unit) {
                Room.databaseBuilder(context, ExpenseDatabase::class.java, "expense_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}