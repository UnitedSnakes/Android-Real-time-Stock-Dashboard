package com.example.android_take_home_exercise_shanglin_yang.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.json.JSONArray

class DatabaseManager(context: Context) {
    private val db: SQLiteDatabase? = SQLiteHelper(context).writableDatabase

    // Modify insertDataToDatabase to return a List<Item> after insertion if needed
    fun insertDataToDatabase(jsonString: String?): List<Item> {
        val itemsList = mutableListOf<Item>()

        jsonString?.let {
            db?.beginTransaction()
            try {
                // Initialize the database
                db?.delete("items", null, null)

                val jsonArray = JSONArray(jsonString)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val listId = jsonObject.optInt("listId")
                    val name = jsonObject.optString("name")

                    if (!name.isNullOrBlank() && name != "null") {
                        val contentValues = ContentValues()
                        contentValues.put("listId", listId)
                        contentValues.put("name", name)
                        db?.insertOrThrow("items", null, contentValues)

                        // Add the inserted item to the list
                        itemsList.add(Item(listId, name))
                    }
                }

                db?.setTransactionSuccessful()
            } finally {
                db?.endTransaction()
            }
        }

        return itemsList
    }

    fun getDatabase(): SQLiteDatabase? {
        return db
    }
}
