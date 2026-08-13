package com.habfit.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.habfit.app.domain.model.AssistantTask
import kotlinx.coroutines.flow.Flow

@Dao
interface AssistantDao {
    @Query("SELECT * FROM assistant_tasks ORDER BY isCompleted ASC, id ASC")
    fun getAllTasks(): Flow<List<AssistantTask>>

    @Query("SELECT * FROM assistant_tasks WHERE source = 'DAILY_MISSION' ORDER BY isCompleted ASC, id ASC")
    fun getDailyMissions(): Flow<List<AssistantTask>>

    @Query("SELECT * FROM assistant_tasks WHERE source = 'AI_RECOMMENDATION' ORDER BY id DESC")
    fun getAIRecommendations(): Flow<List<AssistantTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: AssistantTask): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<AssistantTask>)

    @Update
    suspend fun updateTask(task: AssistantTask)

    @Query("UPDATE assistant_tasks SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun setTaskCompletion(id: Int, isCompleted: Boolean)

    @Query("DELETE FROM assistant_tasks WHERE id = :id")
    suspend fun deleteTask(id: Int)
}
