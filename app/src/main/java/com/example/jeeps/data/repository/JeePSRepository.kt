package com.example.jeeps.data.repository

import android.util.Log
import com.example.jeeps.data.model.*
import com.example.jeeps.SupabaseClientProvider
import com.google.android.gms.maps.model.LatLng
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
    val lat: Double = 0.0,
    val lng: Double = 0.0
)

enum class DestinationType { BARANGAY, LANDMARK }

class SupabaseJeePSRepository(
    private val client: SupabaseClient = SupabaseClientProvider.client
) : JeePSRepository {

    override suspend fun searchRoutes(originId: Int, destinationId: Int): List<RouteSearchResult> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Searching routes from Brgy $originId to $destinationId")
            val allRoutes = getAllRoutes()
            
            val matchingRoutes = allRoutes.filter { route ->
                val stopIds = route.stops.map { it.barangayId }
                val originIdx = stopIds.indexOf(originId)
                val destIdx = stopIds.indexOf(destinationId)
                originIdx != -1 && destIdx != -1 && originIdx < destIdx
            }

            matchingRoutes.map { route ->
                val distance = route.stops.size * 1.5 
                RouteSearchResult(
                    route = route,
                    fare = FareResult(
                        regularFare = 13.0 + (if (distance > 4) (distance - 4) * 1.8 else 0.0),
                        discountedFare = 10.4 + (if (distance > 4) (distance - 4) * 1.5 else 0.0),
                        distanceKm = distance,
                        stopCount = route.stops.size
                    ),
                    isBestMatch = false
                )
            }.sortedBy { it.fare.stopCount }
        } catch (e: Exception) {
            Log.e(TAG, "Error searching routes", e)
            emptyList()
        }
    }

    override suspend fun getRouteById(routeId: Int): Route? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching details for Route ID: $routeId")
            val result = client.postgrest["route"]
                .select(columns = Columns.raw("""
                    *,
                    route_segment(*, barangay(*)),
                    terminal_assignment(*, terminal(*))
                """.trimIndent())) {
                    filter {
                        eq("route_id", routeId)
                    }
                }
                .decodeSingleOrNull<Route>()
            
            return@withContext result?.let { enrichRouteWithCoordinates(it) }
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
            return@withContext result.map { enrichRouteWithCoordinates(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching all routes", e)
            throw e
        }
    }

    private fun enrichRouteWithCoordinates(route: Route): Route {
        return route.copy(
            stops = route.stops.map { stop ->
                if (stop.barangay != null && stop.barangay.lat == 0.0) {
                    val coords = getBarangayCoordinates(stop.barangay.name, route.routeType)
                    stop.copy(barangay = stop.barangay.copy(lat = coords.latitude, lng = coords.longitude))
                } else stop
            }
        )
    }

    override suspend fun getTerminals(cityId: Int?): List<Terminal> = withContext(Dispatchers.IO) {
        try {
            client.postgrest["terminal"].select {
                if (cityId != null) filter { eq("city_id", cityId) }
            }.decodeList<Terminal>()
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching terminals", e)
            emptyList()
        }
    }

    override suspend fun getBarangays(cityId: Int?): List<Barangay> = withContext(Dispatchers.IO) {
        try {
            val list = client.postgrest["barangay"].select {
                if (cityId != null) filter { eq("city_id", cityId) }
            }.decodeList<Barangay>()
            
            list.map { brgy ->
                if (brgy.lat == 0.0) {
                    val coords = getBarangayCoordinates(brgy.name, "hiway")
                    brgy.copy(lat = coords.latitude, lng = coords.longitude)
                } else brgy
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching barangays", e)
            emptyList()
        }
    }

    override suspend fun searchDestinations(query: String): List<DestinationResult> = withContext(Dispatchers.IO) {
        val results = getBarangays().filter { it.name.contains(query, ignoreCase = true) }
        results.map { 
            DestinationResult(it.id, it.name, DestinationType.BARANGAY, it.id, it.lat, it.lng)
        }
    }

    private fun getBarangayCoordinates(name: String, type: String): LatLng {
        val lowerName = name.lowercase()
        val isBayan = type.lowercase() == "bayan"
        
        return when {
            lowerName.contains("nueva") -> LatLng(14.364074, 121.054073)
            lowerName.contains("canlalay") -> LatLng(14.345371, 121.066956)
            lowerName.contains("san antonio") -> LatLng(14.331571, 121.085057)
            lowerName.contains("platero") -> LatLng(14.326311, 121.092885)
            lowerName.contains("tagapo") -> if (isBayan) LatLng(14.318347, 121.103337) else LatLng(14.321298, 121.096825)
            lowerName.contains("labas") -> LatLng(14.309741, 121.111347)
            lowerName.contains("pook") -> LatLng(14.303920, 121.109266)
            lowerName.contains("macabling") -> LatLng(14.309203, 121.101156)
            lowerName.contains("balibago") -> LatLng(14.295484, 121.106032)
            lowerName.contains("dila") -> LatLng(14.293600, 121.106747)
            lowerName.contains("dita") -> if (isBayan) LatLng(14.281827, 121.114924) else LatLng(14.282639, 121.111039)
            lowerName.contains("poblacion i") || lowerName.contains("barangay i") -> LatLng(14.279859, 121.122465)
            lowerName.contains("poblacion ii") || lowerName.contains("barangay ii") -> LatLng(14.278232, 121.123062)
            lowerName.contains("poblacion iii") || lowerName.contains("barangay iii") -> LatLng(14.275218, 121.124391)
            lowerName.contains("sala") -> LatLng(14.267103, 121.126856)
            lowerName.contains("niugan") -> LatLng(14.265405, 121.127138)
            lowerName.contains("banay-banay") -> LatLng(14.257544, 121.128000)
            lowerName.contains("pulo") -> LatLng(14.248767, 121.129300)
            lowerName.contains("san isidro") -> LatLng(14.238663, 121.132909)
            lowerName.contains("banlic") -> LatLng(14.236551, 121.133891)
            lowerName.contains("mamatid") -> LatLng(14.228151, 121.139046)
            lowerName.contains("san cristobal") -> LatLng(14.226513, 121.139770)
            lowerName.contains("paciano") -> LatLng(14.219346, 121.139204)
            lowerName.contains("parian") -> LatLng(14.217809, 121.142339)
            lowerName.contains("real") -> LatLng(14.205195, 121.154650)
            lowerName.contains("crossing") -> LatLng(14.2137, 121.1620)
            else -> LatLng(14.2137, 121.1620)
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
