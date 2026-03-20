package com.example.jeeps.data.repository

import com.example.jeeps.data.model.*

// ─────────────────────────────────────────────────────────────────────────────
// JeePSRepository
//
// CONTRACT between frontend and backend.
// ─────────────────────────────────────────────────────────────────────────────

interface JeePSRepository {

    suspend fun searchRoutes(
        originBarangayId     : Int,
        destinationBarangayId: Int,
    ): List<RouteSearchResult>

    suspend fun getRouteById(routeId: Int): Route?

    suspend fun getAllRoutes(): List<Route>

    suspend fun getTerminals(cityId: Int? = null): List<Terminal>

    suspend fun getTerminalById(terminalId: Int): Terminal?

    suspend fun getProvinces(): List<Province>

    suspend fun getCities(provinceId: Int? = null): List<City>

    suspend fun getBarangays(cityId: Int? = null): List<Barangay>

    suspend fun searchDestinations(query: String): List<DestinationResult>

    suspend fun getLandmarksByBarangay(barangayId: Int): List<Landmark>

    suspend fun getSignageForRoute(routeId: Int): List<Signage>
}

data class DestinationResult(
    val id          : Int,
    val displayName : String,
    val type        : DestinationType,
    val barangayId  : Int,
)

enum class DestinationType { BARANGAY, LANDMARK }

class SampleJeePSRepository : JeePSRepository {

    override suspend fun searchRoutes(
        originBarangayId     : Int,
        destinationBarangayId: Int,
    ): List<RouteSearchResult> = sampleRoutes

    override suspend fun getRouteById(routeId: Int): Route? =
        sampleRoutes.find { it.route.id == routeId }?.route

    override suspend fun getAllRoutes(): List<Route> =
        sampleRoutes.map { it.route }

    override suspend fun getTerminals(cityId: Int?): List<Terminal> =
        if (cityId == null) sampleTerminals
        else sampleTerminals.filter { it.cityId == cityId }

    override suspend fun getTerminalById(terminalId: Int): Terminal? =
        sampleTerminals.find { it.id == terminalId }

    override suspend fun getProvinces(): List<Province> =
        listOf(sampleProvince)

    override suspend fun getCities(provinceId: Int?): List<City> =
        if (provinceId == null) sampleCities
        else sampleCities.filter { it.provinceId == provinceId }

    override suspend fun getBarangays(cityId: Int?): List<Barangay> =
        if (cityId == null) sampleBarangays
        else sampleBarangays.filter { it.cityId == cityId }

    override suspend fun searchDestinations(query: String): List<DestinationResult> {
        if (query.isBlank()) return emptyList()
        val q = query.lowercase()
        return sampleBarangays
            .filter { it.name.lowercase().contains(q) }
            .map { brgy ->
                DestinationResult(
                    id          = brgy.id,
                    displayName = brgy.name,
                    type        = DestinationType.BARANGAY,
                    barangayId  = brgy.id,
                )
            }
    }

    override suspend fun getLandmarksByBarangay(barangayId: Int): List<Landmark> =
        sampleRoutes
            .flatMap { it.route.landmarks }
            .filter { it.barangayId == barangayId }

    override suspend fun getSignageForRoute(routeId: Int): List<Signage> =
        sampleRoutes
            .find { it.route.id == routeId }
            ?.route?.waymarks
            ?.map { Signage(it.signageId, it.signageName) }
            ?: emptyList()
}

// ── Sample Data ──────────────────────────────────────────────────────────────

val sampleProvince = Province(1, "Laguna")

val sampleCities = listOf(
    City(1, "Cabuyao", 1),
    City(2, "Santa Rosa", 1),
    City(3, "Calamba", 1)
)

val sampleBarangays = listOf(
    Barangay(1, "Pulo", 1),
    Barangay(2, "Banay-Banay", 1),
    Barangay(3, "Niugan", 1),
    Barangay(4, "Sala", 1),
    Barangay(5, "Bigaa", 1)
)

val sampleTerminals = listOf(
    Terminal(
        id = 1,
        name = "Cabuyao Central Terminal",
        cityId = 1,
        unitCount = 12,
        isLow = false,
        routes = listOf("Cabuyao - Crossing", "Cabuyao - Sta. Rosa")
    )
)

val sampleRoutes = listOf(
    RouteSearchResult(
        route = Route(
            id = 1,
            displayName = "Cabuyao - Crossing via Pulo",
            routeCode = "CAB-CRS-01",
            origin = "Cabuyao Bayan",
            destination = "Crossing",
            routeType = RouteType.NATIONAL_HIGHWAY,
            stops = listOf(
                RouteSegment(1, 1, "Pulo", 0.0),
                RouteSegment(1, 2, "Banay-Banay", 2.5),
                RouteSegment(1, 3, "Niugan", 5.0)
            ),
            landmarks = listOf(
                Landmark(1, "Pulo Market", 1, "Pulo")
            )
        ),
        fare = FareResult(
            regularFare = 15.0,
            discountedFare = 12.0,
            distanceKm = 5.0,
            stopCount = 3
        ),
        isBestMatch = true
    )
)
