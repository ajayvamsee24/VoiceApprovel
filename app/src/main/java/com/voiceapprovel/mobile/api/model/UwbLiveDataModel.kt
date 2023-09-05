package com.voiceapprovel.mobile.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Ajay Vamsee on 8/10/2023.
 * Time : 16:26
 */
data class UwbLiveDataModel(
    @SerializedName("Door")
    val doorName: String = "",
    @SerializedName("Id")
    val id: String = "",
    @SerializedName("Inference")
    val inference: String = ""
)