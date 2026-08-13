package com.habfit.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.habfit.app.domain.model.AssistantTask
import com.habfit.app.domain.model.Badge
import com.habfit.app.domain.model.ChatMessage
import com.habfit.app.domain.model.ContentPost
import com.habfit.app.domain.model.CreatorProfile
import com.habfit.app.domain.model.FitnessGoal
import com.habfit.app.domain.model.Habit
import com.habfit.app.domain.model.HabitLog
import com.habfit.app.domain.model.RewardTransaction
import com.habfit.app.domain.model.User
import com.habfit.app.domain.model.Workout
import com.habfit.app.domain.model.WorkoutSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        Habit::class,
        HabitLog::class,
        FitnessGoal::class,
        Workout::class,
        WorkoutSet::class,
        AssistantTask::class,
        Badge::class,
        RewardTransaction::class,
        ContentPost::class,
        CreatorProfile::class,
        ChatMessage::class
    ],
    version = 3,
    exportSchema = false
)
abstract class HabitDatabase : RoomDatabase() {
    abstract val userDao: UserDao
    abstract val habitDao: HabitDao
    abstract val habitLogDao: HabitLogDao
    abstract val fitnessDao: FitnessDao
    abstract val assistantDao: AssistantDao
    abstract val badgeDao: BadgeDao
    abstract val communityDao: CommunityDao
    abstract val chatDao: ChatDao

    companion object {
        @Volatile
        private var INSTANCE: HabitDatabase? = null

        fun getInstance(context: Context): HabitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabitDatabase::class.java,
                    "habfit_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                seedDatabase(getInstance(context))
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun seedDatabase(database: HabitDatabase) {
            // Seed Default User
            database.userDao.insertUser(
                User(
                    id = "default_user",
                    name = "Alex Vance",
                    email = "alex@habfit.app",
                    experienceLevel = "Intermediate",
                    mainGoal = "Build Consistency & Fitness",
                    preferredActivities = "Strength,Running,HIIT",
                    availableTimeMinutes = 30,
                    points = 450,
                    level = 3,
                    currentStreak = 7,
                    longestStreak = 14
                )
            )

            // Seed Initial Habits
            val defaultHabits = listOf(
                Habit(
                    name = "Drink 2.5L Water",
                    description = "Stay hydrated throughout the day",
                    category = "Health",
                    icon = "Water",
                    target = "2.5L",
                    frequency = "Daily",
                    reminderTime = "08:00 AM",
                    streak = 7,
                    longestStreak = 14,
                    isCompletedToday = true
                ),
                Habit(
                    name = "Morning Stretch & Mobility",
                    description = "10 mins full body mobility routine",
                    category = "Fitness",
                    icon = "Fitness",
                    target = "10 mins",
                    frequency = "Daily",
                    reminderTime = "07:00 AM",
                    streak = 5,
                    longestStreak = 8,
                    isCompletedToday = false
                ),
                Habit(
                    name = "Read 15 Pages",
                    description = "Self-improvement or fitness reading",
                    category = "Mind",
                    icon = "Book",
                    target = "15 pages",
                    frequency = "Daily",
                    reminderTime = "09:00 PM",
                    streak = 3,
                    longestStreak = 6,
                    isCompletedToday = false
                ),
                Habit(
                    name = "Hit 8,000 Daily Steps",
                    description = "Active walking during breaks",
                    category = "Fitness",
                    icon = "Walk",
                    target = "8,000 steps",
                    frequency = "Daily",
                    reminderTime = "06:00 PM",
                    streak = 6,
                    longestStreak = 12,
                    isCompletedToday = true
                )
            )
            database.habitDao.insertHabits(defaultHabits)

            // Seed Initial Daily Missions
            val defaultMissions = listOf(
                AssistantTask(
                    title = "Hydration Goal: 2.5L Water",
                    reason = "Critical for muscle recovery and metabolic rate",
                    category = "Health",
                    difficulty = "Easy",
                    rewardPoints = 20,
                    isCompleted = true,
                    source = "DAILY_MISSION"
                ),
                AssistantTask(
                    title = "8,000 Steps Power Walk",
                    reason = "Maintains baseline cardiovascular fitness",
                    category = "Fitness",
                    difficulty = "Medium",
                    rewardPoints = 30,
                    isCompleted = false,
                    source = "DAILY_MISSION"
                ),
                AssistantTask(
                    title = "Complete 30-min HIIT or Strength",
                    reason = "Boosts metabolic burn and builds endurance",
                    category = "Fitness",
                    difficulty = "Hard",
                    rewardPoints = 50,
                    isCompleted = false,
                    source = "DAILY_MISSION"
                )
            )
            database.assistantDao.insertTasks(defaultMissions)

            // Seed Initial Fitness Goals & Workouts
            val defaultGoals = listOf(
                FitnessGoal(
                    title = "4 Workouts This Week",
                    type = "Workouts",
                    targetValue = 4f,
                    currentValue = 3f,
                    unit = "sessions"
                ),
                FitnessGoal(
                    title = "Burn 2,000 Active Kcal",
                    type = "Calories",
                    targetValue = 2000f,
                    currentValue = 1450f,
                    unit = "kcal"
                ),
                FitnessGoal(
                    title = "Run 15 km Total",
                    type = "Running",
                    targetValue = 15f,
                    currentValue = 9.2f,
                    unit = "km"
                )
            )
            database.fitnessDao.insertGoals(defaultGoals)

            val defaultWorkouts = listOf(
                Workout(
                    title = "Morning High-Intensity Interval Training",
                    type = "HIIT",
                    durationMinutes = 35,
                    caloriesBurned = 320,
                    distanceKm = 0f,
                    notes = "Tabata protocol with jump rope and bodyweight squats"
                ),
                Workout(
                    title = "Sunset Tempo Run",
                    type = "Running",
                    durationMinutes = 28,
                    caloriesBurned = 280,
                    distanceKm = 4.5f,
                    notes = "Great pacing, steady 5:45 min/km"
                )
            )
            database.fitnessDao.insertWorkouts(defaultWorkouts)

            // Seed Badges
            val defaultBadges = listOf(
                Badge(
                    id = "first_step",
                    name = "First Step",
                    description = "Completed your first habit in HABFIT",
                    pointsThreshold = 50,
                    isUnlocked = true,
                    unlockedAt = System.currentTimeMillis() - 86400000L * 5
                ),
                Badge(
                    id = "streak_7",
                    name = "7-Day Consistency Master",
                    description = "Maintained a 7-day habit streak",
                    pointsThreshold = 200,
                    isUnlocked = true,
                    unlockedAt = System.currentTimeMillis() - 86400000L * 2
                ),
                Badge(
                    id = "workout_warrior",
                    name = "Fitness Warrior",
                    description = "Logged 10 active workout sessions",
                    pointsThreshold = 500,
                    isUnlocked = false
                ),
                Badge(
                    id = "streak_30",
                    name = "30-Day Legend",
                    description = "Maintained a 30-day unbroken habit streak",
                    pointsThreshold = 1000,
                    isUnlocked = false
                ),
                Badge(
                    id = "century_club",
                    name = "Century Club",
                    description = "Earned over 1,000 HAB Coins",
                    pointsThreshold = 1000,
                    isUnlocked = false
                )
            )
            database.badgeDao.insertBadges(defaultBadges)

            // Seed Community Creators & Posts
            val defaultCreators = listOf(
                CreatorProfile(
                    creatorId = "coach_marcus",
                    name = "Coach Marcus Thorne",
                    bio = "Elite Strength & Conditioning Coach. Helping athletes build sustainable habits.",
                    specialization = "Strength & Conditioning",
                    followersCount = 4820,
                    isFollowed = true
                ),
                CreatorProfile(
                    creatorId = "elena_fit",
                    name = "Elena Rostova",
                    bio = "Yoga & Mobility Specialist. Daily routines for joint longevity.",
                    specialization = "Mobility & Recovery",
                    followersCount = 3190,
                    isFollowed = false
                )
            )
            database.communityDao.insertCreators(defaultCreators)

            val defaultPosts = listOf(
                ContentPost(
                    creatorId = "coach_marcus",
                    creatorName = "Coach Marcus Thorne",
                    creatorSpecialization = "Strength & Conditioning",
                    title = "3 Key Rules for Habit Consistency",
                    body = "1. Never miss twice in a row.\n2. Scale the intensity down when busy, but never drop the routine.\n3. Celebrate daily micro-wins with HABFIT!",
                    likesCount = 128,
                    isLiked = true,
                    sharesCount = 24,
                    tag = "#HabitMastery",
                    timeAgo = "1 hour ago"
                ),
                ContentPost(
                    creatorId = "elena_fit",
                    creatorName = "Elena Rostova",
                    creatorSpecialization = "Mobility & Recovery",
                    title = "Post-Workout 5-Minute Spine Flow",
                    body = "If you sit at a desk all day after a heavy workout, incorporate Cat-Cow, World's Greatest Stretch, and deep diaphragmatic breathing. Your lower back will thank you!",
                    likesCount = 89,
                    isLiked = false,
                    sharesCount = 15,
                    tag = "#Mobility",
                    timeAgo = "4 hours ago"
                ),
                ContentPost(
                    creatorId = "default_user",
                    creatorName = "Alex Vance",
                    creatorSpecialization = "Habfit Community Member",
                    title = "Hit my 7-day streak today!",
                    body = "Just logged a 35-minute HIIT session and completed all daily missions. Feeling stronger every day!",
                    likesCount = 34,
                    isLiked = true,
                    sharesCount = 4,
                    tag = "#HabfitWin",
                    timeAgo = "6 hours ago"
                )
            )
            database.communityDao.insertPosts(defaultPosts)
        }
    }
}
