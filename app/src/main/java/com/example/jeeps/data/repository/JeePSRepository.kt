package com.example.jeeps.data.repository

import com.example.jeeps.data.model.*

// ─────────────────────────────────────────────────────────────────────────────
// JeePSRepository
//
// CONTRACT between frontend and backend.
//
// Frontend calls these functions — backend implements them.
// The frontend never touches raw SQL, API URLs, or auth headers.
//
// Backend dev: implement JeePSRepositoryImpl and swap the stub below.
// All functions are suspend — backend can use Retrofit, Ktor, or Room freely.
// ─────────────────────────────────────────────────────────────────────────────

interface JeePSRepository {

    // ── Routes ────────────────────────────────────────────────────────────────

    /**
     * Returns all routes that connect [originBarangayId] to [destinationBarangayId].
     * Backend should sort by best match first (shortest fare or fewest transfers).
     * Each result includes the computed [FareResult] for the given origin→destination pair.
     */
    suspend fun searchRoutes(
        originBarangayId     : Int,
        destinationBarangayId: Int,
    ): List<RouteSearchResult>

    /**
     * Returns the full detail for a single route by [routeId].
     * Backend should join: route_segment → barangay, waymark → signage,
     * terminal_assignment → terminal, landmark → barangay.
     */
    suspend fun getRouteById(routeId: Int): Route?

    /**
     * Returns all active routes (for a future Explore/browse screen).
     */
    suspend fun getAllRoutes(): List<Route>

    // ── Terminals ─────────────────────────────────────────────────────────────

    /**
     * Returns all terminals, optionally filtered by [cityId].
     * Backend should enrich each terminal with live unit count if available.
     */
    suspend fun getTerminals(cityId: Int? = null): List<Terminal>

    /**
     * Returns a single terminal by [terminalId].
     */
    suspend fun getTerminalById(terminalId: Int): Terminal?

    // ── Geography ─────────────────────────────────────────────────────────────

    /**
     * Returns all provinces.
     */
    suspend fun getProvinces(): List<Province>

    /**
     * Returns all cities, optionally filtered by [provinceId].
     */
    suspend fun getCities(provinceId: Int? = null): List<City>

    /**
     * Returns all barangays, optionally filtered by [cityId].
     * Used to populate the destination autocomplete on the search field.
     */
    suspend fun getBarangays(cityId: Int? = null): List<Barangay>

    /**
     * Searches barangays and landmarks by name [query].
     * Used for the TO field autocomplete — returns matching place names
     * the user can tap to select as their destination.
     */
    suspend fun searchDestinations(query: String): List<DestinationResult>

    // ── Landmarks & Signage ───────────────────────────────────────────────────

    /**
     * Returns all landmarks for a given [barangayId].
     */
    suspend fun getLandmarksByBarangay(barangayId: Int): List<Landmark>

    /**
     * Returns all signage (jeepney signs) for a given [routeId].
     * Resolved via waymark junction table.
     */
    suspend fun getSignageForRoute(routeId: Int): List<Signage>
}

// ─────────────────────────────────────────────────────────────────────────────
// DestinationResult — returned by searchDestinations()
//
// A unified search result that can be either a Barangay or a Landmark.
// The frontend uses this to show autocomplete suggestions in the TO field.
// ─────────────────────────────────────────────────────────────────────────────

data class DestinationResult(
    val id          : Int,
    val displayName : String,   // e.g. "Pulo Market, Brgy. Pulo, Cabuyao"
    val type        : DestinationType,
    val barangayId  : Int,      // the barangay_id the route search needs
)

enum class DestinationType { BARANGAY, LANDMARK }

// ─────────────────────────────────────────────────────────────────────────────
// SampleJeePSRepository — stub that returns hardcoded sample data.
//
// Used right now so the app compiles and runs without a backend.
// Backend dev: delete this class and replace the DI binding with your Impl.
// ─────────────────────────────────────────────────────────────────────────────

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