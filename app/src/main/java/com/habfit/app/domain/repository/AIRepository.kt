package com.habfit.app.domain.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIRepository @Inject constructor() {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = "YOUR_GEMINI_API_KEY_HERE"
    )

    fun getChatResponse(prompt: String): Flow<String> = flow {
        val response = generativeModel.generateContent(prompt)
        emit(response.text ?: "I couldn't process that.")
    }

    suspend fun generateDailyPlan(userGoal: String, fitnessLevel: String): String {
        val prompt = "Generate a daily fitness and habit plan for a $fitnessLevel user with a goal of $userGoal. Keep it concise."
        val response = generativeModel.generateContent(prompt)
        return response.text ?: "No plan generated."
    }
}
