package com.voiceapprovel.mobile.api.service

import com.voiceapprovel.mobile.model.AudioPrediction
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Created by Ajay Vamsee on 8/2/2023.
 * Time : 23:33
 */
interface ApiService {
    @GET("/")
    suspend fun getAudioPredictions(): List<AudioPrediction>

    @FormUrlEncoded
    @POST("/")
    suspend fun postAudioApproval(
        @Field("_id") id: String,

        @Field("changedLabel") changedLabel: String
    ): Response<String>
}