package com.habfit.app.features.splash

import androidx.lifecycle.ViewModel
import com.habfit.app.domain.repository.AuthRepository
import com.habfit.app.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun getStartDestination(): String {
        return if (authRepository.currentUser != null) {
            Screen.Main.route
        } else {
            // You might want to check if onboarding is finished via DataStore
            // For now, let's keep it simple: if not logged in, go to Login
            Screen.Login.route
        }
    }
}
