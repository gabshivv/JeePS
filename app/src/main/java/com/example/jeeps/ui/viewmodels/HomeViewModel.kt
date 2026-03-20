package com.example.jeeps.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jeeps.data.model.Terminal
import com.example.jeeps.data.repository.JeePSRepository
import com.example.jeeps.data.repository.SupabaseJeePSRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "HomeViewModel"

data class HomeUiState(
    val terminals      : List<Terminal> = emptyList(),
    val detectedOrigin : String         = "Detecting location…",
    val isLoading      : Boolean        = true,
    val error          : String?        = null,
)

class HomeViewModel(
    private val repository: JeePSRepository = SupabaseJeePSRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadTerminals()
    }

    private fun loadTerminals() {
        Log.d(TAG, "Loading terminals...")
        viewModelScope.launch {
            try {
                val terminals = repository.getTerminals()
                Log.d(TAG, "Successfully loaded ${terminals.size} terminals")
                _uiState.value = HomeUiState(
                    terminals      = terminals,
                    detectedOrigin = "Crossing, Calamba",
                    isLoading      = false,
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load terminals", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error     = "Failed to load data: ${e.message}",
                )
            }
        }
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        loadTerminals()
    }
}
