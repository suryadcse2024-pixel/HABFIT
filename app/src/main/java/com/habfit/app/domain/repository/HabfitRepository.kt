package com.habfit.app.domain.repository

import com.habfit.app.domain.model.AssistantTask
import com.habfit.app.domain.model.Badge
import com.habfit.app.domain.model.ContentPost
import com.habfit.app.domain.model.CreatorProfile
import com.habfit.app.domain.model.FitnessGoal
import com.habfit.app.domain.model.Habit
import com.habfit.app.domain.model.LifeScoreData
import com.habfit.app.domain.model.User
import com.habfit.app.domain.model.Workout
import kotlinx.coroutines.flow.Flow

interface HabfitRepository {
    // User & Profile
    fun getUser(): Flow<User?>
    suspend fun getUserSync(): User?
    suspend fun saveUserPreferences(
        name: String,
        goal: String,
        level: String,
        activities: String,
        time: Int,
        starterHabits: List<String>
    )
    suspend fun updateNotifications(enabled: Boolean)

    // Habits
    fun getAllHabits(): Flow<List<Habit>>
    suspend fun addHabit(
        name: String,
        category: String,
        target: String,
        frequency: String,
        reminderTime: String
    )
    suspend fun toggleHabitCompletion(habit: Habit)
    suspend fun deleteHabit(id: Int)
    suspend fun updateHabit(habit: Habit)

    // Fitness & Workouts
    fun getAllGoals(): Flow<List<FitnessGoal>>
    suspend fun addFitnessGoal(title: String, type: String, targetValue: Float, unit: String)
    suspend fun deleteFitnessGoal(id: Int)
    fun getAllWorkouts(): Flow<List<Workout>>
    fun getRecentWorkouts(limit: Int = 10): Flow<List<Workout>>
    suspend fun logWorkout(
        title: String,
        type: String,
        durationMinutes: Int,
        caloriesBurned: Int,
        distanceKm: Float,
        intensity: String,
        notes: String
    )
    suspend fun deleteWorkout(id: Int)

    // Daily Missions & AI Recommendations
    fun getDailyMissions(): Flow<List<AssistantTask>>
    fun getAIRecommendations(): Flow<List<AssistantTask>>
    suspend fun toggleMissionCompletion(task: AssistantTask)
    suspend fun addAssistantTask(
        title: String,
        reason: String,
        category: String,
        difficulty: String,
        rewardPoints: Int,
        source: String
    )
    suspend fun deleteAssistantTask(id: Int)

    // Gamification & Life Score
    fun getLifeScore(): Flow<LifeScoreData>
    fun getAllBadges(): Flow<List<Badge>>

    // Community
    fun getAllPosts(): Flow<List<ContentPost>>
    suspend fun createPost(title: String, body: String, tag: String)
    suspend fun toggleLike(post: ContentPost)
    fun getAllCreators(): Flow<List<CreatorProfile>>
    suspend fun toggleFollowCreator(creator: CreatorProfile)
}
