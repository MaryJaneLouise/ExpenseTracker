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
    // Getting all of the expenses
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

    @Query("DELETE FROM expense")
    suspend fun deleteAll()


    // Getting the current budget
    @Query("SELECT * from budget")
    fun getAllBudget(): Flow<List<Budget>>

    @Query("SELECT * from budget WHERE id = :id")
    fun getCurrentBudget(id: Int): Flow<Budget>

    @Query("SELECT * from budget")
    fun getBudget(): Flow<Budget?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBudget(budget: Budget)

    @Update
    suspend fun updateBudget(budget: Budget)

    @Query("DELETE FROM budget")
    suspend fun deleteBudget()


    // Getting the transaction history
    @Query("SELECT * FROM transaction_history")
    fun getAllTransaction(): Flow<List<TransactionHistory>>

    @Query("SELECT * FROM transaction_history WHERE id = :id")
    fun getCurrentTransaction(id: Int): Flow<TransactionHistory>

    @Query("SELECT * FROM transaction_history")
    fun getTransaction(): Flow<TransactionHistory?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTransaction(transactionHistory: TransactionHistory)

    @Query("DELETE FROM transaction_history")
    suspend fun deleteAllTransaction()
}