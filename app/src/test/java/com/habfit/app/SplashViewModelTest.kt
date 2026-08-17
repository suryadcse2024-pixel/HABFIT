package com.habfit.app

import com.habfit.app.domain.repository.AuthRepository
import com.habfit.app.domain.repository.HabfitRepository
import com.habfit.app.features.splash.SplashViewModel
import com.habfit.app.ui.navigation.Screen
import com.google.firebase.auth.FirebaseUser
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var habfitRepository: HabfitRepository
    private lateinit var viewModel: SplashViewModel

    @Before
    fun setup() {
        authRepository = mockk()
        habfitRepository = mockk()
        viewModel = SplashViewModel(authRepository, habfitRepository)
    }

    @Test
    fun `getStartDestination should return Login when user is null`() = runTest {
        // Arrange
        every { authRepository.currentUser } returns null

        // Act
        val destination = viewModel.getStartDestination()

        // Assert
        assertEquals(Screen.Login.route, destination)
    }

    @Test
    fun `getStartDestination should return Onboarding when user is not null and onboarding not completed`() = runTest {
        // Arrange
        val mockUser = mockk<FirebaseUser>()
        every { authRepository.currentUser } returns mockUser
        coEvery { habfitRepository.checkOnboardingStatus() } returns false

        // Act
        val destination = viewModel.getStartDestination()

        // Assert
        assertEquals(Screen.Onboarding.route, destination)
    }

    @Test
    fun `getStartDestination should return Main when user is not null and onboarding completed`() = runTest {
        // Arrange
        val mockUser = mockk<FirebaseUser>()
        every { authRepository.currentUser } returns mockUser
        coEvery { habfitRepository.checkOnboardingStatus() } returns true

        // Act
        val destination = viewModel.getStartDestination()

        // Assert
        assertEquals(Screen.Main.route, destination)
    }
}
