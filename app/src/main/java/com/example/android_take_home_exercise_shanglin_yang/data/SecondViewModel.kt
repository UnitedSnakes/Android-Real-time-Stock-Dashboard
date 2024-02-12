package com.example.android_take_home_exercise_shanglin_yang.data

import android.database.sqlite.SQLiteDatabase
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SecondViewModel(private val repository: Repository) : ViewModel() {

    private val _data = MutableLiveData<List<Item>>()
    val data: LiveData<List<Item>> get() = _data

    private var currentPage: Int = 0
    private var totalPages: Int = 0
    private var itemsPerPage: Int = 35

    private val db = repository.getDatabase()

    init {
        initialize()
    }

    private fun initialize() {
        val (_, calculatedTotalPages) = calculateTotalRowsAndPages()
        totalPages = calculatedTotalPages
        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                // Wait for the data to be ready
                repository.dataReady.observeForever { dataReady ->
                    if (dataReady) {
                        // Get the database from the repository
                        val startIndex = currentPage * itemsPerPage
                        // Fetch data from the database and update the LiveData
                        val newData = fetchDataHelper(db!!, startIndex, itemsPerPage)
                        _data.value = newData
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle exception, show error message, etc.
            }
        }
    }

    private fun fetchDataHelper(db: SQLiteDatabase, startIndex: Int, itemsPerPage: Int): List<Item> {
        val query = "SELECT * FROM items WHERE name IS NOT NULL AND name != '' ORDER BY listId ASC, CAST(SUBSTR(name, 5) AS INTEGER) ASC LIMIT $itemsPerPage OFFSET $startIndex"

        val itemsList = mutableListOf<Item>()

        db.rawQuery(query, null).use { cursor ->
            val listIdIndex = cursor.getColumnIndex("listId")
            val nameIndex = cursor.getColumnIndex("name")

            while (cursor.moveToNext()) {
                val listId = cursor.getInt(listIdIndex)
                val name = cursor.getString(nameIndex)

                // check if listId and name are valid
                if (listId != -1 && name != null && name != "") {
                    itemsList.add(Item(listId, name))
                }
            }
        }

        return itemsList
    }

    // Calculate the total number of rows in the database
    private fun calculateTotalRowsAndPages(): Pair<Int, Int> {
        val query = "SELECT COUNT(*) FROM items"
        val cursor = db?.rawQuery(query, null)

        var totalRows = 0

        if (cursor != null && cursor.moveToFirst()) {
            totalRows = cursor.getInt(0)
            cursor.close()
        }

        var totalPages = totalRows / itemsPerPage
        val remainingRows = totalRows % itemsPerPage
        if (remainingRows > 0) {
            totalPages += 1
        }
        return Pair(totalRows, totalPages)
    }

    fun setCurrentPage(page: Int) {
        currentPage = page
    }

    fun getCurrentPage(): Int {
        return currentPage
    }

    fun getTotalPages(): Int {
        return totalPages
    }
}
