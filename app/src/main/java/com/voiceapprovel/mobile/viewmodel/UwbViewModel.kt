package com.voiceapprovel.mobile.viewmodel

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.voiceapprovel.mobile.api.client.RetrofitClient
import com.voiceapprovel.mobile.api.model.AudioPrediction
import com.voiceapprovel.mobile.api.model.UwbLiveDataModel
import com.voiceapprovel.mobile.api.model.UwbOldDataModel
import com.voiceapprovel.mobile.repository.AudioPredictionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Ajay Vamsee on 8/2/2023.
 * Time : 23:39
 */
class UwbViewModel(
    private val repository: AudioPredictionRepository = AudioPredictionRepository(
        RetrofitClient.apiService
    )
) : ViewModel() {

    private val _audioPredictions = MutableLiveData<List<AudioPrediction>>()
    val audioPredictions: LiveData<List<AudioPrediction>>
        get() = _audioPredictions

    // LiveData to hold the API response
    private val _apiResponseLiveData = MutableLiveData<String?>()
    val apiResponseLiveData: LiveData<String?>
        get() = _apiResponseLiveData

    // Live data to hold the UwbVideoData
    private val _uwbResponseLiveData = MutableLiveData<UwbLiveDataModel?>()
    val uwbResponseLiveData: LiveData<UwbLiveDataModel?>
        get() = _uwbResponseLiveData

    // Live data to hold the Api success data
    private val _apiSuccessLiveData = MutableLiveData<Boolean>()
    val apiSuccessLiveData: LiveData<Boolean>
        get() = _apiSuccessLiveData

    // Live data to hold the Timer data
    private val _timer = MutableLiveData<Int>()
    val timer: LiveData<Int> get() = _timer

    // Live data to hold the circular progresss data
    private val _progressPercentage = MutableLiveData<Int>()
    val progressPercentage: LiveData<Int> get() = _progressPercentage

    // Live data to hold the uwb old api
    private val _uwbOldVideoData = MutableLiveData<List<UwbOldDataModel>>()
    val uwbOldVideoData: LiveData<List<UwbOldDataModel>>
        get() = _uwbOldVideoData


    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error


    private lateinit var countDownTimer: CountDownTimer

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

    fun fetchUwVideo() {
        viewModelScope.launch(Dispatchers.IO){
            val uwbVideoResponse = repository.getUwbVideoData()
            _uwbResponseLiveData.postValue(uwbVideoResponse)
        }


    }

    fun postUwbVideoData(id: String, inference: String, feedback: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = repository.postUwbData(id, inference, feedback)
                _apiSuccessLiveData.postValue(true)
                Log.d("AudioPredictionViewModel", "postUwbVideoData: $response")
            } catch (e: Exception) {
                _apiSuccessLiveData.postValue(false)
                Log.d("AudioPredictionViewModel", "postUwbVideoData: ${e.message}")
            }
        }
    }

    fun startTimer() {
        countDownTimer = object : CountDownTimer(20000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val time = millisUntilFinished / 1000
                _timer.value = time.toInt()
                _progressPercentage.value = millisUntilFinished.toInt()
            }

            override fun onFinish() {
                _timer.value = 0
                _progressPercentage.value = 0
            }

        }.start()
    }

    fun stopTimer() {
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
    }

    fun fetchOldUwbVideos() {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val uwbOldVideoResponse = repository.getUwbOldVideoData()
                _uwbOldVideoData.postValue(uwbOldVideoResponse)
            }
        } catch (e: Exception) {
            Log.d("TAG", "fetchOldUwbVideos: ${e.message} ")
        }
    }
}