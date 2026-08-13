package com.habfit.app.features.fitness

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habfit.app.domain.model.FitnessGoal
import com.habfit.app.domain.model.Workout
import com.habfit.app.domain.repository.HabfitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FitnessViewModel @Inject constructor(
    private val repository: HabfitRepository
) : ViewModel() {

    val goals: StateFlow<List<FitnessGoal>> = repository.getAllGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val workouts: StateFlow<List<Workout>> = repository.getAllWorkouts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedTab = MutableStateFlow(0) // 0: Workouts & Goals, 1: Nearby Gyms Map
    val selectedTab: StateFlow<Int> = _selectedTab

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    fun logWorkout(
        title: String,
        type: String,
        durationMinutes: Int,
        caloriesBurned: Int,
        distanceKm: Float,
        intensity: String,
        notes: String
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.logWorkout(
                title = title,
                type = type,
                durationMinutes = durationMinutes,
                caloriesBurned = caloriesBurned,
                distanceKm = distanceKm,
                intensity = intensity,
                notes = notes
            )
        }
    }

    fun addGoal(title: String, type: String, targetValue: Float, unit: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addFitnessGoal(title, type, targetValue, unit)
        }
    }

    fun deleteWorkout(id: Int) {
        viewModelScope.launch {
            repository.deleteWorkout(id)
        }
    }

    fun deleteGoal(id: Int) {
        viewModelScope.launch {
            repository.deleteFitnessGoal(id)
        }
    }
}
