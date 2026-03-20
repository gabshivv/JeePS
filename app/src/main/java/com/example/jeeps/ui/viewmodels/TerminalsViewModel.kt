package com.example.jeeps.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jeeps.data.model.Terminal
import com.example.jeeps.data.repository.JeePSRepository
import com.example.jeeps.data.repository.SupabaseJeePSRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TerminalsUiState(
    val terminals : List<Terminal> = emptyList(),
    val isLoading : Boolean        = true,
    val error     : String?        = null,
)

class TerminalsViewModel(
    private val repository: JeePSRepository = SupabaseJeePSRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TerminalsUiState())
    val uiState: StateFlow<TerminalsUiState> = _uiState.asStateFlow()

    init {
        loadTerminals()
    }

    private fun loadTerminals() {
        viewModelScope.launch {
            try {
                val terminals = repository.getTerminals()
                _uiState.value = TerminalsUiState(
                    terminals = terminals,
                    isLoading = false,
                )
            } catch (e: Exception) {
                _uiState.value = TerminalsUiState(
                    isLoading = false,
                    error     = e.message,
                )
            }
        }
    }
}
