package com.habfit.app.core.di

import android.app.Application
import androidx.room.Room
import com.habfit.app.data.local.HabitDatabase
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
        return Room.databaseBuilder(
            app,
            HabitDatabase::class.java,
            "habfit_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideHabitDao(db: HabitDatabase) = db.habitDao
}
