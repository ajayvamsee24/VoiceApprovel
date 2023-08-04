package com.voiceapprovel.mobile.api.client

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.voiceapprovel.mobile.api.service.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Ajay Vamsee on 8/2/2023.
 * Time : 23:37
 */
private const val BASE_URL = "http://34.93.212.15:8501" // Replace with your actual base URL

object RetrofitClient {

    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}