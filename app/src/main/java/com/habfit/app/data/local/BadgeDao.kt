package com.habfit.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.habfit.app.domain.model.Badge
import com.habfit.app.domain.model.RewardTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface BadgeDao {
    @Query("SELECT * FROM badges ORDER BY isUnlocked DESC, pointsThreshold ASC")
    fun getAllBadges(): Flow<List<Badge>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadges(badges: List<Badge>)

    @Query("UPDATE badges SET isUnlocked = 1, unlockedAt = :timestamp WHERE id = :badgeId")
    suspend fun unlockBadge(badgeId: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT * FROM reward_transactions ORDER BY timestamp DESC")
    fun getRewardHistory(): Flow<List<RewardTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: RewardTransaction)
}
