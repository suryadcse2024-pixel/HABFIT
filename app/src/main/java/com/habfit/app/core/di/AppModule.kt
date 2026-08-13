package com.habfit.app.core.di

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import com.habfit.app.data.local.AssistantDao
import com.habfit.app.data.local.BadgeDao
import com.habfit.app.data.local.ChatDao
import com.habfit.app.data.local.CommunityDao
import com.habfit.app.data.local.FitnessDao
import com.habfit.app.data.local.HabitDao
import com.habfit.app.data.local.HabitDatabase
import com.habfit.app.data.local.HabitLogDao
import com.habfit.app.data.local.UserDao
import com.habfit.app.data.repository.HabfitRepositoryImpl
import com.habfit.app.domain.repository.AIRepository
import com.habfit.app.domain.repository.AuthRepository
import com.habfit.app.domain.repository.FirestoreRepository
import com.habfit.app.domain.repository.HabfitRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHabitDatabase(app: Application): HabitDatabase {
        return HabitDatabase.getInstance(app)
    }

    @Provides
    @Singleton
    fun provideUserDao(db: HabitDatabase): UserDao = db.userDao

    @Provides
    @Singleton
    fun provideHabitDao(db: HabitDatabase): HabitDao = db.habitDao

    @Provides
    @Singleton
    fun provideHabitLogDao(db: HabitDatabase): HabitLogDao = db.habitLogDao

    @Provides
    @Singleton
    fun provideFitnessDao(db: HabitDatabase): FitnessDao = db.fitnessDao

    @Provides
    @Singleton
    fun provideAssistantDao(db: HabitDatabase): AssistantDao = db.assistantDao

    @Provides
    @Singleton
    fun provideBadgeDao(db: HabitDatabase): BadgeDao = db.badgeDao

    @Provides
    @Singleton
    fun provideCommunityDao(db: HabitDatabase): CommunityDao = db.communityDao

    @Provides
    @Singleton
    fun provideChatDao(db: HabitDatabase): ChatDao = db.chatDao

    @Provides
    @Singleton
    fun provideHabfitRepository(
        userDao: UserDao,
        habitDao: HabitDao,
        habitLogDao: HabitLogDao,
        fitnessDao: FitnessDao,
        assistantDao: AssistantDao,
        badgeDao: BadgeDao,
        communityDao: CommunityDao,
        authRepository: AuthRepository,
        firestoreRepository: FirestoreRepository,
        analytics: FirebaseAnalytics
    ): HabfitRepository {
        return HabfitRepositoryImpl(
            userDao,
            habitDao,
            habitLogDao,
            fitnessDao,
            assistantDao,
            badgeDao,
            communityDao,
            authRepository,
            firestoreRepository,
            analytics
        )
    }
}
