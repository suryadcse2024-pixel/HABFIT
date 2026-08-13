package com.habfit.app.features.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habfit.app.domain.repository.AIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AIViewModel @Inject constructor(
    private val aiRepository: AIRepository
) : ViewModel() {

    private val _chatResponse = MutableStateFlow("")
    val chatResponse: StateFlow<String> = _chatResponse

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun askAI(prompt: String) {
        viewModelScope.launch {
            _isLoading.value = true
            aiRepository.getChatResponse(prompt).collect { response ->
                _chatResponse.value = response
                _isLoading.value = false
            }
        }
    }
}
