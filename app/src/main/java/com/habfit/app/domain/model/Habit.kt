package com.habfit.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String = "default_user",
    val name: String,
    val description: String = "",
    val category: String = "Health", // Health, Fitness, Mind, Routine, Focus
    val icon: String = "Water",
    val target: String = "1 time",
    val frequency: String = "Daily", // Daily, Weekdays, Weekends, Custom
    val reminderTime: String = "08:00 AM",
    val streak: Int = 0,
    val longestStreak: Int = 0,
    val isCompletedToday: Boolean = false,
    val isPaused: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "habit_logs")
data class HabitLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val habitId: Int,
    val userId: String = "default_user",
    val date: String, // YYYY-MM-DD
    val isCompleted: Boolean = true,
    val completedAt: Long = System.currentTimeMillis(),
    val note: String = ""
)
