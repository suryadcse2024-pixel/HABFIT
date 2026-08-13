package com.habfit.app.features.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.habfit.app.domain.model.AssistantTask
import com.habfit.app.domain.model.ChatMessage
import com.habfit.app.ui.components.HabfitButton
import com.habfit.app.ui.components.HabfitCard
import com.habfit.app.ui.components.HabfitSectionTitle
import com.habfit.app.ui.components.HabfitTextField
import com.habfit.app.ui.theme.Background
import com.habfit.app.ui.theme.CardBackground
import com.habfit.app.ui.theme.GoldReward
import com.habfit.app.ui.theme.PrimaryNeonGreen
import com.habfit.app.ui.theme.PrimaryText
import com.habfit.app.ui.theme.PurpleAI
import com.habfit.app.ui.theme.SecondaryText

@Composable
fun AIScreen(
    viewModel: AIViewModel = hiltViewModel()
) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val recommendations by viewModel.aiRecommendations.collectAsState()
    var prompt by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    val promptSuggestions = listOf(
        "Suggest 20-min HIIT",
        "Consistency Strategy",
        "Hydration Advice",
        "Boost Life Score"
    )

    Scaffold(
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = PurpleAI,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "HABIT AI",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurpleAI
                    )
                    Text(
                        text = "Your Personal AI Coach",
                        color = SecondaryText,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                if (chatMessages.isNotEmpty()) {
                    Text(
                        text = "Clear",
                        color = SecondaryText,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .clickable { viewModel.clearHistory() }
                            .padding(8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            // Quick Prompt Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                promptSuggestions.forEach { suggestion ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(CardBackground)
                            .clickable {
                                prompt = suggestion
                                viewModel.askAI(suggestion)
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(text = suggestion, color = PurpleAI, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Chat Messages History
            if (chatMessages.isEmpty()) {
                HabfitCard(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "👋 Hello! I am your HABFIT Coach.\n\nI can suggest daily micro-habits, create personalized HIIT/strength routines, or analyze your consistency trends. What are you working on today?",
                            color = PrimaryText,
                            fontSize = 14.sp,
                            lineHeight = 22.sp
                        )
                    }
                }
            } else {
                chatMessages.forEach { message ->
                    ChatMessageItem(message = message)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PurpleAI, modifier = Modifier.size(24.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Input and Ask AI Button
            HabfitTextField(
                value = prompt,
                onValueChange = { prompt = it },
                label = "Ask your AI coach..."
            )
            Spacer(modifier = Modifier.height(12.dp))
            HabfitButton(
                text = "ASK AI COACH",
                onClick = {
                    if (prompt.isNotBlank()) {
                        viewModel.askAI(prompt)
                        prompt = ""
                    }
                }
            )

            if (recommendations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                HabfitSectionTitle(title = "AI Recommendations")
                recommendations.forEach { task ->
                    AIRecommendationItem(
                        task = task,
                        onAccept = { viewModel.acceptRecommendation(task) },
                        onDismiss = { viewModel.dismissRecommendation(task) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            Spacer(modifier = Modifier.height(80.dp)) // space for bottom nav
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    val isUser = message.role == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(if (isUser) PurpleAI else CardBackground)
                .padding(12.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(
                text = message.content,
                color = if (isUser) Color.White else PrimaryText,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun AIRecommendationItem(
    task: AssistantTask,
    onAccept: () -> Unit,
    onDismiss: () -> Unit
) {
    HabfitCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = task.title, color = PrimaryText, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(text = "+${task.rewardPoints} HAB Coins", color = GoldReward, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = task.reason, color = SecondaryText, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = SecondaryText)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryNeonGreen)
                        .clickable { onAccept() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Add to Today", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
