package com.habfit.app.data.repository

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.habfit.app.data.local.AssistantDao
import com.habfit.app.data.local.BadgeDao
import com.habfit.app.data.local.CommunityDao
import com.habfit.app.data.local.FitnessDao
import com.habfit.app.data.local.HabitDao
import com.habfit.app.data.local.HabitLogDao
import com.habfit.app.data.local.UserDao
import com.habfit.app.domain.model.AssistantTask
import com.habfit.app.domain.model.Badge
import com.habfit.app.domain.model.ContentPost
import com.habfit.app.domain.model.CreatorProfile
import com.habfit.app.domain.model.FitnessGoal
import com.habfit.app.domain.model.Habit
import com.habfit.app.domain.model.HabitLog
import com.habfit.app.domain.model.LifeScoreData
import com.habfit.app.domain.model.OnboardingData
import com.habfit.app.domain.model.RewardTransaction
import com.habfit.app.domain.model.User
import com.habfit.app.domain.model.Workout
import com.habfit.app.domain.repository.AuthRepository
import com.habfit.app.domain.repository.FirestoreRepository
import com.habfit.app.domain.repository.HabfitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabfitRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val habitDao: HabitDao,
    private val habitLogDao: HabitLogDao,
    private val fitnessDao: FitnessDao,
    private val assistantDao: AssistantDao,
    private val badgeDao: BadgeDao,
    private val communityDao: CommunityDao,
    private val authRepository: AuthRepository,
    private val firestoreRepository: FirestoreRepository,
    private val analytics: FirebaseAnalytics
) : HabfitRepository {

    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    override fun getUser(): Flow<User?> {
        val uid = authRepository.currentUser?.uid ?: "default_user"
        return userDao.getUser(uid)
    }

    override suspend fun getUserSync(): User? {
        val uid = authRepository.currentUser?.uid ?: return null
        return userDao.getUserSync(uid)
    }

    override suspend fun saveUserPreferences(
        name: String,
        goal: String,
        level: String,
        activities: String,
        time: Int,
        starterHabits: List<String>
    ) {
        val uid = authRepository.currentUser?.uid ?: "default_user"
        val existing = userDao.getUserSync(uid) ?: User(id = uid)
        val updated = existing.copy(
            name = if (name.isNotBlank()) name else existing.name,
            mainGoal = goal,
            experienceLevel = level,
            preferredActivities = activities,
            availableTimeMinutes = time
        )
        userDao.insertUser(updated)

        // Sync to Firestore if authenticated
        authRepository.currentUser?.uid?.let { currentUid ->
            firestoreRepository.updateUserProfile(currentUid, name, goal)
        }

        // If starter habits were picked in onboarding, insert them
        if (starterHabits.isNotEmpty()) {
            val habitsToInsert = starterHabits.map { habitName ->
                val category = when {
                    habitName.contains("Water", ignoreCase = true) -> "Health"
                    habitName.contains("Stretch", ignoreCase = true) || habitName.contains("Steps", ignoreCase = true) -> "Fitness"
                    habitName.contains("Read", ignoreCase = true) || habitName.contains("Meditation", ignoreCase = true) -> "Mind"
                    else -> "Routine"
                }
                Habit(
                    name = habitName,
                    description = "Personal habit selected during onboarding",
                    category = category,
                    icon = category,
                    frequency = "Daily",
                    target = "Daily",
                    streak = 0,
                    isCompletedToday = false
                )
            }
            habitDao.insertHabits(habitsToInsert)
        }
    }

    override suspend fun updateNotifications(enabled: Boolean) {
        val uid = authRepository.currentUser?.uid ?: "default_user"
        val user = userDao.getUserSync(uid) ?: return
        userDao.updateUser(user.copy(isNotificationsEnabled = enabled))
    }

    override suspend fun logout() {
        authRepository.logout()
        // Log Analytics
        analytics.logEvent("logout", null)
    }

    override suspend fun completeOnboarding(data: OnboardingData) {
        val uid = authRepository.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
        
        // 1. Update Firestore
        firestoreRepository.saveOnboardingData(uid, data)
        
        // 2. Update Room
        val existing = userDao.getUserSync(uid) ?: User(id = uid)
        val updated = existing.copy(
            experienceLevel = data.experienceLevel,
            goals = data.goals.joinToString(","),
            preferredActivities = data.preferredActivities.joinToString(","),
            availableTimeMinutes = when(data.availableTime) {
                "10 Minutes" -> 10
                "20 Minutes" -> 20
                "30 Minutes" -> 30
                else -> 45
            },
            reminderPreference = data.reminderPreference,
            onboardingCompleted = true,
            onboardingCompletedAt = System.currentTimeMillis()
        )
        userDao.insertUser(updated)
        
        // Log Analytics
        analytics.logEvent("onboarding_completed", Bundle().apply {
            putString("experience_level", data.experienceLevel)
            putString("main_goal", data.goals.firstOrNull())
        })
    }

    override suspend fun checkOnboardingStatus(): Boolean {
        val uid = authRepository.currentUser?.uid ?: return false
        
        // Always check Firestore as the source of truth for onboarding status
        val isCompletedRemote = firestoreRepository.isOnboardingCompleted(uid)
        
        // Sync to Room if completed remote but not local
        if (isCompletedRemote) {
            val localUser = userDao.getUserSync(uid)
            if (localUser == null || !localUser.onboardingCompleted) {
                userDao.insertUser((localUser ?: User(id = uid)).copy(onboardingCompleted = true))
            }
        }
        
        return isCompletedRemote
    }

    override fun getAllHabits(): Flow<List<Habit>> = habitDao.getAllHabits()

    override suspend fun addHabit(
        name: String,
        category: String,
        target: String,
        frequency: String,
        reminderTime: String
    ) {
        val habit = Habit(
            name = name,
            category = category,
            icon = category,
            target = target.ifBlank { "Daily" },
            frequency = frequency.ifBlank { "Daily" },
            reminderTime = reminderTime.ifBlank { "08:00 AM" }
        )
        val id = habitDao.insertHabit(habit)
        
        // Sync to Firestore
        authRepository.currentUser?.uid?.let { uid ->
            firestoreRepository.syncHabit(uid, habit.copy(id = id.toInt()))
        }
    }

    override suspend fun toggleHabitCompletion(habit: Habit) {
        val newStatus = !habit.isCompletedToday
        val newStreak = if (newStatus) habit.streak + 1 else (habit.streak - 1).coerceAtLeast(0)
        val longestStreak = maxOf(habit.longestStreak, newStreak)

        val updatedHabit = habit.copy(
            isCompletedToday = newStatus,
            streak = newStreak,
            longestStreak = longestStreak
        )
        habitDao.updateHabit(updatedHabit)

        // Sync to Firestore
        authRepository.currentUser?.uid?.let { uid ->
            firestoreRepository.syncHabit(uid, updatedHabit)
        }

        val today = getCurrentDate()
        if (newStatus) {
            habitLogDao.insertLog(HabitLog(habitId = habit.id, date = today, isCompleted = true))
            
            // Log Analytics Event
            analytics.logEvent("habit_completed", Bundle().apply {
                putString("habit_name", habit.name)
                putString("category", habit.category)
                putInt("streak", newStreak)
            })

            // Reward points
            authRepository.currentUser?.uid?.let { uid ->
                userDao.addPoints(15, uid)
            }
            badgeDao.insertTransaction(
                RewardTransaction(
                    title = "Completed Habit: ${habit.name}",
                    points = 15,
                    actionType = "HABIT_COMPLETED"
                )
            )
            checkBadges()
        } else {
            habitLogDao.deleteLog(habit.id, today)
        }
    }

    override suspend fun deleteHabit(id: Int) {
        habitDao.deleteHabit(id)
        
        // Sync to Firestore
        authRepository.currentUser?.uid?.let { uid ->
            firestoreRepository.deleteHabit(uid, id)
        }
    }

    override suspend fun updateHabit(habit: Habit) {
        habitDao.updateHabit(habit)
    }

    override fun getCompletionStatsForDate(date: String): Flow<Pair<Int, Int>> {
        return combine(
            habitDao.getAllHabits(),
            habitLogDao.getLogsForDate(date)
        ) { habits, logs ->
            Pair(logs.size, habits.size)
        }
    }

    override fun getAllGoals(): Flow<List<FitnessGoal>> = fitnessDao.getAllGoals()

    override suspend fun addFitnessGoal(title: String, type: String, targetValue: Float, unit: String) {
        fitnessDao.insertGoal(
            FitnessGoal(
                title = title,
                type = type,
                targetValue = targetValue,
                unit = unit
            )
        )
    }

    override suspend fun updateGoalProgress(type: String, increment: Float) {
        // This is a simplified implementation. In a real app, you'd fetch active goals for the type.
        // We'll get all goals and update any that match the type (Running, Steps, Calories, etc.)
        val allGoals = fitnessDao.getAllGoalsSync()
        allGoals.forEach { goal ->
            if (goal.type.equals(type, ignoreCase = true) || 
                (type.equals("Steps", ignoreCase = true) && goal.unit.equals("steps", ignoreCase = true)) ||
                (type.equals("Running", ignoreCase = true) && goal.unit.equals("km", ignoreCase = true))
            ) {
                val newValue = goal.currentValue + increment
                fitnessDao.updateGoal(goal.copy(
                    currentValue = newValue,
                    isCompleted = newValue >= goal.targetValue
                ))
            }
        }
    }

    override suspend fun deleteFitnessGoal(id: Int) {
        fitnessDao.deleteGoal(id)
    }

    override fun getAllWorkouts(): Flow<List<Workout>> = fitnessDao.getAllWorkouts()

    override fun getRecentWorkouts(limit: Int): Flow<List<Workout>> = fitnessDao.getRecentWorkouts(limit)

    override suspend fun logWorkout(
        title: String,
        type: String,
        durationMinutes: Int,
        caloriesBurned: Int,
        distanceKm: Float,
        intensity: String,
        notes: String
    ) {
        val workout = Workout(
            title = title,
            type = type,
            durationMinutes = durationMinutes,
            caloriesBurned = caloriesBurned,
            distanceKm = distanceKm,
            intensity = intensity,
            notes = notes
        )
        val id = fitnessDao.insertWorkout(workout)

        // Update goals
        updateGoalProgress("Workouts", 1f)
        if (caloriesBurned > 0) updateGoalProgress("Calories", caloriesBurned.toFloat())
        if (distanceKm > 0) {
            updateGoalProgress("Running", distanceKm)
            updateGoalProgress("Walking", distanceKm)
            updateGoalProgress("Cycling", distanceKm)
        }

        // Sync to Firestore
        authRepository.currentUser?.uid?.let { uid ->
            firestoreRepository.syncWorkout(uid, workout.copy(id = id.toInt()))
        }

        // Reward points for workout
        authRepository.currentUser?.uid?.let { uid ->
            userDao.addPoints(30, uid)
        }
        badgeDao.insertTransaction(
            RewardTransaction(
                title = "Logged Workout: $title",
                points = 30,
                actionType = "WORKOUT_LOGGED"
            )
        )
        checkBadges()
    }

    override suspend fun deleteWorkout(id: Int) {
        fitnessDao.deleteWorkout(id)
        
        // Sync to Firestore
        authRepository.currentUser?.uid?.let { uid ->
            firestoreRepository.deleteWorkout(uid, id)
        }
    }

    override fun getDailyMissions(): Flow<List<AssistantTask>> = assistantDao.getDailyMissions()

    override fun getAIRecommendations(): Flow<List<AssistantTask>> = assistantDao.getAIRecommendations()

    override suspend fun toggleMissionCompletion(task: AssistantTask) {
        val newStatus = !task.isCompleted
        assistantDao.setTaskCompletion(task.id, newStatus)
        if (newStatus) {
            authRepository.currentUser?.uid?.let { uid ->
                userDao.addPoints(task.rewardPoints, uid)
            }
            badgeDao.insertTransaction(
                RewardTransaction(
                    title = "Completed Mission: ${task.title}",
                    points = task.rewardPoints,
                    actionType = "MISSION_COMPLETED"
                )
            )
            checkBadges()
        }
    }

    override suspend fun addAssistantTask(
        title: String,
        reason: String,
        category: String,
        difficulty: String,
        rewardPoints: Int,
        source: String
    ) {
        assistantDao.insertTask(
            AssistantTask(
                title = title,
                reason = reason,
                category = category,
                difficulty = difficulty,
                rewardPoints = rewardPoints,
                source = source
            )
        )
    }

    override suspend fun deleteAssistantTask(id: Int) {
        assistantDao.deleteTask(id)
    }

    override fun getLifeScore(): Flow<LifeScoreData> {
        return combine(
            habitDao.getAllHabits(),
            assistantDao.getDailyMissions(),
            fitnessDao.getAllWorkouts()
        ) { habits, missions, workouts ->
            val habitTotal = habits.size.coerceAtLeast(1)
            val habitsDone = habits.count { it.isCompletedToday }
            val habitRate = habitsDone.toFloat() / habitTotal

            val missionTotal = missions.size.coerceAtLeast(1)
            val missionsDone = missions.count { it.isCompleted }
            val missionRate = missionsDone.toFloat() / missionTotal

            val workoutFactor = if (workouts.isNotEmpty()) 1.0f else 0.5f

            // Weighted Life Score calculation (0 to 100)
            val calculatedScore = ((habitRate * 50) + (missionRate * 30) + (workoutFactor * 20)).toInt().coerceIn(30, 100)

            LifeScoreData(
                score = calculatedScore,
                weeklyChangePercent = if (calculatedScore >= 75) 8 else 4,
                habitsCompletionRate = habitRate,
                workoutConsistency = workoutFactor,
                streakConsistency = 0.85f
            )
        }
    }

    override fun getAllBadges(): Flow<List<Badge>> = badgeDao.getAllBadges()

    private suspend fun checkBadges() {
        val uid = authRepository.currentUser?.uid ?: "default_user"
        val user = userDao.getUserSync(uid) ?: return
        if (user.points >= 200) {
            badgeDao.unlockBadge("streak_7")
        }
        if (user.points >= 500) {
            badgeDao.unlockBadge("workout_warrior")
        }
        if (user.points >= 1000) {
            badgeDao.unlockBadge("century_club")
            badgeDao.unlockBadge("streak_30")
        }
        // Update user level dynamically based on points
        val newLevel = when {
            user.points >= 1000 -> 5
            user.points >= 600 -> 4
            user.points >= 350 -> 3
            user.points >= 150 -> 2
            else -> 1
        }
        if (newLevel != user.level) {
            userDao.updateUser(user.copy(level = newLevel))
        }
    }

    override fun getAllPosts(): Flow<List<ContentPost>> = communityDao.getAllPosts()

    override suspend fun createPost(title: String, body: String, tag: String) {
        val uid = authRepository.currentUser?.uid ?: "default_user"
        val user = userDao.getUserSync(uid)
        val authorName = user?.name ?: "Habfit Member"
        val userId = authRepository.currentUser?.uid ?: "user_me"
        
        val post = ContentPost(
            creatorId = userId,
            creatorName = authorName,
            creatorSpecialization = "Habfit Consistency Member",
            title = title,
            body = body,
            likesCount = 0,
            isLiked = false,
            sharesCount = 0,
            tag = if (tag.startsWith("#")) tag else "#$tag",
            timeAgo = "Just now"
        )
        
        val id = communityDao.insertPost(post)
        
        // Sync to Firestore
        firestoreRepository.createCommunityPost(post.copy(id = id.toInt()))
        
        // Log Analytics
        analytics.logEvent("community_post_created", Bundle().apply {
            putString("tag", tag)
        })
    }

    override suspend fun toggleLike(post: ContentPost) {
        val newLiked = !post.isLiked
        val newCount = if (newLiked) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(0)
        communityDao.updateLikeStatus(post.id, newLiked, newCount)
        
        // Sync to Firestore
        authRepository.currentUser?.uid?.let { uid ->
            firestoreRepository.toggleLikePost(post.id, uid, newLiked)
        }
    }

    override fun getAllCreators(): Flow<List<CreatorProfile>> = communityDao.getAllCreators()

    override suspend fun toggleFollowCreator(creator: CreatorProfile) {
        val newFollow = !creator.isFollowed
        val newCount = if (newFollow) creator.followersCount + 1 else (creator.followersCount - 1).coerceAtLeast(0)
        communityDao.updateFollowStatus(creator.creatorId, newFollow, newCount)
    }
}
