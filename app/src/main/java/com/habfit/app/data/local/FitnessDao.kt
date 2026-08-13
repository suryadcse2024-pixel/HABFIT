package com.habfit.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.habfit.app.domain.model.FitnessGoal
import com.habfit.app.domain.model.Workout
import com.habfit.app.domain.model.WorkoutSet
import kotlinx.coroutines.flow.Flow

@Dao
interface FitnessDao {
    // Goals
    @Query("SELECT * FROM fitness_goals ORDER BY id DESC")
    fun getAllGoals(): Flow<List<FitnessGoal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: FitnessGoal): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoals(goals: List<FitnessGoal>)

    @Update
    suspend fun updateGoal(goal: FitnessGoal)

    @Query("DELETE FROM fitness_goals WHERE id = :id")
    suspend fun deleteGoal(id: Int)

    // Workouts
    @Query("SELECT * FROM workouts ORDER BY dateTime DESC")
    fun getAllWorkouts(): Flow<List<Workout>>

    @Query("SELECT * FROM workouts ORDER BY dateTime DESC LIMIT :limit")
    fun getRecentWorkouts(limit: Int = 10): Flow<List<Workout>>

    @Query("SELECT * FROM workouts WHERE dateTime >= :startOfDay")
    fun getTodayWorkouts(startOfDay: Long): Flow<List<Workout>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: Workout): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkouts(workouts: List<Workout>)

    @Query("DELETE FROM workouts WHERE id = :id")
    suspend fun deleteWorkout(id: Int)

    // Sets
    @Query("SELECT * FROM workout_sets WHERE workoutId = :workoutId")
    fun getSetsForWorkout(workoutId: Int): Flow<List<WorkoutSet>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSets(sets: List<WorkoutSet>)
}
