package com.habfit.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String = "default_user",
    val name: String = "Habfit Champion",
    val email: String = "user@habfit.app",
    val profileImage: String = "",
    val experienceLevel: String = "Intermediate", // Beginner, Intermediate, Advanced
    val mainGoal: String = "Build Consistency & Fitness",
    val preferredActivities: String = "Strength,Running,HIIT",
    val availableTimeMinutes: Int = 30,
    val points: Int = 350,
    val level: Int = 2,
    val currentStreak: Int = 5,
    val longestStreak: Int = 12,
    val isNotificationsEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
