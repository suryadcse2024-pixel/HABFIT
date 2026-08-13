package com.habfit.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assistant_tasks")
data class AssistantTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String = "default_user",
    val title: String,
    val reason: String = "Recommended to maintain daily habit consistency",
    val category: String = "Health", // Health, Fitness, Mind, Routine
    val difficulty: String = "Easy", // Easy, Medium, Hard
    val rewardPoints: Int = 10,
    val isCompleted: Boolean = false,
    val date: String = "", // YYYY-MM-DD
    val source: String = "DAILY_MISSION", // DAILY_MISSION, AI_RECOMMENDATION
    val createdAt: Long = System.currentTimeMillis()
)
