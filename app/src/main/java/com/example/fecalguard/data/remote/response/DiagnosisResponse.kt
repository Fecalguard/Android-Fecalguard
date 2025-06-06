package com.example.fecalguard.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DiagnosisResponse(
	@field:SerializedName("datetime")
	val datetime: String,

	@field:SerializedName("image_url")
	val imageUrl: String,

	@field:SerializedName("predicted_class")
	val predictedClass: String,

	@field:SerializedName("confidence")
	val confidence: Double,

	@field:SerializedName("current_user")
	val currentUser: String
): Parcelable
