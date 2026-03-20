package com.example.jeeps.data.repository

import android.util.Log
import com.example.jeeps.data.model.*
import com.example.jeeps.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "JeePSRepository"

interface JeePSRepository {
    suspend fun searchRoutes(originId: Int, destinationId: Int): List<RouteSearchResult>
    suspend fun getRouteById(routeId: Int): Route?
    suspend fun getAllRoutes(): List<Route>
    suspend fun getTerminals(cityId: Int? = null): List<Terminal>
    suspend fun getBarangays(cityId: Int? = null): List<Barangay>
    suspend fun searchDestinations(query: String): List<DestinationResult>
    suspend fun getProvinces(): List<Province>
    suspend fun getCities(provinceId: Int? = null): List<City>
    suspend fun getLandmarksByBarangay(barangayId: Int): List<Landmark>
    suspend fun getSignageForRoute(routeId: Int): List<Signage>
}

data class DestinationResult(
    val id: Int,
    val displayName: String,
    val type: DestinationType,
    val barangayId: Int,
)

enum class DestinationType { BARANGAY, LANDMARK }

class SupabaseJeePSRepository(
    private val client: SupabaseClient = SupabaseClientProvider.client
) : JeePSRepository {

    override suspend fun searchRoutes(originId: Int, destinationId: Int): List<RouteSearchResult> = withContext(Dispatchers.IO) {
        try {
            val routes = getAllRoutes()
            routes.map { route ->
                RouteSearchResult(
                    route = route,
                    fare = FareResult(13.0, 10.4, 0.0, route.stops.size),
                    isBestMatch = false
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error searching routes", e)
            emptyList()
        }
    }

    override suspend fun getRouteById(routeId: Int): Route? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching route detail for ID: $routeId")
            val result = client.postgrest["route"]
                .select(columns = Columns.raw("""
                    *,
                    route_segment(*, barangay(*)),
                    terminal_assignment(*, terminal(*))
                """.trimIndent())) {
                    filter {
                        // Use the column name directly to ensure the filter hits the main table
                        eq("route_id", routeId)
                    }
                }
                .decodeSingleOrNull<Route>()
            
            if (result == null) {
                Log.w(TAG, "No route found in Supabase for ID: $routeId")
            } else {
                Log.d(TAG, "Successfully fetched route: ${result.displayName}")
            }
            result
        } catch (e: Exception) {
            Log.e(TAG, "Exception fetching route $routeId", e)
            null
        }
    }

    override suspend fun getAllRoutes(): List<Route> = withContext(Dispatchers.IO) {
        try {
            val result = client.postgrest["route"]
                .select(columns = Columns.raw("""
                    *,
                    route_segment(*, barangay(*)),
                    terminal_assignment(*, terminal(*))
                """.trimIndent()))
                .decodeList<Route>()
            Log.d(TAG, "Fetched ${result.size} routes from Supabase")
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching all routes", e)
            throw e
        }
    }

    override suspend fun getTerminals(cityId: Int?): List<Terminal> = withContext(Dispatchers.IO) {
        try {
            val result = client.postgrest["terminal"].select {
                if (cityId != null) filter { eq("city_id", cityId) }
            }.decodeList<Terminal>()
            Log.d(TAG, "Fetched ${result.size} terminals")
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching terminals", e)
            throw e
        }
    }

    override suspend fun getBarangays(cityId: Int?): List<Barangay> = withContext(Dispatchers.IO) {
        client.postgrest["barangay"].select {
            if (cityId != null) filter { eq("city_id", cityId) }
        }.decodeList<Barangay>()
    }

    override suspend fun searchDestinations(query: String): List<DestinationResult> = withContext(Dispatchers.IO) {
        val results = client.postgrest["barangay"]
            .select { filter { ilike("barangay_name", "%$query%") } }
            .decodeList<Barangay>()
        
        results.map { 
            DestinationResult(it.id, it.name, DestinationType.BARANGAY, it.id)
        }
    }

    override suspend fun getProvinces(): List<Province> = withContext(Dispatchers.IO) {
        client.postgrest["province"].select().decodeList<Province>()
    }

    override suspend fun getCities(provinceId: Int?): List<City> = withContext(Dispatchers.IO) {
        client.postgrest["city"].select {
            if (provinceId != null) filter { eq("province_id", provinceId) }
        }.decodeList<City>()
    }

    override suspend fun getLandmarksByBarangay(barangayId: Int): List<Landmark> = withContext(Dispatchers.IO) {
        client.postgrest["landmark"].select {
            filter { eq("barangay_id", barangayId) }
        }.decodeList<Landmark>()
    }

    override suspend fun getSignageForRoute(routeId: Int): List<Signage> = withContext(Dispatchers.IO) {
        emptyList()
    }
}
