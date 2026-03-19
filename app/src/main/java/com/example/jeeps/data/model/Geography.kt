package com.example.jeeps.data.model

// ERD: province_id (PK), province_name
data class Province(
    val id   : Int,
    val name : String,
)

// ERD: city_id (PK), province_id (FK), city_name
data class City(
    val id         : Int,
    val provinceId : Int,
    val name       : String,
)

// ERD: barangay_id (PK), city_id (FK), barangay_name
// Bridge between city and route_segment — every stop references a barangay.
data class Barangay(
    val id     : Int,
    val cityId : Int,
    val name   : String,
)

// Sample geography (Laguna scope — San Pedro to Calamba)
// Replace with API response when backend is ready.

val sampleProvince = Province(id = 1, name = "Laguna")

val sampleCities = listOf(
    City(id = 1, provinceId = 1, name = "San Pedro"),
    City(id = 2, provinceId = 1, name = "Biñan"),
    City(id = 3, provinceId = 1, name = "Cabuyao"),
    City(id = 4, provinceId = 1, name = "Calamba"),
)

val sampleBarangays = listOf(
    // Calamba
    Barangay(id = 1, cityId = 4, name = "Crossing"),
    Barangay(id = 2, cityId = 4, name = "Mamatid"),
    Barangay(id = 3, cityId = 4, name = "Pulo"),
    // Cabuyao
    Barangay(id = 4, cityId = 3, name = "Uno"),
    Barangay(id = 5, cityId = 3, name = "Cabuyao Bayan"),
    // Biñan
    Barangay(id = 6, cityId = 2, name = "Parian"),
    Barangay(id = 7, cityId = 2, name = "Barandal"),
    Barangay(id = 8, cityId = 2, name = "Complex"),
)