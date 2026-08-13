package com.habfit.app.features.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habfit.app.domain.model.AssistantTask
import com.habfit.app.domain.repository.AIRepository
import com.habfit.app.domain.repository.HabfitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AIViewModel @Inject constructor(
    private val aiRepository: AIRepository,
    private val habfitRepository: HabfitRepository
) : ViewModel() {

    private val _chatResponse = MutableStateFlow(
        "👋 Hello! I am your HABFIT Coach.\n\nI can suggest daily micro-habits, create personalized HIIT/strength routines, or analyze your consistency trends. What are you working on today?"
    )
    val chatResponse: StateFlow<String> = _chatResponse.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val aiRecommendations: StateFlow<List<AssistantTask>> = habfitRepository.getAIRecommendations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun askAI(prompt: String) {
        if (prompt.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            aiRepository.getChatResponse(prompt).collect { response ->
                _chatResponse.value = response
                _isLoading.value = false
            }
        }
    }

    fun acceptRecommendation(task: AssistantTask) {
        viewModelScope.launch {
            // Add task as a daily mission on Home screen
            habfitRepository.addAssistantTask(
                title = task.title,
                reason = task.reason,
                category = task.category,
                difficulty = task.difficulty,
                rewardPoints = task.rewardPoints,
                source = "DAILY_MISSION"
            )
            habfitRepository.deleteAssistantTask(task.id)
        }
    }

    fun dismissRecommendation(task: AssistantTask) {
        viewModelScope.launch {
            habfitRepository.deleteAssistantTask(task.id)
        }
    }
}
