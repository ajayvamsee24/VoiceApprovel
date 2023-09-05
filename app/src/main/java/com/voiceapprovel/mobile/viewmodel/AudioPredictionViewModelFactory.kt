package com.voiceapprovel.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.voiceapprovel.mobile.api.client.RetrofitClient
import com.voiceapprovel.mobile.repository.AudioPredictionRepository

/**
 * Created by Ajay Vamsee on 8/2/2023.
 * Time : 23:43
 */
class AudioPredictionViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UwbViewModel::class.java)) {
            val apiService = RetrofitClient.apiService
            val repository = AudioPredictionRepository(apiService)
            return UwbViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
