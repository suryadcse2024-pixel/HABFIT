package com.habfit.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "badges")
data class Badge(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val icon: String = "EmojiEvents",
    val category: String = "Consistency", // Consistency, Fitness, Habit, Milestone
    val pointsThreshold: Int = 100,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)

@Entity(tableName = "reward_transactions")
data class RewardTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String = "default_user",
    val title: String,
    val points: Int,
    val actionType: String, // HABIT_COMPLETED, WORKOUT_LOGGED, MISSION_COMPLETED, STREAK_BONUS
    val timestamp: Long = System.currentTimeMillis()
)

data class LifeScoreData(
    val score: Int = 85,
    val weeklyChangePercent: Int = 8,
    val habitsCompletionRate: Float = 0.8f,
    val workoutConsistency: Float = 0.9f,
    val streakConsistency: Float = 0.85f
)
