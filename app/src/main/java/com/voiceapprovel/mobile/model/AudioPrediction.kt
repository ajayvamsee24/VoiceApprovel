package com.voiceapprovel.mobile.model

/**
 * Created by Ajay Vamsee on 8/2/2023.
 * Time : 23:21
 */
import com.google.gson.annotations.SerializedName

data class AudioPrediction(
    @SerializedName("_id")
    val id: Id,
    val changesMade: List<Any>,
    @SerializedName("confidence_score")
    val confidenceScore: Double,
    @SerializedName("created_at")
    val createdAt: CreatedAt,
    val csv: String,
    @SerializedName("dataBoost")
    val dataBoost: Int,
    val dataSetCollections: List<Any>,
    val gcspath: String,
    val label: String,
    val model: Model,
    val prediction: String,
    val publicurl: String,
    val resource: String,
    val status: String,
    @SerializedName("statusLastModifiedAt")
    val statusLastModifiedAt: StatusLastModifiedAt,
    val tag: String,
    val trimmedAudios: List<Any>,
    @SerializedName("updated_at")
    val updatedAt: UpdatedAt,
    val videoAnnotations: List<Any>
)

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


