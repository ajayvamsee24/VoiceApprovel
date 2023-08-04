package com.voiceapprovel.mobile.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voiceapprovel.mobile.api.client.RetrofitClient
import com.voiceapprovel.mobile.model.AudioPrediction
import com.voiceapprovel.mobile.repository.AudioPredictionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Ajay Vamsee on 8/2/2023.
 * Time : 23:39
 */
class AudioPredictionViewModel(
    private val repository: AudioPredictionRepository = AudioPredictionRepository(
        RetrofitClient.apiService
    )
) : ViewModel() {

    private val _audioPredictions = MutableLiveData<List<AudioPrediction>>()
    val audioPredictions: LiveData<List<AudioPrediction>> get() = _audioPredictions

    // LiveData to hold the API response
    private val _apiResponseLiveData = MutableLiveData<String?>()
    val apiResponseLiveData: LiveData<String?> get() = _apiResponseLiveData


    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchAudioPredictions() {
        Log.d("TAG", "fetchAudioPredictions: ")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val predictions = repository.getAudioPredictions()

                _audioPredictions.postValue(predictions)

            } catch (e: Exception) {

                _error.postValue("Failed to fetch audio predictions: ${e.message}")
                Log.d("TAG", "Failed to fetch audio predictions: ${e.message}")

            }
        }
    }

    fun postAudioApproval(id: String, newLabel: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = repository.postAudioApproval(id, newLabel)
                _apiResponseLiveData.postValue(response)


            } catch (e: Exception) {
                Log.d("TAG1", "postAudioApproval: ${e.message}")
            }
        }
    }
}