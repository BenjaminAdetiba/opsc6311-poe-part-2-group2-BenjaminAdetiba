package com.example.extrackapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.extrackapp.DatabaseHelper.Companion.TABLE_CATEGORIES
import com.example.extrackapp.DatabaseHelper.Companion.TABLE_EXPENSES

class FilterExpenseDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ExpenseTracker.db"
        private const val DATABASE_VERSION = 11
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // No table creation needed
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // No upgrade logic needed
    }

    fun getExpensesOnDate(userId: Int, selectedDate: String): List<FilterExpense> {
        val expenses = mutableListOf<FilterExpense>()
        val db = readableDatabase

        val query = """
            SELECT e.ExpenseID, e.UserID, e.CategoryID, e.Amount, e.Date, e.Description, c.Name AS name
            FROM Expenses e
            INNER JOIN Categories c ON e.CategoryID = c.CategoryID
            WHERE e.UserID = ? AND e.Date = ?
        """.trimIndent()



//        fun getExpenses(userId: Int): List<Expense> {
//            val expenses = mutableListOf<Expense>()
//            val db = readableDatabase
//
//            val query = """
//    SELECT e.ExpenseID, e.expenseName, e.Amount, e.Date, e.StartTime, e.EndTime,
//           e.Description, e.PhotoPath, c.Name AS CategoryName
//    FROM Expenses e
//    INNER JOIN $TABLE_CATEGORIES c ON e.CategoryID = c.CategoryID
//    WHERE e.UserID = ?
//    ORDER BY e.Date DESC
//""".trimIndent()

        val cursor = db.rawQuery(query, arrayOf(userId.toString(), selectedDate))

        if (cursor.moveToFirst()) {
            do {
                val expense = FilterExpense(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("ExpenseID")),
                    userId = cursor.getInt(cursor.getColumnIndexOrThrow("UserID")),
                    categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("CategoryID")),
                    amount = cursor.getDouble(cursor.getColumnIndexOrThrow("Amount")),
                    date = cursor.getString(cursor.getColumnIndexOrThrow("Date")),
                    description = cursor.getString(cursor.getColumnIndexOrThrow("Description")),
                    name = cursor.getString(cursor.getColumnIndexOrThrow("name")) // NEW
                )
                expenses.add(expense)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return expenses
    }
}
