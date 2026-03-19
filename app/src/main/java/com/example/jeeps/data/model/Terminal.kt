package com.example.jeeps.data.model

// ── terminal ──────────────────────────────────────────────────────────────────
// ERD: terminal_id (PK), city_id (FK), terminal_name
data class Terminal(
    val id         : Int,
    val cityId     : Int,          // ERD FK → city.city_id
    val name       : String,       // ERD: terminal_name
    // ── Frontend display helpers (not in ERD — computed/enriched by backend) ─
    val unitCount  : Int,          // how many jeepneys are currently at this terminal
    val routes     : List<String>, // route codes serving this terminal (e.g. "Line A")
    val isLow      : Boolean = false, // true when unitCount is below a threshold
)

// ── Sample data ───────────────────────────────────────────────────────────────
// PLACEHOLDER — replace with repository/API response when backend is ready.
// cityId values match sampleCities in Geography.kt:
//   1 = San Pedro, 2 = Biñan, 3 = Cabuyao, 4 = Calamba

val sampleTerminals = listOf(
    Terminal(id = 1, cityId = 4, name = "Crossing",  unitCount = 12, routes = listOf("Line A", "Line B", "Line C")),
    Terminal(id = 2, cityId = 1, name = "San Pedro", unitCount = 8,  routes = listOf("Line B", "Line D")),
    Terminal(id = 3, cityId = 2, name = "Binan",     unitCount = 6,  routes = listOf("Line A", "Line C")),
    Terminal(id = 4, cityId = 3, name = "Cabuyao",   unitCount = 3,  routes = listOf("Line C"), isLow = true),
)