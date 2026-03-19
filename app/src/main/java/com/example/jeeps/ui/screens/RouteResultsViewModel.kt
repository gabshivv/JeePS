package com.example.jeeps.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jeeps.data.model.RouteSearchResult
import com.example.jeeps.data.repository.JeePSRepository
import com.example.jeeps.data.repository.SampleJeePSRepository
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
    private val repository: JeePSRepository = SampleJeePSRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RouteResultsUiState())
    val uiState: StateFlow<RouteResultsUiState> = _uiState.asStateFlow()

    /**
     * Called by the screen on launch with the origin/destination passed from
     * the nav graph. Backend dev: resolve barangay IDs from the string names,
     * then call repository.searchRoutes(originId, destinationId).
     */
    fun search(origin: String, destination: String) {
        viewModelScope.launch {
            _uiState.value = RouteResultsUiState(isLoading = true)
            try {
                // TODO (backend): resolve origin/destination strings to barangay IDs,
                // then call repository.searchRoutes(originBarangayId, destinationBarangayId)
                // For now: return all sample routes regardless of input.
                val routes = repository.searchRoutes(
                    originBarangayId      = 0,  // placeholder
                    destinationBarangayId = 0,  // placeholder
                )
                _uiState.value = RouteResultsUiState(
                    routes    = routes,
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