package com.example.jeeps.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Terminal(
    @SerialName("terminal_id") val id: Int,
    @SerialName("terminal_name") val name: String,
    @SerialName("city_id") val cityId: Int,
    val unitCount: Int = 5,
    val isLow: Boolean = false,
    val routes: List<String> = emptyList()
)