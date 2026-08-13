package com.habfit.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.habfit.app.domain.model.ContentPost
import com.habfit.app.domain.model.Habit
import com.habfit.app.domain.model.Workout
import com.habfit.app.domain.repository.FirestoreRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FirestoreRepository {

    override suspend fun syncHabit(userId: String, habit: Habit) {
        val habitMap = mapOf(
            "id" to habit.id,
            "name" to habit.name,
            "description" to habit.description,
            "category" to habit.category,
            "target" to habit.target,
            "frequency" to habit.frequency,
            "streak" to habit.streak,
            "isCompletedToday" to habit.isCompletedToday,
            "updatedAt" to System.currentTimeMillis()
        )
        firestore.collection("users").document(userId)
            .collection("habits").document(habit.id.toString())
            .set(habitMap).await()
    }

    override suspend fun deleteHabit(userId: String, habitId: Int) {
        firestore.collection("users").document(userId)
            .collection("habits").document(habitId.toString())
            .delete().await()
    }

    override suspend fun syncWorkout(userId: String, workout: Workout) {
        val workoutMap = mapOf(
            "id" to workout.id,
            "title" to workout.title,
            "type" to workout.type,
            "duration" to workout.durationMinutes,
            "calories" to workout.caloriesBurned,
            "distance" to workout.distanceKm,
            "timestamp" to workout.dateTime
        )
        firestore.collection("users").document(userId)
            .collection("workouts").document(workout.id.toString())
            .set(workoutMap).await()
    }

    override fun getCommunityPosts(): Flow<List<ContentPost>> = callbackFlow {
        val subscription = firestore.collection("posts")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val posts = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ContentPost::class.java)
                } ?: emptyList()
                trySend(posts)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun createCommunityPost(post: ContentPost) {
        firestore.collection("posts").add(post).await()
    }

    override suspend fun toggleLikePost(postId: Int, userId: String, isLiked: Boolean) {
        // Simple implementation: update likes count in Firestore
        // In a real app, you'd have a subcollection of likes
        val docRef = firestore.collection("posts").document(postId.toString())
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val currentLikes = snapshot.getLong("likesCount") ?: 0
            val newLikes = if (isLiked) currentLikes + 1 else (currentLikes - 1).coerceAtLeast(0)
            transaction.update(docRef, "likesCount", newLikes)
        }.await()
    }
}
