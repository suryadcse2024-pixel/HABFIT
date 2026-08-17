package com.habfit.app.features.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.habfit.app.domain.model.User
import com.habfit.app.ui.components.AddHabitDialog
import com.habfit.app.ui.components.HabitCard
import com.habfit.app.ui.components.HabfitCard
import com.habfit.app.ui.components.HabfitSectionTitle
import com.habfit.app.ui.components.HabfitStatCard
import com.habfit.app.ui.components.LogWorkoutDialog
import com.habfit.app.ui.components.WorkoutHistoryItem
import com.habfit.app.ui.theme.Background
import com.habfit.app.ui.theme.CardBackground
import com.habfit.app.ui.theme.GoldReward
import com.habfit.app.ui.theme.PrimaryNeonGreen
import com.habfit.app.ui.theme.PrimaryText
import com.habfit.app.ui.theme.SecondaryText
import java.util.Calendar

@Composable
fun HomeScreen(
    onNavigateToHabits: () -> Unit = {},
    onNavigateToFitness: () -> Unit = {},
    onNavigateToAI: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val habits by viewModel.habits.collectAsState()
    val recentWorkouts by viewModel.recentWorkouts.collectAsState()
    
    var showAddHabitDialog by remember { mutableStateOf(false) }
    var showLogWorkoutDialog by remember { mutableStateOf(false) }

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
            item { 
                ProgressSection(
                    progress = 0,
                    streak = user?.currentStreak ?: 0,
                    points = user?.points ?: 0
                ) 
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
            
            item { HabfitSectionTitle(title = "Daily Quick Actions") }
            item {
                Row(modifier = Modifier.fillMaxWidth()) {
                    ActionButton(
                        text = "Add Habit",
                        icon = Icons.Default.Add,
                        onClick = { showAddHabitDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    ActionButton(
                        text = "Log Workout",
                        icon = Icons.Default.FitnessCenter,
                        onClick = { showLogWorkoutDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    ActionButton(
                        text = "AI Assistant",
                        icon = Icons.Default.AutoAwesome,
                        onClick = onNavigateToAI,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    ActionButton(
                        text = "Progress",
                        icon = Icons.Default.TrendingUp,
                        onClick = onNavigateToFitness,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
            
            item { HabfitSectionTitle(title = "Today's Habits") }
            if (habits.isEmpty()) {
                item { PlaceholderCard(text = "No habits yet") }
            } else {
                items(habits.take(3), key = { it.id }) { habit ->
                    HabitCard(
                        habit = habit,
                        isCompleted = habit.isCompletedToday,
                        onToggle = { viewModel.toggleHabit(habit) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
            
            item { HabfitSectionTitle(title = "Recent Fitness") }
            if (recentWorkouts.isEmpty()) {
                item { PlaceholderCard(text = "No workout logged today") }
            } else {
                items(recentWorkouts.take(2), key = { it.id }) { workout ->
                    WorkoutHistoryItem(workout = workout)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
            
            item { HabfitSectionTitle(title = "HABFIT Assistant") }
            item {
                PlaceholderCard(
                    text = "Complete your onboarding and start building your routine!",
                    isAssistant = true
                )
            }
            
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }

    if (showAddHabitDialog) {
        AddHabitDialog(
            onDismiss = { showAddHabitDialog = false },
            onConfirm = { name, cat, target, freq, rem ->
                viewModel.addHabit(name, cat, target, freq, rem)
                showAddHabitDialog = false
            }
        )
    }

    if (showLogWorkoutDialog) {
        LogWorkoutDialog(
            onDismiss = { showLogWorkoutDialog = false },
            onConfirm = { title, type, mins, cals, dist, intensity, notes ->
                viewModel.logWorkout(title, type, mins, cals, dist, intensity, notes)
                showLogWorkoutDialog = false
            }
        )
    }
}

@Composable
fun HomeTopBar(user: User?) {
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        currentHour < 12 -> "GOOD MORNING 👋"
        currentHour < 17 -> "GOOD AFTERNOON 👋"
        else -> "GOOD EVENING 👋"
    }
    val name = user?.name ?: "Habfit Champion"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = greeting,
                color = SecondaryText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = name.uppercase(),
                color = PrimaryText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )
        }
        IconButton(
            onClick = { },
            modifier = Modifier
                .clip(CircleShape)
                .background(CardBackground)
        ) {
            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = PrimaryText)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(PrimaryNeonGreen.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = "Profile", tint = PrimaryNeonGreen)
        }
    }
}

@Composable
fun ProgressSection(progress: Int, streak: Int, points: Int) {
    Row(modifier = Modifier.fillMaxWidth()) {
        HabfitStatCard(
            label = "Today's Progress",
            value = "$progress%",
            subValue = "Consistency Rate",
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            HabfitCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Current Streak", color = SecondaryText, fontSize = 12.sp)
                    Text(text = "🔥 $streak Days", color = Color(0xFFFF8A00), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            HabfitCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Points", color = SecondaryText, fontSize = 12.sp)
                    Text(text = "⭐ $points", color = GoldReward, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    HabfitCard(
        modifier = modifier
            .height(60.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = PrimaryNeonGreen, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = text, color = PrimaryText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PlaceholderCard(text: String, isAssistant: Boolean = false) {
    HabfitCard(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = if (isAssistant) Alignment.TopStart else Alignment.Center
        ) {
            Text(
                text = text,
                color = if (isAssistant) PrimaryText else SecondaryText,
                textAlign = if (isAssistant) TextAlign.Start else TextAlign.Center,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}
