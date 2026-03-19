package com.example.jeeps.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jeeps.data.model.FareResult
import com.example.jeeps.data.model.Route
import com.example.jeeps.data.repository.JeePSRepository
import com.example.jeeps.data.repository.SampleJeePSRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RouteDetailUiState(
    val route     : Route?      = null,
    val fare      : FareResult? = null,
    val isLoading : Boolean     = true,
    val error     : String?     = null,
)

class RouteDetailViewModel(
    private val repository: JeePSRepository = SampleJeePSRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RouteDetailUiState())
    val uiState: StateFlow<RouteDetailUiState> = _uiState.asStateFlow()

    fun load(routeId: Int) {
        viewModelScope.launch {
            _uiState.value = RouteDetailUiState(isLoading = true)
            try {
                val route = repository.getRouteById(routeId)
                // Fare is pre-computed by the repository (from RouteSearchResult).
                // Fetched separately here since detail screen can also be reached
                // directly (e.g. deep link). Backend dev: add a getFareForRoute()
                // call when the API supports it.
                val fare = repository.searchRoutes(0, 0)
                    .find { it.route.id == routeId }
                    ?.fare

                _uiState.value = RouteDetailUiState(
                    route     = route,
                    fare      = fare,
                    isLoading = false,
                    error     = if (route == null) "Route not found" else null,
                )
            } catch (e: Exception) {
                _uiState.value = RouteDetailUiState(
                    isLoading = false,
                    error     = e.message,
                )
            }
        }
    }
}