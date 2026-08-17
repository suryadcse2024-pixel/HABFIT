package com.habfit.app.features.fitness

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habfit.app.domain.model.FitnessGoal
import com.habfit.app.domain.model.Gym
import com.habfit.app.domain.model.Workout
import com.habfit.app.domain.repository.HabfitRepository
import com.habfit.app.location.LocationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FitnessViewModel @Inject constructor(
    private val repository: HabfitRepository,
    private val locationService: LocationService
) : ViewModel() {

    val goals: StateFlow<List<FitnessGoal>> = repository.getAllGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val workouts: StateFlow<List<Workout>> = repository.getAllWorkouts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedTab = MutableStateFlow(0) // 0: Workouts & Goals, 1: Nearby Gyms Map
    val selectedTab: StateFlow<Int> = _selectedTab

    private val _nearbyGyms = MutableStateFlow<List<Gym>>(emptyList())
    val nearbyGyms = _nearbyGyms.asStateFlow()

    private val _userLocation = MutableStateFlow<android.location.Location?>(null)
    val userLocation = _userLocation.asStateFlow()

    init {
        refreshLocation()
    }

    fun refreshLocation() {
        viewModelScope.launch {
            // Get initial location
            val initial = locationService.getCurrentLocation()
            if (initial != null) {
                _userLocation.value = initial
                generateMockGyms(initial.latitude, initial.longitude)
            } else {
                generateMockGyms(1.3521, 103.8198)
            }

            // Real-time updates
            locationService.getLocationUpdates().collect { location ->
                _userLocation.value = location
                generateMockGyms(location.latitude, location.longitude)
            }
        }
    }

    private fun generateMockGyms(lat: Double, lng: Double) {
        _nearbyGyms.value = listOf(
            Gym("1", "PowerFit Gym & Studio", lat + 0.002, lng + 0.003, "123 Fitness St", "0.8 km", "Open 24/7", 4.8f),
            Gym("2", "Olympic CrossFit Arena", lat - 0.003, lng - 0.004, "456 Strength Ave", "1.4 km", "06:00 - 22:00", 4.6f),
            Gym("3", "Habfit Wellness Center", lat + 0.005, lng - 0.001, "789 Health Rd", "2.1 km", "08:00 - 20:00", 4.9f)
        )
    }

    fun selectTab(index: Int) {
        _selectedTab.value = index
        if (index == 1) refreshLocation()
    }

    fun logWorkout(
        title: String,
        type: String,
        durationMinutes: Int,
        caloriesBurned: Int,
        distanceKm: Float,
        intensity: String,
        notes: String
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.logWorkout(
                title = title,
                type = type,
                durationMinutes = durationMinutes,
                caloriesBurned = caloriesBurned,
                distanceKm = distanceKm,
                intensity = intensity,
                notes = notes
            )
        }
    }

    fun addGoal(title: String, type: String, targetValue: Float, unit: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addFitnessGoal(title, type, targetValue, unit)
        }
    }

    fun deleteWorkout(id: Int) {
        viewModelScope.launch {
            repository.deleteWorkout(id)
        }
    }

    fun deleteGoal(id: Int) {
        viewModelScope.launch {
            repository.deleteFitnessGoal(id)
        }
    }
}
