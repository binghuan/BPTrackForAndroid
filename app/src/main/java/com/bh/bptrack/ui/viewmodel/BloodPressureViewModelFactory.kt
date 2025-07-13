package com.bh.bptrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bh.bptrack.data.repository.BloodPressureRepository

class BloodPressureViewModelFactory(
    private val repository: BloodPressureRepository
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BloodPressureViewModel::class.java)) {
            return BloodPressureViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 