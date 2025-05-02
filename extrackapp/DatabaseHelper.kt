package com.example.extrackapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "ExpenseTracker.db"
        const val DATABASE_VERSION = 11// increment if changes are made in the database
        // Table names
        const val TABLE_USERS = "Users"
        const val TABLE_CATEGORIES = "Categories"
        const val TABLE_EXPENSES = "Expenses"
        const val TABLE_GOALS = "MonthlyGoals"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUserTable = """
            CREATE TABLE $TABLE_USERS (
                UserID INTEGER PRIMARY KEY AUTOINCREMENT,
                Username TEXT UNIQUE NOT NULL,
                Password TEXT NOT NULL
            );
        """.trimIndent()

        val createCategoriesTable = """
            CREATE TABLE $TABLE_CATEGORIES (
                CategoryID INTEGER PRIMARY KEY AUTOINCREMENT,
                UserID INTEGER,
                Name TEXT NOT NULL,
                FOREIGN KEY(UserID) REFERENCES $TABLE_USERS(UserID)
            );
        """.trimIndent()

        val createExpensesTable = """
            CREATE TABLE $TABLE_EXPENSES (
                ExpenseID INTEGER PRIMARY KEY AUTOINCREMENT,
                UserID INTEGER,
                CategoryID INTEGER,
                expenseName TEXT,
                Date TEXT NOT NULL,
                StartTime TEXT,
                EndTime TEXT,
                Description TEXT,
                PhotoPath TEXT,
                Amount REAL NOT NULL,
                FOREIGN KEY(UserID) REFERENCES $TABLE_USERS(UserID),
                FOREIGN KEY(CategoryID) REFERENCES $TABLE_CATEGORIES(CategoryID)
            );
        """.trimIndent()



        val createGoalsTable = """
            CREATE TABLE $TABLE_GOALS (
                GoalID INTEGER PRIMARY KEY AUTOINCREMENT,
                UserID INTEGER,
                Month TEXT NOT NULL,
                MinAmount REAL,
                MaxAmount REAL,
                FOREIGN KEY(UserID) REFERENCES $TABLE_USERS(UserID)
            );
        """.trimIndent()


        db.execSQL(createUserTable)
        db.execSQL(createCategoriesTable)
        db.execSQL(createExpensesTable)
        db.execSQL(createGoalsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GOALS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // ---------------- USERS ----------------

    fun registerUser(username: String, password: String): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put("Username", username)
        values.put("Password", password)
        val result = db.insert(TABLE_USERS, null, values)
        return result != -1L
    }

    fun loginUser(username: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USERS WHERE Username = ? AND Password = ?",
            arrayOf(username, password)
        )
        val isLoggedIn = cursor.count > 0
        cursor.close()
        return isLoggedIn
    }

    fun getUserId(username: String): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT UserID FROM $TABLE_USERS WHERE Username = ?",
            arrayOf(username)
        )
        val id = if (cursor.moveToFirst()) cursor.getInt(0) else -1
        cursor.close()
        return id
    }

    // ---------------- CATEGORIES ----------------

    fun insertCategory(userId: Int, name: String): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put("UserID", userId)
        values.put("Name", name)
        return db.insert(TABLE_CATEGORIES, null, values) != -1L
    }

    fun addCategory(userId: Int, categoryName: String): Boolean {
        val db = this.writableDatabase

        // Check for duplicate category
        val cursor = db.rawQuery(
            "SELECT * FROM categories WHERE userId = ? AND name = ?",
            arrayOf(userId.toString(), categoryName)
        )

        if (cursor.count > 0) {
            cursor.close()
            return false
        }

        val values = ContentValues().apply {
            put("userId", userId)
            put("name", categoryName)
        }

        val result = db.insert("categories", null, values)
        return result != -1L
    }


    fun getCategories(userId: Int): List<Category> {
        val db = readableDatabase
        val categories = mutableListOf<Category>()

        val cursor: Cursor = db.rawQuery(
            "SELECT CategoryID, Name FROM $TABLE_CATEGORIES WHERE UserID = ?",
            arrayOf(userId.toString())
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("CategoryID"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("Name"))
                categories.add(Category(id, name))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return categories
    }


    // ---------------- EXPENSES ----------------

    fun insertExpense(
        userId: Int,
        categoryId: Int,
        date: String,
        expenseName: String,
        startTime: String?,
        endTime: String?,
        description: String,
        photoPath: String?,
        amount: Double
    ): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put("UserID", userId)
        values.put("CategoryID", categoryId)
        values.put("expenseName", expenseName)
        values.put("Date", date)
        values.put("StartTime", startTime)
        values.put("EndTime", endTime)
        values.put("Description", description)
        values.put("PhotoPath", photoPath)
        values.put("Amount", amount)
        return db.insert(TABLE_EXPENSES, null, values) != -1L
    }

    fun getExpenses(userId: Int): List<Expense> {
        val expenses = mutableListOf<Expense>()
        val db = readableDatabase

        val query = """
        SELECT e.ExpenseID, e.expenseName, e.Amount, e.Date, e.StartTime, e.EndTime,
               e.Description, e.PhotoPath, c.Name AS CategoryName
        FROM Expenses e
        INNER JOIN Categories c ON e.CategoryID = c.CategoryID
        WHERE e.UserID = ?
        ORDER BY e.Date DESC
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("ExpenseID"))
                val expenseName = cursor.getString(cursor.getColumnIndexOrThrow("expenseName"))
                val amount = cursor.getDouble(cursor.getColumnIndexOrThrow("Amount"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("Date"))
                val startTime = cursor.getString(cursor.getColumnIndexOrThrow("StartTime")) ?: ""
                val endTime = cursor.getString(cursor.getColumnIndexOrThrow("EndTime")) ?: ""
                val description = cursor.getString(cursor.getColumnIndexOrThrow("Description")) ?: ""
                val photoPath = cursor.getString(cursor.getColumnIndexOrThrow("PhotoPath")) ?: ""
                val categoryName = cursor.getString(cursor.getColumnIndexOrThrow("CategoryName"))

                val expense = Expense(
                    id = id,
                    categoryName = categoryName,
                    expenseName = expenseName,
                    amount = amount,
                    date = date,
                    description = description,
                    startTime = startTime,
                    endTime = endTime,
                    photoPath = photoPath
                )

                expenses.add(expense)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return expenses
    }

    fun addExpense(
        userId: Int,
        categoryId: Int,
        expenseName: String,
        amount: Double,
        date: String,
        startTime: String? = null,
        endTime: String? = null,
        description: String? = null,
        photoPath: String? = null
    ): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("UserID", userId)
            put("CategoryID", categoryId)
            put("expenseName", expenseName)
            put("Amount", amount)
            put("Date", date)

            // Add optional fields only if they're not null or empty
            if (!startTime.isNullOrEmpty()) put("StartTime", startTime)
            if (!endTime.isNullOrEmpty()) put("EndTime", endTime)
            if (!description.isNullOrEmpty()) put("Description", description)
            if (!photoPath.isNullOrEmpty()) put("PhotoPath", photoPath)
        }

        val result = db.insert(TABLE_EXPENSES, null, values)
        db.close()
        return result != -1L // true if insert succeeded
    }


    fun getMonthlyReport(userId: Int): Map<String, Double> {
        val report = mutableMapOf<String, Double>()
        val db = readableDatabase

        val query = """
        SELECT strftime('%Y-%m', date) AS month, SUM(amount) as total 
        FROM expenses 
        WHERE userId = ? 
        GROUP BY month 
        ORDER BY month DESC
    """
        val cursor = db.rawQuery(query, arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val month = cursor.getString(cursor.getColumnIndexOrThrow("month"))
                val total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
                report[month] = total
            } while (cursor.moveToNext())
        }

        cursor.close()
        return report
    }


    // ---------------- GOALS ----------------

    fun setMonthlyGoal(userId: Int, month: String, min: Double, max: Double): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put("UserID", userId)
        values.put("Month", month)
        values.put("MinAmount", min)
        values.put("MaxAmount", max)
        return db.insert(TABLE_GOALS, null, values) != -1L
    }

    fun getMonthlyGoal(userId: Int, month: String): Cursor {
        val db = readableDatabase
        return db.rawQuery(
            "SELECT MinAmount, MaxAmount FROM $TABLE_GOALS WHERE UserID = ? AND Month = ?",
            arrayOf(userId.toString(), month)
        )
    }
    fun setGoal(userId: Int, month: String, minAmount: Double, maxAmount: Double) {
        val db = writableDatabase
        val query = """
        INSERT OR REPLACE INTO MonthlyGoals (UserID, Month, MinAmount, MaxAmount)
        VALUES (?, ?, ?, ?)
    """
        db.execSQL(query, arrayOf(userId, month, minAmount, maxAmount))
    }

    fun getGoal(userId: Int, month: String): Pair<Double, Double>? {
        val db = readableDatabase
        val query = """
        SELECT MinAmount, MaxAmount FROM MonthlyGoals 
        WHERE UserID = ? AND Month = ?
    """
        val cursor = db.rawQuery(query, arrayOf(userId.toString(), month))
        if (cursor.moveToFirst()) {
            val minAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("MinAmount"))
            val maxAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("MaxAmount"))
            cursor.close()
            return Pair(minAmount, maxAmount)
        }
        cursor.close()
        return null
    }


    // ---------------- REPORTING ----------------
    fun getTotalSpentPerCategory(userId: Int): Map<String, Double> {
        val db = readableDatabase
        val result = mutableMapOf<String, Double>()

        val query = """
        SELECT c.Name, SUM(e.Amount) AS total
        FROM $TABLE_EXPENSES e
        JOIN $TABLE_CATEGORIES c ON e.CategoryID = c.CategoryID
        WHERE e.UserID = ?
        GROUP BY c.Name
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val category = cursor.getString(cursor.getColumnIndexOrThrow("Name"))
                val total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
                result[category] = total
            } while (cursor.moveToNext())
        }

        cursor.close()
        return result
    }


    // Insert a filtered expense
    fun insertFilteredExpense(expense: FilterExpense) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("user_id", expense.userId)
            put("category_id", expense.categoryId)
            put("amount", expense.amount)
            put("date", expense.date)
            put("description", expense.description)
        }
        db.insert("FilteredExpenses", null, values)
        db.close()
    }

    // Get filtered expenses between two dates
    fun getFilteredExpenses(userId: Int, startDate: String, endDate: String): List<FilterExpense> {
        val expenses = mutableListOf<FilterExpense>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM FilteredExpenses WHERE user_id = ? AND date BETWEEN ? AND ?",
            arrayOf(userId.toString(), startDate, endDate)
        )

        if (cursor.moveToFirst()) {
            do {
                val expense = FilterExpense(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    userId = userId,
                    categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("category_id")),
                    amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
                    date = cursor.getString(cursor.getColumnIndexOrThrow("date")),
                    description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                )
                expenses.add(expense)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return expenses
    }


}