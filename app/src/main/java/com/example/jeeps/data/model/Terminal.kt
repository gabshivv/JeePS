package com.example.jeeps.data.model

data class Terminal(
    val name: String,
    val unitCount: Int,
    val routes: List<String>,
    val isLow: Boolean = false,
)

// Sample data — replace with API calls later
val sampleTerminals = listOf(
    Terminal("Crossing",  12, listOf("Line A", "Line B", "Line C")),
    Terminal("San Pedro",  8, listOf("Line B", "Line D")),
    Terminal("Binan",      6, listOf("Line A", "Line C")),
    Terminal("Cabuyao",    3, listOf("Line C"), isLow = true),
)