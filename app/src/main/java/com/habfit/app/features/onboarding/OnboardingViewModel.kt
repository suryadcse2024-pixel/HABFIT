package com.habfit.app.features.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.habfit.app.domain.model.OnboardingData
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

    private val _selectedGoals = MutableStateFlow<Set<String>>(emptySet())
    val selectedGoals: StateFlow<Set<String>> = _selectedGoals.asStateFlow()

    private val _selectedLevel = MutableStateFlow<String?>(null)
    val selectedLevel: StateFlow<String?> = _selectedLevel.asStateFlow()

    private val _selectedActivities = MutableStateFlow<Set<String>>(emptySet())
    val selectedActivities: StateFlow<Set<String>> = _selectedActivities.asStateFlow()

    private val _selectedTime = MutableStateFlow<String?>(null)
    val selectedTime: StateFlow<String?> = _selectedTime.asStateFlow()

    private val _selectedReminder = MutableStateFlow<String?>(null)
    val selectedReminder: StateFlow<String?> = _selectedReminder.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun toggleGoal(goal: String) {
        val current = _selectedGoals.value.toMutableSet()
        if (current.contains(goal)) {
            current.remove(goal)
        } else {
            current.add(goal)
        }
        _selectedGoals.value = current
    }

    fun setLevel(level: String) {
        _selectedLevel.value = level
    }

    fun toggleActivity(activity: String) {
        val current = _selectedActivities.value.toMutableSet()
        if (current.contains(activity)) {
            current.remove(activity)
        } else {
            current.add(activity)
        }
        _selectedActivities.value = current
    }

    fun setTime(time: String) {
        _selectedTime.value = time
    }

    fun setReminder(reminder: String) {
        _selectedReminder.value = reminder
    }

    fun completeOnboarding(onSuccess: () -> Unit) {
        val goals = _selectedGoals.value.toList()
        val level = _selectedLevel.value ?: run {
            Log.e("OnboardingViewModel", "Missing level")
            return
        }
        val activities = _selectedActivities.value.toList()
        val time = _selectedTime.value ?: run {
            Log.e("OnboardingViewModel", "Missing time")
            return
        }
        val reminder = _selectedReminder.value ?: run {
            Log.e("OnboardingViewModel", "Missing reminder")
            return
        }

        if (goals.isEmpty()) {
            _errorMessage.value = "Please select at least one goal"
            Log.e("OnboardingViewModel", "Goals are empty")
            return
        }
        if (activities.isEmpty()) {
            _errorMessage.value = "Please select at least one activity"
            Log.e("OnboardingViewModel", "Activities are empty")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.completeOnboarding(
                    OnboardingData(
                        goals = goals,
                        experienceLevel = level,
                        preferredActivities = activities,
                        availableTime = time,
                        reminderPreference = reminder
                    )
                )
                Log.d("OnboardingViewModel", "Onboarding completed successfully")
                onSuccess()
            } catch (e: Exception) {
                Log.e("OnboardingViewModel", "Onboarding completion failed", e)
                _errorMessage.value = "Failed to save preferences. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }
}
