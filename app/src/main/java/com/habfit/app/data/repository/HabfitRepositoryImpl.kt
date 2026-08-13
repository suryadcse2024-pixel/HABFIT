package com.habfit.app.data.repository

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
import com.habfit.app.domain.model.RewardTransaction
import com.habfit.app.domain.model.User
import com.habfit.app.domain.model.Workout
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
    private val communityDao: CommunityDao
) : HabfitRepository {

    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    override fun getUser(): Flow<User?> = userDao.getUser()

    override suspend fun getUserSync(): User? = userDao.getUserSync()

    override suspend fun saveUserPreferences(
        name: String,
        goal: String,
        level: String,
        activities: String,
        time: Int,
        starterHabits: List<String>
    ) {
        val existing = userDao.getUserSync() ?: User()
        val updated = existing.copy(
            name = if (name.isNotBlank()) name else existing.name,
            mainGoal = goal,
            experienceLevel = level,
            preferredActivities = activities,
            availableTimeMinutes = time
        )
        userDao.insertUser(updated)

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
        val user = userDao.getUserSync() ?: return
        userDao.updateUser(user.copy(isNotificationsEnabled = enabled))
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
        habitDao.insertHabit(habit)
    }

    override suspend fun toggleHabitCompletion(habit: Habit) {
        val newStatus = !habit.isCompletedToday
        val newStreak = if (newStatus) habit.streak + 1 else (habit.streak - 1).coerceAtLeast(0)
        val longestStreak = maxOf(habit.longestStreak, newStreak)

        habitDao.updateHabit(
            habit.copy(
                isCompletedToday = newStatus,
                streak = newStreak,
                longestStreak = longestStreak
            )
        )

        val today = getCurrentDate()
        if (newStatus) {
            habitLogDao.insertLog(HabitLog(habitId = habit.id, date = today, isCompleted = true))
            // Reward points
            userDao.addPoints(15)
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
    }

    override suspend fun updateHabit(habit: Habit) {
        habitDao.updateHabit(habit)
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
        fitnessDao.insertWorkout(workout)

        // Reward points for workout
        userDao.addPoints(30)
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
    }

    override fun getDailyMissions(): Flow<List<AssistantTask>> = assistantDao.getDailyMissions()

    override fun getAIRecommendations(): Flow<List<AssistantTask>> = assistantDao.getAIRecommendations()

    override suspend fun toggleMissionCompletion(task: AssistantTask) {
        val newStatus = !task.isCompleted
        assistantDao.setTaskCompletion(task.id, newStatus)
        if (newStatus) {
            userDao.addPoints(task.rewardPoints)
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
        val user = userDao.getUserSync() ?: return
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
        val user = userDao.getUserSync()
        val authorName = user?.name ?: "Habfit Member"
        communityDao.insertPost(
            ContentPost(
                creatorId = "user_me",
                creatorName = authorName,
                creatorSpecialization = "Habfit Consistency Member",
                title = title,
                body = body,
                likesCount = 1,
                isLiked = true,
                sharesCount = 0,
                tag = if (tag.startsWith("#")) tag else "#$tag",
                timeAgo = "Just now"
            )
        )
    }

    override suspend fun toggleLike(post: ContentPost) {
        val newLiked = !post.isLiked
        val newCount = if (newLiked) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(0)
        communityDao.updateLikeStatus(post.id, newLiked, newCount)
    }

    override fun getAllCreators(): Flow<List<CreatorProfile>> = communityDao.getAllCreators()

    override suspend fun toggleFollowCreator(creator: CreatorProfile) {
        val newFollow = !creator.isFollowed
        val newCount = if (newFollow) creator.followersCount + 1 else (creator.followersCount - 1).coerceAtLeast(0)
        communityDao.updateFollowStatus(creator.creatorId, newFollow, newCount)
    }
}
