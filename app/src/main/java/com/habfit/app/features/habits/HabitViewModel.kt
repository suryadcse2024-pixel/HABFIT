package com.habfit.app.features.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habfit.app.domain.model.Habit
import com.habfit.app.domain.repository.HabfitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitViewModel @Inject constructor(
    private val repository: HabfitRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory

    private val _allHabits = repository.getAllHabits()

    val habits: StateFlow<List<Habit>> = combine(_allHabits, _selectedCategory) { list, category ->
        if (category == "All") list else list.filter { it.category.equals(category, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalHabitsCount: StateFlow<Int> = _allHabits.combine(_selectedCategory) { list, _ ->
        list.size
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completedTodayCount: StateFlow<Int> = _allHabits.combine(_selectedCategory) { list, _ ->
        list.count { it.isCompletedToday }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun addHabit(
        name: String,
        category: String,
        target: String,
        frequency: String,
        reminderTime: String
    ) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.addHabit(
                name = name,
                category = category,
                target = target,
                frequency = frequency,
                reminderTime = reminderTime
            )
        }
    }

    fun toggleHabitCompletion(habit: Habit) {
        viewModelScope.launch {
            repository.toggleHabitCompletion(habit)
        }
    }

    fun deleteHabit(id: Int) {
        viewModelScope.launch {
            repository.deleteHabit(id)
        }
    }
}
