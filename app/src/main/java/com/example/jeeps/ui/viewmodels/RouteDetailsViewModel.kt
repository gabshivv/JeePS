package com.example.jeeps.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jeeps.data.model.FareResult
import com.example.jeeps.data.model.Route
import com.example.jeeps.data.repository.JeePSRepository
import com.example.jeeps.data.repository.SupabaseJeePSRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "RouteDetailViewModel"

data class RouteDetailUiState(
    val route     : Route?      = null,
    val fare      : FareResult? = null,
    val isLoading : Boolean     = true,
    val error     : String?     = null,
)

class RouteDetailViewModel(
    private val repository: JeePSRepository = SupabaseJeePSRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RouteDetailUiState())
    val uiState: StateFlow<RouteDetailUiState> = _uiState.asStateFlow()

    fun load(routeId: Int, originId: Int = 0, destId: Int = 0) {
        Log.d(TAG, "Loading route details for ID: $routeId (Origin: $originId, Dest: $destId)")
        viewModelScope.launch {
            _uiState.value = RouteDetailUiState(isLoading = true)
            try {
                val route = repository.getRouteById(routeId)
                if (route == null) {
                    Log.e(TAG, "Route not found for ID: $routeId")
                }
                
                // Fetch fare from search results for consistency
                val searchedFare = if (originId != 0 && destId != 0) {
                    try {
                        repository.searchRoutes(originId, destId)
                            .find { it.route.id == routeId }
                            ?.fare
                    } catch (e: Exception) {
                        null
                    }
                } else null
                
                // Fallback fare with conditional distance
                val fare = searchedFare ?: route?.let { 
                    val distance = if (it.routeType.lowercase() == "bayan") 36.0 else 30.0
                    val regFare = 13.0 + (if (distance > 4) (distance - 4) * 1.8 else 0.0)
                    FareResult(
                        regularFare = regFare,
                        discountedFare = regFare * 0.8, // 20% deduction
                        distanceKm = distance,
                        stopCount = it.stops.size
                    )
                }

                _uiState.value = RouteDetailUiState(
                    route     = route,
                    fare      = fare,
                    isLoading = false,
                    error     = if (route == null) "Route not found" else null,
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error loading route details", e)
                _uiState.value = RouteDetailUiState(
                    isLoading = false,
                    error     = e.message,
                )
            }
        }
    }
}
