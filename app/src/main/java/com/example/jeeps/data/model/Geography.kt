package com.example.jeeps.data.model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Province(
    @SerialName("province_id") val id: Int,
    @SerialName("province_name") val name: String
)

@Serializable
data class City(
    @SerialName("city_id") val id: Int,
    @SerialName("city_name") val name: String,
    @SerialName("province_id") val provinceId: Int
)

@Serializable
data class Barangay(
    @SerialName("barangay_id") val id: Int,
    @SerialName("barangay_name") val name: String,
    @SerialName("city_id") val cityId: Int,
    val lat: Double = 0.0,
    val lng: Double = 0.0
)