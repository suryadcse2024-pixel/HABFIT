package com.habfit.app.domain.repository

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIRepository @Inject constructor() {

    private val apiKey = "YOUR_GEMINI_API_KEY_HERE"
    
    private val generativeModel by lazy {
        try {
            GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = apiKey
            )
        } catch (e: Exception) {
            null
        }
    }

    fun getChatResponse(prompt: String): Flow<String> = flow {
        if (apiKey != "YOUR_GEMINI_API_KEY_HERE" && generativeModel != null) {
            try {
                val response = generativeModel?.generateContent(prompt)
                emit(response?.text ?: getFallbackCoachResponse(prompt))
                return@flow
            } catch (e: Exception) {
                // Fallback to local intelligent assistant logic
            }
        }
        emit(getFallbackCoachResponse(prompt))
    }

    private fun getFallbackCoachResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("hiit") || lower.contains("workout") -> {
                "🔥 **20-Minute High-Energy HIIT Routine:**\n\n" +
                "1. **Jumping Jacks** — 45s work / 15s rest\n" +
                "2. **Bodyweight Squats** — 45s work / 15s rest\n" +
                "3. **Mountain Climbers** — 45s work / 15s rest\n" +
                "4. **High Knees** — 45s work / 15s rest\n" +
                "5. **Plank Hold** — 45s work / 15s rest\n\n" +
                "🔁 Repeat for 3 rounds. Stay hydrated and track this workout in HABFIT!"
            }
            lower.contains("consistency") || lower.contains("streak") || lower.contains("habit") -> {
                "🎯 **Habit Consistency Tip:**\n\n" +
                "• **The 2-Minute Rule**: Start smaller than you think is necessary so resistance is near zero.\n" +
                "• **Never miss twice in a row**: If life gets busy, complete at least 25% of the habit to keep the neural loop alive.\n" +
                "• **Habit Stacking**: Pair your new habit immediately after an existing anchor routine (e.g. stretch right after morning coffee)."
            }
            lower.contains("water") || lower.contains("hydrate") -> {
                "💧 **Hydration Strategy:**\n\n" +
                "Aim for 2.5L to 3L daily. Keep a full water bottle by your workspace and take 3 sips before each transition in your day. Proper hydration boosts energy by up to 20%!"
            }
            lower.contains("score") || lower.contains("life score") -> {
                "📊 **Boosting Your Life Score:**\n\n" +
                "Your HABFIT Life Score reflects your daily balance:\n" +
                "• 50% from Habit completion\n" +
                "• 30% from Daily Mission completion\n" +
                "• 20% from Logged Workouts & active movement\n\n" +
                "Complete today's pending missions to push your score past 80%!"
            }
            else -> {
                "💡 **Coach Advice:**\n\n" +
                "Consistency always beats intensity in the long run. Focus on completing your daily missions and checking off your core habits today. What specific goal would you like to tackle next?"
            }
        }
    }

    suspend fun generateDailyPlan(userGoal: String, fitnessLevel: String): String {
        return "Recommended Plan for $fitnessLevel ($userGoal):\n1. 20-min active movement session\n2. 2.5L daily hydration target\n3. 10-min evening mobility stretch"
    }
}
