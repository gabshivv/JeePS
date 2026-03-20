package com.example.jeeps.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jeeps.data.model.RouteSearchResult
import com.example.jeeps.data.repository.JeePSRepository
import com.example.jeeps.data.repository.SupabaseJeePSRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RouteResultsUiState(
    val routes    : List<RouteSearchResult> = emptyList(),
    val isLoading : Boolean                 = true,
    val error     : String?                 = null,
)

class RouteResultsViewModel(
    private val repository: JeePSRepository = SupabaseJeePSRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RouteResultsUiState())
    val uiState: StateFlow<RouteResultsUiState> = _uiState.asStateFlow()

    fun search(origin: String, destination: String) {
        viewModelScope.launch {
            _uiState.value = RouteResultsUiState(isLoading = true)
            try {
                // Fetch all routes from Supabase to ensure your new route shows up
                val allRoutes = repository.getAllRoutes()
                
                // Convert to search results
                val results = allRoutes.map { route ->
                    RouteSearchResult(
                        route = route,
                        fare = com.example.jeeps.data.model.FareResult(13.0, 10.4, 0.0, route.stops.size),
                        isBestMatch = false
                    )
                }
                
                _uiState.value = RouteResultsUiState(
                    routes    = results,
                    isLoading = false,
                )
            } catch (e: Exception) {
                _uiState.value = RouteResultsUiState(
                    isLoading = false,
                    error     = e.message,
                )
            }
        }
    }
}