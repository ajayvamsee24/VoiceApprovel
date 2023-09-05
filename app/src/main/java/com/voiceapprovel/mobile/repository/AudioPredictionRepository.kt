package com.voiceapprovel.mobile.repository

import android.util.Log
import com.voiceapprovel.mobile.api.exception.ApiException
import com.voiceapprovel.mobile.api.model.AudioPrediction
import com.voiceapprovel.mobile.api.model.UwbLiveDataModel
import com.voiceapprovel.mobile.api.model.UwbOldDataModel
import com.voiceapprovel.mobile.api.service.ApiService
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

    suspend fun getUwbVideoData(): UwbLiveDataModel {
        try {
            val response = apiService.getUWBVideoData()
            return response
        } catch (e: Exception) {
            Log.d(TAG, "getUwbVideoData: ${e.message}")
            throw ApiException(e.message)
        }
    }

    suspend fun postUwbData(id: String, inference: String, feedback: String): String? {
        Log.d(TAG, "PostUwbVideoData ID: $id INFERENCE: $inference FEEDBACK: $feedback")
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.postUwbVideoData(id, inference, feedback)
                Log.d(TAG, "postUwbData: ${response.code()}")
                return@withContext response.toString()
            } catch (e: Exception) {
                Log.d(TAG, "postUwbData: 11 ${e.message}")
                e.message

            }
        }
    }

    suspend fun getUwbOldVideoData(): List<UwbOldDataModel> {
        return  try {
                val response = apiService.getUwbOldData()
                return response
            } catch (e: Exception) {
                Log.d("TAG", "getUwbOldVideoData: ${e.message}")
                throw ApiException(e.message)
            }

    }


    companion object {
        const val TAG = "AudioPredictionRepository"
    }
}