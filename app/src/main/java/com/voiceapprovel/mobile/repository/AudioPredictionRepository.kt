package com.voiceapprovel.mobile.repository

import android.util.Log
import com.voiceapprovel.mobile.api.service.ApiService
import com.voiceapprovel.mobile.model.AudioPrediction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by Ajay Vamsee on 8/2/2023.
 * Time : 23:54
 */
class AudioPredictionRepository(private val apiService: ApiService) {
    suspend fun getAudioPredictions(): List<AudioPrediction> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAudioPredictions()
                Log.d("TAG", "getAudioPredictions: $response")
                return@withContext response ?: emptyList()
            } catch (e: Exception) {
                Log.d("TAG", "getAudioPredictions: ${e.message} ")
                emptyList()
            }
        }
    }

    suspend fun postAudioApproval(id: String, label: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.postAudioApproval(id = id, changedLabel = label)
                Log.d("TAG1", "postAudioApproval: 1 ${response.body()}")
                return@withContext response.body()
            } catch (e: Exception) {
                Log.e("TAG1", "postAudioApproval: 111${e.message}")
                e.message
            }
        }
    }
}