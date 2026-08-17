package com.habfit.app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "community_posts")
data class ContentPost(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val creatorId: String = "coach_sarah",
    val creatorName: String = "Sarah Jenkins",
    val creatorSpecialization: String = "Certified HIIT & Strength Coach",
    val creatorAvatarUrl: String = "",
    val title: String = "",
    val body: String = "",
    val imageUrl: String = "",
    val likesCount: Int = 42,
    val isLiked: Boolean = false,
    val sharesCount: Int = 8,
    val tag: String = "#HIIT",
    val timeAgo: String = "2 hours ago",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "creator_profiles")
data class CreatorProfile(
    @PrimaryKey val creatorId: String,
    val name: String,
    val bio: String,
    val specialization: String,
    val followersCount: Int = 1250,
    val isFollowed: Boolean = false,
    val avatarUrl: String = ""
)
