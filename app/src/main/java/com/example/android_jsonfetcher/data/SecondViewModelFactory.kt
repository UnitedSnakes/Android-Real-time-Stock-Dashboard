//SecondViewModelFactory.kt


package com.example.android_jsonfetcher.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SecondViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SecondViewModel::class.java)) {
            return SecondViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

