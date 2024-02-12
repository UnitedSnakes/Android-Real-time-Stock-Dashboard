package com.example.android_take_home_exercise_shanglin_yang.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Repository (context: Context) {
    private val _dataReady = MutableLiveData<Boolean>()
    val dataReady: LiveData<Boolean> get() = _dataReady

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val networkClient: NetworkClient = NetworkClient()
    private val databaseManager: DatabaseManager = DatabaseManager(context)

    init {
        applicationScope.launch {
            // credits to Fetch Rewards for the JSON dataset
            // val jsonString = networkClient.performRequest("https://fetch-hiring.s3.amazonaws.com/hiring.json")
            val jsonString = networkClient.performRequest("https://github.com/UnitedSnakes/JSONFetcherDemo/blob/main/app/src/main/AndroidManifest.xml")

            // Asynchronously call insertData
            withContext(Dispatchers.IO) {
                databaseManager.insertDataToDatabase(jsonString)
            }

            // Data is ready, notify observers
            _dataReady.postValue(true)
        }
    }

    // Expose a method to get the database from DatabaseManager
    fun getDatabase(): SQLiteDatabase? {
        return databaseManager.getDatabase()
    }
}
