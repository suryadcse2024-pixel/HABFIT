package com.habfit.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fitness_goals")
data class FitnessGoal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String = "default_user",
    val title: String,
    val type: String = "Workouts", // Workouts, Calories, Steps, Running, Weight
    val targetValue: Float,
    val currentValue: Float = 0f,
    val unit: String = "sessions", // sessions, kcal, steps, km, kg
    val startDate: String = "",
    val targetDate: String = "",
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String = "default_user",
    val title: String,
    val type: String = "Strength", // Strength, Running, HIIT, Yoga, Cycling, Walking
    val dateTime: Long = System.currentTimeMillis(),
    val durationMinutes: Int = 30,
    val distanceKm: Float = 0f,
    val caloriesBurned: Int = 250,
    val intensity: String = "Medium", // Low, Medium, High
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "workout_sets")
data class WorkoutSet(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val workoutId: Int,
    val exerciseName: String,
    val sets: Int = 3,
    val reps: Int = 12,
    val weightKg: Float = 0f,
    val durationSeconds: Int = 0
)
