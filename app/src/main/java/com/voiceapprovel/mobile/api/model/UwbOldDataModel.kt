package com.voiceapprovel.mobile.api.model

import com.google.gson.annotations.SerializedName


/**
 * Created by Ajay Vamsee on 8/16/2023.
 * Time : 19:25
 */
data class UwbOldDataModel(
    @SerializedName("_id")
    val id: Id,
    @SerializedName("changesMade")
    val changesMade: List<Any>,
    @SerializedName("confidence_score")
    val confidenceScore: Int,
    @SerializedName("created_at")
    val createdAt: CreatedAt,
    @SerializedName("csv")
    val csv: String,
    @SerializedName("dataBoost")
    val dataBoost: Int,
    @SerializedName("dataSetCollections")
    val dataSetCollections: List<Any>,
    @SerializedName("gcspath")
    val gcspath: String,
    @SerializedName("label")
    val label: String,
    @SerializedName("model")
    val model: Model,
    @SerializedName("prediction")
    val prediction: String,
    @SerializedName("publicurl")
    val publicUrl: String,
    @SerializedName("resource")
    val resource: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("statusLastModifiedAt")
    val statusLastModifiedAt: StatusLastModifiedAt,
    @SerializedName("tag")
    val tag: String,
    @SerializedName("updated_at")
    val updatedAt: UpdatedAt
) {
    data class Id(
        @SerializedName("\$oid")
        val oid: String
    )

    data class CreatedAt(
        @SerializedName("\$date")
        val date: String
    )

    data class Model(
        @SerializedName("\$oid")
        val oid: String
    )

    data class StatusLastModifiedAt(
        @SerializedName("\$date")
        val date: String
    )

    data class UpdatedAt(
        @SerializedName("\$date")
        val date: String
    )
}
