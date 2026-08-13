package com.habfit.app.features.onboarding

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.habfit.app.ui.components.HabfitButton
import com.habfit.app.ui.components.HabfitCard
import com.habfit.app.ui.theme.Background
import com.habfit.app.ui.theme.CardBackground
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

    val selectedGoal by viewModel.selectedGoal.collectAsState()
    val selectedLevel by viewModel.selectedLevel.collectAsState()
    val selectedActivities by viewModel.selectedActivities.collectAsState()
    val selectedTime by viewModel.selectedTime.collectAsState()
    val selectedHabits by viewModel.selectedHabits.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            OnboardingPageContent(
                pageIndex = page,
                selectedGoal = selectedGoal,
                selectedLevel = selectedLevel,
                selectedActivities = selectedActivities,
                selectedTime = selectedTime,
                selectedHabits = selectedHabits,
                onGoalSelected = { viewModel.setGoal(it) },
                onLevelSelected = { viewModel.setLevel(it) },
                onActivityToggled = { viewModel.toggleActivity(it) },
                onTimeSelected = { viewModel.setTime(it) },
                onHabitToggled = { viewModel.toggleHabit(it) }
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
        ) {
            HabfitButton(
                text = if (pagerState.currentPage == 4) "GENERATE MY HABFIT PLAN" else "NEXT",
                onClick = {
                    if (pagerState.currentPage < 4) {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        viewModel.completeOnboarding(onFinished)
                    }
                }
            )
        }
    }
}

@Composable
fun OnboardingPageContent(
    pageIndex: Int,
    selectedGoal: String,
    selectedLevel: String,
    selectedActivities: Set<String>,
    selectedTime: Int,
    selectedHabits: Set<String>,
    onGoalSelected: (String) -> Unit,
    onLevelSelected: (String) -> Unit,
    onActivityToggled: (String) -> Unit,
    onTimeSelected: (Int) -> Unit,
    onHabitToggled: (String) -> Unit
) {
    val title = when (pageIndex) {
        0 -> "What's your main goal?"
        1 -> "Fitness Level"
        2 -> "Preferred Activities"
        3 -> "Daily Available Time"
        4 -> "Choose Your Habits"
        else -> ""
    }

    val subtitle = when (pageIndex) {
        0 -> "Help us personalize your daily routines"
        1 -> "Select where you are in your journey"
        2 -> "What do you enjoy doing most?"
        3 -> "How much time can you commit each day?"
        4 -> "Start with these core consistency habits"
        else -> ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryText,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            fontSize = 14.sp,
            color = SecondaryText,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))

        when (pageIndex) {
            0 -> {
                val goals = listOf(
                    "Build Consistency & Fitness",
                    "Lose Body Fat & Get Lean",
                    "Gain Muscle & Strength",
                    "Improve Daily Energy & Focus",
                    "Fix Sleep & Daily Routine"
                )
                goals.forEach { goal ->
                    SelectableItemCard(
                        text = goal,
                        isSelected = selectedGoal == goal,
                        onClick = { onGoalSelected(goal) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            1 -> {
                val levels = listOf(
                    "Beginner" to "Starting fresh or restarting routine",
                    "Intermediate" to "Train 2-3x / week with basic habits",
                    "Advanced" to "Consistent athlete seeking peak optimization"
                )
                levels.forEach { (level, desc) ->
                    SelectableItemCard(
                        text = level,
                        subText = desc,
                        isSelected = selectedLevel == level,
                        onClick = { onLevelSelected(level) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            2 -> {
                val activities = listOf(
                    "Strength / Gym",
                    "Running / Walking",
                    "Home Workouts",
                    "Yoga & Mobility",
                    "Cycling / HIIT"
                )
                activities.forEach { act ->
                    SelectableItemCard(
                        text = act,
                        isSelected = selectedActivities.contains(act),
                        isMultiSelect = true,
                        onClick = { onActivityToggled(act) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            3 -> {
                val times = listOf(
                    15 to "15 minutes (Quick & Focused)",
                    30 to "30 minutes (Balanced Routine)",
                    45 to "45 minutes (Comprehensive)",
                    60 to "60+ minutes (Intense Focus)"
                )
                times.forEach { (mins, label) ->
                    SelectableItemCard(
                        text = label,
                        isSelected = selectedTime == mins,
                        onClick = { onTimeSelected(mins) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            4 -> {
                val habits = listOf(
                    "Drink 2.5L Water" to "Health",
                    "Morning Stretch & Mobility" to "Fitness",
                    "Hit 8,000 Daily Steps" to "Fitness",
                    "Read 15 Pages" to "Mind",
                    "10-min Evening Reflection" to "Routine",
                    "No Late Snacking after 8PM" to "Health"
                )
                habits.forEach { (habit, cat) ->
                    SelectableItemCard(
                        text = habit,
                        subText = "Category: $cat",
                        isSelected = selectedHabits.contains(habit),
                        isMultiSelect = true,
                        onClick = { onHabitToggled(habit) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(100.dp)) // space for bottom button
    }
}

@Composable
fun SelectableItemCard(
    text: String,
    subText: String? = null,
    isSelected: Boolean,
    isMultiSelect: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) CardBackground else CardBackground.copy(alpha = 0.6f)
        ),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) PrimaryNeonGreen else Color.White.copy(alpha = 0.08f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = text,
                    color = if (isSelected) PrimaryNeonGreen else PrimaryText,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                if (subText != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subText,
                        color = SecondaryText,
                        fontSize = 12.sp
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(if (isMultiSelect) RoundedCornerShape(6.dp) else CircleShape)
                    .background(if (isSelected) PrimaryNeonGreen else Color.White.copy(alpha = 0.08f)),
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
