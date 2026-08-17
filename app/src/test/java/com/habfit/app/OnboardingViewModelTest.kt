package com.habfit.app

import com.habfit.app.domain.model.OnboardingData
import com.habfit.app.domain.repository.HabfitRepository
import com.habfit.app.features.onboarding.OnboardingViewModel
import io.mockk.coEvery
import io.mockk.coVerify
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
class OnboardingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: HabfitRepository
    private lateinit var viewModel: OnboardingViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        viewModel = OnboardingViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `toggleGoal should update selectedGoals state`() {
        val goal = "Build Healthy Habits"
        viewModel.toggleGoal(goal)
        assertEquals(setOf(goal), viewModel.selectedGoals.value)

        viewModel.toggleGoal(goal)
        assertEquals(emptySet<String>(), viewModel.selectedGoals.value)
    }

    @Test
    fun `completeOnboarding should call repository with correct data`() {
        // Arrange
        val goals = setOf("Build Healthy Habits")
        val level = "Beginner"
        val activities = setOf("Walking")
        val time = "20 Minutes"
        val reminder = "Evening"

        viewModel.toggleGoal("Build Healthy Habits")
        viewModel.setLevel(level)
        viewModel.toggleActivity("Walking")
        viewModel.setTime(time)
        viewModel.setReminder(reminder)

        var successCalled = false

        // Act
        viewModel.completeOnboarding { successCalled = true }
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify {
            repository.completeOnboarding(
                OnboardingData(
                    goals = goals.toList(),
                    experienceLevel = level,
                    preferredActivities = activities.toList(),
                    availableTime = time,
                    reminderPreference = reminder
                )
            )
        }
        assertEquals(true, successCalled)
    }
}
