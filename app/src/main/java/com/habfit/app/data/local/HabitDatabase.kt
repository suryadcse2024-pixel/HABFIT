package com.habfit.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.habfit.app.domain.model.Habit

@Database(entities = [Habit::class], version = 1)
abstract class HabitDatabase : RoomDatabase() {
    abstract val habitDao: HabitDao
}
