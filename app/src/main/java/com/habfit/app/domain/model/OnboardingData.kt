package com.habfit.app.domain.model

data class OnboardingData(
    val goals: List<String>,
    val experienceLevel: String,
    val preferredActivities: List<String>,
    val availableTime: String,
    val reminderPreference: String
)
