package com.voiceapprovel.mobile.api.client

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Created by Ajay Vamsee on 8/3/2023.
 * Time : 13:11
 */
fun createOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .followRedirects(true)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
}