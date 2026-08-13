package com.habfit.app.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habfit.app.domain.model.Badge
import com.habfit.app.domain.model.User
import com.habfit.app.domain.repository.HabfitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: HabfitRepository
) : ViewModel() {

    val user: StateFlow<User?> = repository.getUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val badges: StateFlow<List<Badge>> = repository.getAllBadges()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateNotifications(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateNotifications(enabled)
        }
    }

    fun updateProfile(name: String, goal: String) {
        viewModelScope.launch {
            val current = repository.getUserSync() ?: return@launch
            repository.saveUserPreferences(
                name = name,
                goal = goal,
                level = current.experienceLevel,
                activities = current.preferredActivities,
                time = current.availableTimeMinutes,
                starterHabits = emptyList()
            )
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.logout()
            onSuccess()
        }
    }
}
