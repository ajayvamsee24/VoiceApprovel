package com.voiceapprovel.mobile.api.service

import com.voiceapprovel.mobile.api.model.AudioPrediction
import com.voiceapprovel.mobile.api.model.UwbLiveDataModel
import com.voiceapprovel.mobile.api.model.UwbOldDataModel
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


    @GET("fetch_record")
    suspend fun getUWBVideoData(): UwbLiveDataModel

    @FormUrlEncoded
    @POST("/get_response/")
    suspend fun postUwbVideoData(
        @Field("Id") id: String,
        @Field("Inference") inference: String,
        @Field("Feedback") feedback: String
    ): Response<UwbLiveDataModel>

    @GET("/screen_2_data")
    suspend fun getUwbOldData(): List<UwbOldDataModel>
}