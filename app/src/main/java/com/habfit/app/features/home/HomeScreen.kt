package com.habfit.app.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.habfit.app.domain.model.AssistantTask
import com.habfit.app.domain.model.LifeScoreData
import com.habfit.app.domain.model.User
import com.habfit.app.ui.components.HabfitCard
import com.habfit.app.ui.components.HabfitSectionTitle
import com.habfit.app.ui.components.HabfitStatCard
import com.habfit.app.ui.theme.Background
import com.habfit.app.ui.theme.CardBackground
import com.habfit.app.ui.theme.GoldReward
import com.habfit.app.ui.theme.PrimaryNeonGreen
import com.habfit.app.ui.theme.PrimaryText
import com.habfit.app.ui.theme.SecondaryText
import java.util.Calendar

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val lifeScore by viewModel.lifeScore.collectAsState()
    val missions by viewModel.dailyMissions.collectAsState()
    val totalSteps by viewModel.totalSteps.collectAsState()
    val totalCalories by viewModel.totalCalories.collectAsState()

    Scaffold(
        containerColor = Background,
        topBar = { HomeTopBar(user = user) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            item { LifeScoreSection(lifeScore = lifeScore) }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item { HabfitSectionTitle(title = "Weekly Activity") }
            item {
                WeeklyActivitySection(
                    steps = totalSteps,
                    calories = totalCalories
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item { HabfitSectionTitle(title = "Today's Mission") }
            item {
                MissionSection(
                    missions = missions,
                    onToggleMission = { viewModel.toggleMission(it) }
                )
            }
            item { Spacer(modifier = Modifier.height(80.dp)) } // Space for bottom nav
        }
    }
}

@Composable
fun HomeTopBar(user: User?) {
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        currentHour < 12 -> "Good Morning"
        currentHour < 17 -> "Good Afternoon"
        else -> "Good Evening"
    }
    val name = user?.name ?: "Habfit Champion"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "$greeting, $name",
                color = PrimaryText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Let's make today count.",
                color = SecondaryText,
                fontSize = 14.sp
            )
        }
        IconButton(onClick = { }) {
            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = PrimaryText)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(CardBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = "Profile", tint = SecondaryText)
        }
    }
}

@Composable
fun LifeScoreSection(lifeScore: LifeScoreData) {
    HabfitCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "LIFE SCORE",
                    color = SecondaryText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${lifeScore.score} / 100",
                    color = PrimaryText,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "+${lifeScore.weeklyChangePercent}% this week",
                    color = PrimaryNeonGreen,
                    fontSize = 14.sp
                )
            }
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(PrimaryNeonGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${lifeScore.score}%",
                    color = PrimaryNeonGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun WeeklyActivitySection(steps: Int, calories: Int) {
    val formattedSteps = String.format("%,d", steps)
    val stepsPercentage = ((steps / 10000f) * 100).toInt()
    Row(modifier = Modifier.fillMaxWidth()) {
        HabfitStatCard(
            label = "Steps",
            value = formattedSteps,
            subValue = "$stepsPercentage% of goal",
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        HabfitStatCard(
            label = "Calories",
            value = "$calories kcal",
            subValue = "Active Burned",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun MissionSection(
    missions: List<AssistantTask>,
    onToggleMission: (AssistantTask) -> Unit
) {
    Column {
        if (missions.isEmpty()) {
            HabfitCard(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "All missions completed for today! 🎉", color = SecondaryText)
                }
            }
        } else {
            missions.forEach { mission ->
                MissionItem(
                    task = mission,
                    onToggle = { onToggleMission(mission) }
                )
            }
        }
    }
}

@Composable
fun MissionItem(
    task: AssistantTask,
    onToggle: () -> Unit
) {
    HabfitCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onToggle() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    color = if (task.isCompleted) SecondaryText else PrimaryText,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "+${task.rewardPoints} HAB Coins",
                    color = GoldReward,
                    fontSize = 12.sp
                )
            }
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (task.isCompleted) PrimaryNeonGreen else Color.White.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
