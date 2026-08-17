package com.habfit.app

import com.habfit.app.domain.repository.AuthRepository
import com.habfit.app.domain.repository.HabfitRepository
import com.habfit.app.features.auth.AuthViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseUser
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var authRepository: AuthRepository
    private lateinit var habfitRepository: HabfitRepository
    private lateinit var analytics: FirebaseAnalytics
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk()
        habfitRepository = mockk()
        analytics = mockk(relaxed = true)
        viewModel = AuthViewModel(authRepository, habfitRepository, analytics)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login success should navigate to main if onboarding completed`() {
        // Arrange
        val mockUser = mockk<FirebaseUser>()
        coEvery { authRepository.login(any(), any()) } returns Result.success(mockUser)
        coEvery { habfitRepository.checkOnboardingStatus() } returns true
        
        var destination = ""

        // Act
        viewModel.login("test@test.com", "password") { destination = it }
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals("main", destination)
    }

    @Test
    fun `login success should navigate to onboarding if onboarding not completed`() {
        // Arrange
        val mockUser = mockk<FirebaseUser>()
        coEvery { authRepository.login(any(), any()) } returns Result.success(mockUser)
        coEvery { habfitRepository.checkOnboardingStatus() } returns false
        
        var destination = ""

        // Act
        viewModel.login("test@test.com", "password") { destination = it }
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals("onboarding", destination)
    }
}
