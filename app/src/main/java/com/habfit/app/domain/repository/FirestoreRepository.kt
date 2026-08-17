package com.habfit.app.domain.repository

import com.habfit.app.domain.model.ContentPost
import com.habfit.app.domain.model.Habit
import com.habfit.app.domain.model.Workout
import kotlinx.coroutines.flow.Flow

interface FirestoreRepository {
    // Personal Data Sync
    suspend fun syncHabit(userId: String, habit: Habit)
    suspend fun deleteHabit(userId: String, habitId: Int)
    suspend fun syncWorkout(userId: String, workout: Workout)
    suspend fun deleteWorkout(userId: String, workoutId: Int)
    
    // Community Data
    fun getCommunityPosts(): Flow<List<ContentPost>>
    suspend fun createCommunityPost(post: ContentPost)
    suspend fun toggleLikePost(postId: Int, userId: String, isLiked: Boolean)
    suspend fun updateUserProfile(userId: String, name: String, goal: String)

    // Onboarding
    suspend fun isOnboardingCompleted(userId: String): Boolean
    suspend fun saveOnboardingData(userId: String, data: com.habfit.app.domain.model.OnboardingData)
}
