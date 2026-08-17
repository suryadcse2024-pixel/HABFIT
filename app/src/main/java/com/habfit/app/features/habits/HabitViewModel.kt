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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class DailyStats(
    val date: String,
    val dayLabel: String,
    val completed: Int,
    val total: Int,
    val isSelected: Boolean
)

data class HabitUiModel(
    val habit: Habit,
    val isCompleted: Boolean
)

@HiltViewModel
class HabitViewModel @Inject constructor(
    private val repository: HabfitRepository
) : ViewModel() {

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val today = sdf.format(Date())

    private val _selectedDate = MutableStateFlow(today)
    val selectedDate: StateFlow<String> = _selectedDate

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory

    private val _allHabits = repository.getAllHabits()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val habits: StateFlow<List<HabitUiModel>> = _selectedDate.flatMapLatest { date ->
        combine(_allHabits, _selectedCategory) { list, category ->
            val filtered = if (category == "All") list else list.filter { it.category.equals(category, ignoreCase = true) }
            // For now, if date is today, use isCompletedToday. 
            // In a real app, we'd check habit logs for that date.
            filtered.map { HabitUiModel(it, if (date == today) it.isCompletedToday else false) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @Suppress("UNCHECKED_CAST")
    val weeklyStats: StateFlow<List<DailyStats>> = combine(
        getWeekDates().map { date ->
            repository.getCompletionStatsForDate(date).map { pair ->
                val calendar = Calendar.getInstance()
                calendar.time = sdf.parse(date) ?: Date()
                val dayLabel = SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time).uppercase()
                Triple(date, dayLabel, pair)
            }
        }
    ) { arrayOfAny ->
        val selected = _selectedDate.value
        arrayOfAny.map { it as Triple<String, String, Pair<Int, Int>> }.map { triple ->
            DailyStats(
                date = triple.first,
                dayLabel = triple.second,
                completed = triple.third.first,
                total = triple.third.second,
                isSelected = triple.first == selected
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalHabitsCount: StateFlow<Int> = _allHabits.map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completedTodayCount: StateFlow<Int> = repository.getCompletionStatsForDate(today)
        .map { it.first }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private fun getWeekDates(): List<String> {
        val calendar = Calendar.getInstance()
        val dates = mutableListOf<String>()
        
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        
        val tempCal = calendar.clone() as Calendar
        for (i in 0 until 7) {
            dates.add(sdf.format(tempCal.time))
            tempCal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return dates
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun selectDate(date: String) {
        _selectedDate.value = date
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
