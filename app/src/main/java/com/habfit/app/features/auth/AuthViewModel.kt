package com.habfit.app.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habfit.app.domain.model.User
import com.habfit.app.domain.repository.HabfitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: HabfitRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Please enter your email and password"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            val user = repository.getUserSync()
            if (user == null) {
                repository.saveUserPreferences(
                    name = email.substringBefore("@").replace(".", " ").capitalize(),
                    goal = "Build Consistency & Fitness",
                    level = "Intermediate",
                    activities = "Strength,Running",
                    time = 30,
                    starterHabits = emptyList()
                )
            }
            _isLoading.value = false
            onSuccess()
        }
    }

    fun signup(name: String, email: String, password: String, confirmPassword: String, onSuccess: () -> Unit) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Please fill in all fields"
            return
        }
        if (password != confirmPassword) {
            _errorMessage.value = "Passwords do not match"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            repository.saveUserPreferences(
                name = name,
                goal = "Build Consistency & Fitness",
                level = "Intermediate",
                activities = "Strength,Running",
                time = 30,
                starterHabits = emptyList()
            )
            _isLoading.value = false
            onSuccess()
        }
    }
}
