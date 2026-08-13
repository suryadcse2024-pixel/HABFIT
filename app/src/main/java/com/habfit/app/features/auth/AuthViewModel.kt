package com.habfit.app.features.auth

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.habfit.app.domain.model.User
import com.habfit.app.domain.repository.AuthRepository
import com.habfit.app.domain.repository.HabfitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val habfitRepository: HabfitRepository,
    private val analytics: FirebaseAnalytics
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
            _errorMessage.value = null
            
            authRepository.login(email, password).fold(
                onSuccess = {
                    analytics.logEvent(FirebaseAnalytics.Event.LOGIN, Bundle().apply {
                        putString(FirebaseAnalytics.Param.METHOD, "email")
                    })
                    _isLoading.value = false
                    onSuccess()
                },
                onFailure = {
                    _isLoading.value = false
                    _errorMessage.value = it.localizedMessage ?: "Login failed"
                }
            )
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
            _errorMessage.value = null

            authRepository.signup(name, email, password).fold(
                onSuccess = {
                    analytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, Bundle().apply {
                        putString(FirebaseAnalytics.Param.METHOD, "email")
                    })
                    // Initialize local preferences with cloud data
                    habfitRepository.saveUserPreferences(
                        name = name,
                        goal = "Build Consistency & Fitness",
                        level = "Intermediate",
                        activities = "Strength,Running",
                        time = 30,
                        starterHabits = emptyList()
                    )
                    _isLoading.value = false
                    onSuccess()
                },
                onFailure = {
                    _isLoading.value = false
                    _errorMessage.value = it.localizedMessage ?: "Signup failed"
                }
            )
        }
    }

    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            authRepository.signInWithGoogle(idToken).fold(
                onSuccess = {
                    analytics.logEvent(FirebaseAnalytics.Event.LOGIN, Bundle().apply {
                        putString(FirebaseAnalytics.Param.METHOD, "google")
                    })
                    _isLoading.value = false
                    onSuccess()
                },
                onFailure = {
                    _isLoading.value = false
                    _errorMessage.value = it.localizedMessage ?: "Google Sign-In failed"
                }
            )
        }
    }
}
