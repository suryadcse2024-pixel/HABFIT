package com.habfit.app.features.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.habfit.app.ui.components.HabfitButton
import com.habfit.app.ui.theme.Background
import com.habfit.app.ui.theme.CardBackground
import com.habfit.app.ui.theme.HabfitTheme
import com.habfit.app.ui.theme.PrimaryNeonGreen
import com.habfit.app.ui.theme.PrimaryText
import com.habfit.app.ui.theme.SecondaryText
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { 5 })
    val scope = rememberCoroutineScope()
    val isLoading by viewModel.isLoading.collectAsState()

    val selectedGoals by viewModel.selectedGoals.collectAsState()
    val selectedLevel by viewModel.selectedLevel.collectAsState()
    val selectedActivities by viewModel.selectedActivities.collectAsState()
    val selectedTime by viewModel.selectedTime.collectAsState()
    val selectedReminder by viewModel.selectedReminder.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val isStepComplete = when (pagerState.currentPage) {
        0 -> selectedGoals.isNotEmpty()
        1 -> selectedLevel != null
        2 -> selectedActivities.isNotEmpty()
        3 -> selectedTime != null
        4 -> selectedReminder != null
        else -> false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with Back Button and Progress
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (pagerState.currentPage > 0) {
                    IconButton(onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryText)
                    }
                } else {
                    Spacer(modifier = Modifier.width(48.dp))
                }

                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Step ${pagerState.currentPage + 1} of 5",
                        color = SecondaryText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = (pagerState.currentPage + 1) / 5f,
                        modifier = Modifier
                            .width(120.dp)
                            .height(6.dp)
                            .clip(CircleShape),
                        color = PrimaryNeonGreen,
                        trackColor = CardBackground
                    )
                }
                Spacer(modifier = Modifier.width(48.dp))
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                userScrollEnabled = false // Force using buttons for validation
            ) { page ->
                OnboardingPageContent(
                    pageIndex = page,
                    selectedGoals = selectedGoals,
                    selectedLevel = selectedLevel,
                    selectedActivities = selectedActivities,
                    selectedTime = selectedTime,
                    selectedReminder = selectedReminder,
                    onGoalToggled = { viewModel.toggleGoal(it) },
                    onLevelSelected = { viewModel.setLevel(it) },
                    onActivityToggled = { viewModel.toggleActivity(it) },
                    onTimeSelected = { viewModel.setTime(it) },
                    onReminderSelected = { viewModel.setReminder(it) }
                )
            }

            // Bottom Navigation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PrimaryNeonGreen
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        errorMessage?.let {
                            Text(
                                text = it,
                                color = com.habfit.app.ui.theme.ErrorColor,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        HabfitButton(
                            text = if (pagerState.currentPage == 4) "FINISH" else "CONTINUE",
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                if (isStepComplete) {
                                    if (pagerState.currentPage < 4) {
                                        scope.launch {
                                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                        }
                                    } else {
                                        viewModel.completeOnboarding(onFinished)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(
    pageIndex: Int,
    selectedGoals: Set<String>,
    selectedLevel: String?,
    selectedActivities: Set<String>,
    selectedTime: String?,
    selectedReminder: String?,
    onGoalToggled: (String) -> Unit,
    onLevelSelected: (String) -> Unit,
    onActivityToggled: (String) -> Unit,
    onTimeSelected: (String) -> Unit,
    onReminderSelected: (String) -> Unit
) {
    val title = when (pageIndex) {
        0 -> "What are your goals?"
        1 -> "What is your experience level?"
        2 -> "What activities do you enjoy?"
        3 -> "How much time do you have daily?"
        4 -> "When should we remind you?"
        else -> ""
    }

    val subtitle = when (pageIndex) {
        0 -> "Select one or multiple goals to personalize your journey"
        1 -> "Help us tailor your workouts and habits"
        2 -> "Choose the activities you'd like to include"
        3 -> "Consistency is key, choose a sustainable time"
        4 -> "We'll help you stay on track with gentle reminders"
        else -> ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = title,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryText,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = subtitle,
            fontSize = 14.sp,
            color = SecondaryText,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(40.dp))

        when (pageIndex) {
            0 -> {
                val goals = listOf("Build Healthy Habits", "Improve Fitness", "Lose Weight", "Build Muscle", "Stay Active")
                goals.forEach { goal ->
                    OnboardingItemCard(
                        text = goal,
                        isSelected = selectedGoals.contains(goal),
                        isMultiSelect = true,
                        onClick = { onGoalToggled(goal) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            1 -> {
                val levels = listOf("Beginner", "Intermediate", "Advanced")
                levels.forEach { level ->
                    OnboardingItemCard(
                        text = level,
                        isSelected = selectedLevel == level,
                        onClick = { onLevelSelected(level) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            2 -> {
                val activities = listOf("Walking", "Running", "Gym", "Home Workout", "Yoga", "Cycling")
                activities.forEach { activity ->
                    OnboardingItemCard(
                        text = activity,
                        isSelected = selectedActivities.contains(activity),
                        isMultiSelect = true,
                        onClick = { onActivityToggled(activity) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            3 -> {
                val times = listOf("10 Minutes", "20 Minutes", "30 Minutes", "45+ Minutes")
                times.forEach { time ->
                    OnboardingItemCard(
                        text = time,
                        isSelected = selectedTime == time,
                        onClick = { onTimeSelected(time) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            4 -> {
                val reminders = listOf("Morning", "Afternoon", "Evening", "No Reminders")
                reminders.forEach { reminder ->
                    OnboardingItemCard(
                        text = reminder,
                        isSelected = selectedReminder == reminder,
                        onClick = { onReminderSelected(reminder) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun OnboardingItemCard(
    text: String,
    isSelected: Boolean,
    isMultiSelect: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryNeonGreen.copy(alpha = 0.1f) else CardBackground
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) PrimaryNeonGreen else Color.White.copy(alpha = 0.05f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                color = if (isSelected) PrimaryText else SecondaryText,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
            
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(if (isMultiSelect) RoundedCornerShape(6.dp) else CircleShape)
                    .background(if (isSelected) PrimaryNeonGreen else Color.White.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingGoalStepPreview() {
    HabfitTheme {
        Box(modifier = Modifier.background(Background)) {
            OnboardingPageContent(
                pageIndex = 0,
                selectedGoals = setOf("Build Healthy Habits"),
                selectedLevel = null,
                selectedActivities = emptySet(),
                selectedTime = null,
                selectedReminder = null,
                onGoalToggled = {},
                onLevelSelected = {},
                onActivityToggled = {},
                onTimeSelected = {},
                onReminderSelected = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingLevelStepPreview() {
    HabfitTheme {
        Box(modifier = Modifier.background(Background)) {
            OnboardingPageContent(
                pageIndex = 1,
                selectedGoals = emptySet(),
                selectedLevel = "Intermediate",
                selectedActivities = emptySet(),
                selectedTime = null,
                selectedReminder = null,
                onGoalToggled = {},
                onLevelSelected = {},
                onActivityToggled = {},
                onTimeSelected = {},
                onReminderSelected = {}
            )
        }
    }
}
