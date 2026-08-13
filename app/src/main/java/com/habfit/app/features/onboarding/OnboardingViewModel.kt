package com.habfit.app.features.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habfit.app.domain.repository.HabfitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: HabfitRepository
) : ViewModel() {

    private val _selectedGoal = MutableStateFlow("Build Consistency & Fitness")
    val selectedGoal: StateFlow<String> = _selectedGoal.asStateFlow()

    private val _selectedLevel = MutableStateFlow("Intermediate")
    val selectedLevel: StateFlow<String> = _selectedLevel.asStateFlow()

    private val _selectedActivities = MutableStateFlow(setOf("Strength / Gym", "Running / Walking"))
    val selectedActivities: StateFlow<Set<String>> = _selectedActivities.asStateFlow()

    private val _selectedTime = MutableStateFlow(30)
    val selectedTime: StateFlow<Int> = _selectedTime.asStateFlow()

    private val _selectedHabits = MutableStateFlow(setOf("Drink 2.5L Water", "Morning Stretch & Mobility", "Hit 8,000 Daily Steps"))
    val selectedHabits: StateFlow<Set<String>> = _selectedHabits.asStateFlow()

    fun setGoal(goal: String) {
        _selectedGoal.value = goal
    }

    fun setLevel(level: String) {
        _selectedLevel.value = level
    }

    fun toggleActivity(activity: String) {
        val current = _selectedActivities.value.toMutableSet()
        if (current.contains(activity)) {
            if (current.size > 1) current.remove(activity)
        } else {
            current.add(activity)
        }
        _selectedActivities.value = current
    }

    fun setTime(minutes: Int) {
        _selectedTime.value = minutes
    }

    fun toggleHabit(habit: String) {
        val current = _selectedHabits.value.toMutableSet()
        if (current.contains(habit)) {
            if (current.size > 1) current.remove(habit)
        } else {
            current.add(habit)
        }
        _selectedHabits.value = current
    }

    fun completeOnboarding(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.saveUserPreferences(
                name = "Alex Vance",
                goal = _selectedGoal.value,
                level = _selectedLevel.value,
                activities = _selectedActivities.value.joinToString(","),
                time = _selectedTime.value,
                starterHabits = _selectedHabits.value.toList()
            )
            onSuccess()
        }
    }
}
