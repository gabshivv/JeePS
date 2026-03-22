package com.example.jeeps.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jeeps.data.model.Barangay
import com.example.jeeps.data.model.Terminal
import com.example.jeeps.data.repository.DestinationResult
import com.example.jeeps.data.repository.JeePSRepository
import com.example.jeeps.data.repository.SupabaseJeePSRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "HomeViewModel"

data class HomeUiState(
    val terminals         : List<Terminal> = emptyList(),
    val barangays         : List<Barangay> = emptyList(),
    val selectedOrigin    : DestinationResult? = null,
    val selectedDestination: DestinationResult? = null,
    val originResults     : List<DestinationResult> = emptyList(),
    val destResults       : List<DestinationResult> = emptyList(),
    val isLoading         : Boolean        = true,
    val error             : String?        = null,
)

class HomeViewModel(
    private val repository: JeePSRepository = SupabaseJeePSRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                val terminals = repository.getTerminals()
                val barangays = repository.getBarangays()
                _uiState.value = HomeUiState(
                    terminals = terminals,
                    barangays = barangays,
                    isLoading = false,
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load initial data", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error     = "Failed to load data: ${e.message}",
                )
            }
        }
    }

    fun searchOrigin(query: String) {
        if (query.length < 2) {
            _uiState.value = _uiState.value.copy(originResults = emptyList())
            return
        }
        viewModelScope.launch {
            try {
                val results = repository.searchDestinations(query)
                _uiState.value = _uiState.value.copy(originResults = results)
            } catch (e: Exception) {
                Log.e(TAG, "Origin search failed", e)
            }
        }
    }

    fun searchDestination(query: String) {
        if (query.length < 2) {
            _uiState.value = _uiState.value.copy(destResults = emptyList())
            return
        }
        viewModelScope.launch {
            try {
                val results = repository.searchDestinations(query)
                _uiState.value = _uiState.value.copy(destResults = results)
            } catch (e: Exception) {
                Log.e(TAG, "Destination search failed", e)
            }
        }
    }

    fun setOrigin(result: DestinationResult) {
        _uiState.value = _uiState.value.copy(
            selectedOrigin = result,
            originResults = emptyList()
        )
    }

    fun setDestination(result: DestinationResult) {
        _uiState.value = _uiState.value.copy(
            selectedDestination = result,
            destResults = emptyList()
        )
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        loadInitialData()
    }
}
