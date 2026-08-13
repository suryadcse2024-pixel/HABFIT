package com.habfit.app.features.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habfit.app.data.local.ChatDao
import com.habfit.app.domain.model.AssistantTask
import com.habfit.app.domain.model.ChatMessage
import com.habfit.app.domain.repository.AIRepository
import com.habfit.app.domain.repository.GroqRepository
import com.habfit.app.domain.repository.HabfitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AIViewModel @Inject constructor(
    private val aiRepository: AIRepository,
    private val groqRepository: GroqRepository,
    private val habfitRepository: HabfitRepository,
    private val chatDao: ChatDao
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val chatMessages: StateFlow<List<ChatMessage>> = chatDao.getAllMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aiRecommendations: StateFlow<List<AssistantTask>> = habfitRepository.getAIRecommendations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun askAI(prompt: String) {
        if (prompt.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            groqRepository.getChatResponse(prompt).collect { response ->
                _isLoading.value = false
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            chatDao.clearHistory()
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
