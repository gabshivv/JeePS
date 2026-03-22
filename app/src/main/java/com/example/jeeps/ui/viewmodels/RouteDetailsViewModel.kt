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

    fun load(routeId: Int) {
        Log.d(TAG, "Loading route details for ID: $routeId")
        viewModelScope.launch {
            _uiState.value = RouteDetailUiState(isLoading = true)
            try {
                val route = repository.getRouteById(routeId)
                if (route == null) {
                    Log.e(TAG, "Route not found for ID: $routeId")
                }
                
                // Fetch fare from search if possible, otherwise use a default one based on route stops
                // Note: searchRoutes(0,0) is a hack to get all routes from search results if repository allows it
                val searchedFare = try {
                    repository.searchRoutes(0, 0)
                        .find { it.route.id == routeId }
                        ?.fare
                } catch (e: Exception) {
                    null
                }
                
                val fare = searchedFare ?: route?.let { 
                    FareResult(
                        regularFare = 13.0, 
                        discountedFare = 10.4, 
                        distanceKm = 0.0, 
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
