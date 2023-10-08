package com.mariejuana.expensetracker.data.expense

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * from expense ORDER BY date_added ASC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * from expense WHERE id = :id")
    fun getExpense(id: Int): Flow<Expense>

    @Query("SELECT IFNULL(SUM(amount), 0) from expense WHERE strftime('%m', datetime(date_added / 1000, 'unixepoch')) = :month AND strftime('%Y', datetime(date_added / 1000, 'unixepoch')) = :year")
    fun getTotalAmountForMonth(month: String, year: String): Flow<Double>

    @Query("SELECT IFNULL(SUM(amount), 0) from expense WHERE strftime('%Y', datetime(date_added / 1000, 'unixepoch')) = :year")
    fun getTotalAmountForYear(year: String): Flow<Double>

    @Query("SELECT * FROM expense ORDER BY date_added DESC LIMIT 1")
    fun getRecentExpense(): Flow<Expense?>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(expense: Expense)

    @Update
    suspend fun update(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)
}