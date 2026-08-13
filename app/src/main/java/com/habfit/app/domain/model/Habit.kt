package com.habfit.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String,
    val icon: String,
    val target: Int = 1,
    val frequency: String = "Daily",
    val streak: Int = 0,
    val isCompletedToday: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
