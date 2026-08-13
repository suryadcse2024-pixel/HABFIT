package com.habfit.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.habfit.app.domain.model.ContentPost
import com.habfit.app.domain.model.CreatorProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface CommunityDao {
    @Query("SELECT * FROM community_posts ORDER BY createdAt DESC")
    fun getAllPosts(): Flow<List<ContentPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: ContentPost): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<ContentPost>)

    @Update
    suspend fun updatePost(post: ContentPost)

    @Query("UPDATE community_posts SET isLiked = :isLiked, likesCount = :newLikesCount WHERE id = :id")
    suspend fun updateLikeStatus(id: Int, isLiked: Boolean, newLikesCount: Int)

    @Query("SELECT * FROM creator_profiles ORDER BY followersCount DESC")
    fun getAllCreators(): Flow<List<CreatorProfile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreators(creators: List<CreatorProfile>)

    @Query("UPDATE creator_profiles SET isFollowed = :isFollowed, followersCount = :newFollowersCount WHERE creatorId = :creatorId")
    suspend fun updateFollowStatus(creatorId: String, isFollowed: Boolean, newFollowersCount: Int)
}
