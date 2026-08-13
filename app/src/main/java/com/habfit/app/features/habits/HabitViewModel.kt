package com.habfit.app.features.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habfit.app.data.local.HabitDao
import com.habfit.app.domain.model.Habit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitViewModel @Inject constructor(
    private val habitDao: HabitDao
) : ViewModel() {

    val habits = habitDao.getAllHabits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addHabit(name: String, category: String, icon: String) {
        viewModelScope.launch {
            habitDao.insertHabit(
                Habit(name = name, category = category, icon = icon)
            )
        }
    }

    fun toggleHabitCompletion(habit: Habit) {
        viewModelScope.launch {
            habitDao.updateHabit(
                habit.copy(isCompletedToday = !habit.isCompletedToday)
            )
        }
    }

    fun deleteHabit(id: Int) {
        viewModelScope.launch {
            habitDao.deleteHabit(id)
        }
    }
}
