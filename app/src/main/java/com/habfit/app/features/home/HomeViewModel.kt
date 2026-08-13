package com.habfit.app.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habfit.app.domain.model.AssistantTask
import com.habfit.app.domain.model.Habit
import com.habfit.app.domain.model.LifeScoreData
import com.habfit.app.domain.model.User
import com.habfit.app.domain.model.Workout
import com.habfit.app.domain.repository.HabfitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HabfitRepository
) : ViewModel() {

    val user: StateFlow<User?> = repository.getUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val lifeScore: StateFlow<LifeScoreData> = repository.getLifeScore()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LifeScoreData())

    val dailyMissions: StateFlow<List<AssistantTask>> = repository.getDailyMissions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val habits: StateFlow<List<Habit>> = repository.getAllHabits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentWorkouts: StateFlow<List<Workout>> = repository.getRecentWorkouts(5)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCalories: StateFlow<Int> = recentWorkouts.map { list ->
        list.sumOf { it.caloriesBurned }.coerceAtLeast(350)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 350)

    val totalSteps: StateFlow<Int> = habits.map { list ->
        val stepHabit = list.find { it.name.contains("step", ignoreCase = true) }
        if (stepHabit?.isCompletedToday == true) 8420 else 6150
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 6150)

    fun toggleMission(task: AssistantTask) {
        viewModelScope.launch {
            repository.toggleMissionCompletion(task)
        }
    }

    fun toggleHabit(habit: Habit) {
        viewModelScope.launch {
            repository.toggleHabitCompletion(habit)
        }
    }
}
