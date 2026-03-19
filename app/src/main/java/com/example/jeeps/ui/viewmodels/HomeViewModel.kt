package com.example.jeeps.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jeeps.data.model.Terminal
import com.example.jeeps.data.repository.JeePSRepository
import com.example.jeeps.data.repository.SampleJeePSRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val terminals      : List<Terminal> = emptyList(),
    val detectedOrigin : String         = "Detecting location…",
    val isLoading      : Boolean        = true,
    val error          : String?        = null,
)

class HomeViewModel(
    // Backend dev: swap SampleJeePSRepository with the real implementation
    // via dependency injection (Hilt/Koin). Don't change this screen.
    private val repository: JeePSRepository = SampleJeePSRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadTerminals()
    }

    private fun loadTerminals() {
        viewModelScope.launch {
            try {
                val terminals = repository.getTerminals()
                _uiState.value = HomeUiState(
                    terminals      = terminals,
                    // TODO: replace with real GPS-resolved address from LocationManager
                    // For now, hardcoded origin lives here (ViewModel) not in the screen.
                    detectedOrigin = "Crossing, Calamba",
                    isLoading      = false,
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error     = e.message,
                )
            }
        }
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        loadTerminals()
    }
}