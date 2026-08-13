package com.habfit.app.domain.repository

import com.habfit.app.BuildConfig
import com.habfit.app.data.local.ChatDao
import com.habfit.app.data.local.UserDao
import com.habfit.app.data.remote.GroqApiService
import com.habfit.app.data.remote.model.GroqMessage
import com.habfit.app.data.remote.model.GroqRequest
import com.habfit.app.domain.model.ChatMessage
import com.habfit.app.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroqRepository @Inject constructor(
    private val apiService: GroqApiService,
    private val chatDao: ChatDao,
    private val userDao: UserDao
) {

    private val apiKey = BuildConfig.GROQ_API_KEY
    private val model = "llama3-8b-8192"

    fun getChatResponse(prompt: String): Flow<String> = flow {
        if (apiKey.isEmpty()) {
            emit("Please configure your Groq API key in local.properties")
            return@flow
        }

        // 1. Save user message to DB
        val userMessage = ChatMessage(role = "user", content = prompt)
        chatDao.insertMessage(userMessage)

        try {
            // 2. Fetch User Context
            val user = userDao.getUser().firstOrNull() ?: User()
            
            // 3. Fetch History (last 10 messages)
            val history = chatDao.getRecentMessages(10).reversed()
            
            // 4. Build System Prompt
            val systemPrompt = buildSystemPrompt(user)
            
            // 5. Construct Groq Messages
            val groqMessages = mutableListOf<GroqMessage>()
            groqMessages.add(GroqMessage(role = "system", content = systemPrompt))
            
            // Add history
            history.forEach { msg ->
                groqMessages.add(GroqMessage(role = msg.role, content = msg.content))
            }
            
            // Current message is already at the end of history if it was just inserted, 
            // but history was fetched before insertion or we handle it carefully.
            // Actually getRecentMessages(10) includes the message we just inserted.
            
            val request = GroqRequest(
                model = model,
                messages = groqMessages
            )

            // 6. Call API
            val response = apiService.getChatCompletion("Bearer $apiKey", request)
            val aiContent = response.choices.firstOrNull()?.message?.content ?: "Sorry, I couldn't generate a response."
            
            // 7. Save AI response to DB
            chatDao.insertMessage(ChatMessage(role = "assistant", content = aiContent))
            
            emit(aiContent)
            
        } catch (e: Exception) {
            val errorMessage = "Error: ${e.localizedMessage ?: "Unknown error occurred"}"
            emit(errorMessage)
        }
    }

    private fun buildSystemPrompt(user: User): String {
        return """
            You are a professional fitness and habit coach for the Habfit app.
            Your goal is to provide personalized, encouraging, and science-based advice.
            
            User Profile:
            - Name: ${user.name}
            - Goal: ${user.mainGoal}
            - Level: ${user.experienceLevel}
            - Activities: ${user.preferredActivities}
            - Current Streak: ${user.currentStreak} days
            - Points: ${user.points}
            
            Guidelines:
            - Be concise but highly supportive.
            - Address the user by their name (${user.name}) occasionally.
            - Relate your advice to their goal: ${user.mainGoal}.
            - If they ask for workouts, suggest something suitable for an ${user.experienceLevel} level.
            - Encourage them to maintain their ${user.currentStreak}-day streak!
        """.trimIndent()
    }
}
