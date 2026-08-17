package com.habfit.app.features.splash

import androidx.lifecycle.ViewModel
import com.habfit.app.domain.repository.AuthRepository
import com.habfit.app.domain.repository.HabfitRepository
import com.habfit.app.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val habfitRepository: HabfitRepository
) : ViewModel() {

    suspend fun getStartDestination(): String {
        val user = authRepository.currentUser
        return if (user != null) {
            val isOnboardingCompleted = habfitRepository.checkOnboardingStatus()
            if (isOnboardingCompleted) {
                Screen.Main.route
            } else {
                Screen.Onboarding.route
            }
        } else {
            Screen.Login.route
        }
    }
}
