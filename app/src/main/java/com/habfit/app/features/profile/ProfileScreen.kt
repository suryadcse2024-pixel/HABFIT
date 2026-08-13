package com.habfit.app.features.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.habfit.app.domain.model.Badge
import com.habfit.app.domain.model.User
import com.habfit.app.ui.components.HabfitButton
import com.habfit.app.ui.components.HabfitCard
import com.habfit.app.ui.components.HabfitTextField
import com.habfit.app.ui.theme.Background
import com.habfit.app.ui.theme.CardBackground
import com.habfit.app.ui.theme.GoldReward
import com.habfit.app.ui.theme.PrimaryNeonGreen
import com.habfit.app.ui.theme.PrimaryText
import com.habfit.app.ui.theme.SecondaryText

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val badges by viewModel.badges.collectAsState()
    var showEditProfileDialog by remember { mutableStateOf(false) }

    val name = user?.name ?: "Habfit Champion"
    val level = user?.level ?: 3
    val points = user?.points ?: 450
    val streak = user?.currentStreak ?: 7
    val levelTitle = when (level) {
        1 -> "Level 1 Consistency Explorer"
        2 -> "Level 2 Habit Builder"
        3 -> "Level 3 Fitness Enthusiast"
        4 -> "Level 4 Routine Warrior"
        else -> "Level 5 Habit Master"
    }

    Scaffold(
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Profile Avatar & Edit button
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(CardBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = SecondaryText,
                        modifier = Modifier.size(50.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PrimaryNeonGreen)
                        .clickable { showEditProfileDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
            Text(text = levelTitle, color = PrimaryNeonGreen, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(32.dp))

            // Stat Cards
            Row(modifier = Modifier.fillMaxWidth()) {
                ProfileStatCard("HAB Coins", String.format("%,d", points), GoldReward, Modifier.weight(1f))
                Spacer(modifier = Modifier.width(16.dp))
                ProfileStatCard("Streak", "🔥 $streak days", PrimaryNeonGreen, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Achievements Section
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                Text(text = "ACHIEVEMENTS & BADGES", color = SecondaryText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                if (badges.isEmpty()) {
                    AchievementItem(title = "30 Day Streak", subtitle = "Master of consistency", isUnlocked = false)
                    AchievementItem(title = "Fitness Warrior", subtitle = "Completed 10 workouts", isUnlocked = false)
                } else {
                    badges.forEach { badge ->
                        AchievementItem(
                            title = badge.name,
                            subtitle = badge.description,
                            isUnlocked = badge.isUnlocked
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Preferences / Notifications Card
            HabfitCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = PrimaryNeonGreen)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Daily Habit Reminders", color = PrimaryText, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text(text = "Get notified at scheduled habit times", color = SecondaryText, fontSize = 11.sp)
                    }
                    Switch(
                        checked = user?.isNotificationsEnabled ?: true,
                        onCheckedChange = { viewModel.updateNotifications(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = PrimaryNeonGreen,
                            uncheckedThumbColor = SecondaryText,
                            uncheckedTrackColor = CardBackground
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (showEditProfileDialog) {
        EditProfileDialog(
            currentName = name,
            currentGoal = user?.mainGoal ?: "Build Consistency & Fitness",
            onDismiss = { showEditProfileDialog = false },
            onConfirm = { newName, newGoal ->
                viewModel.updateProfile(newName, newGoal)
                showEditProfileDialog = false
            }
        )
    }
}

@Composable
fun ProfileStatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    HabfitCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, color = SecondaryText, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = color, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AchievementItem(title: String, subtitle: String, isUnlocked: Boolean) {
    HabfitCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                contentDescription = null,
                tint = if (isUnlocked) GoldReward else SecondaryText.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = if (isUnlocked) PrimaryText else SecondaryText,
                    fontWeight = FontWeight.SemiBold
                )
                Text(text = subtitle, color = SecondaryText, fontSize = 12.sp)
            }
            if (isUnlocked) {
                Text(text = "UNLOCKED", color = GoldReward, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    currentName: String,
    currentGoal: String,
    onDismiss: () -> Unit,
    onConfirm: (name: String, goal: String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var goal by remember { mutableStateOf(currentGoal) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBackground,
        title = { Text("Edit Profile", color = PrimaryText, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
        text = {
            Column {
                HabfitTextField(value = name, onValueChange = { name = it }, label = "Display Name")
                Spacer(modifier = Modifier.height(12.dp))
                HabfitTextField(value = goal, onValueChange = { goal = it }, label = "Primary Fitness Goal")
            }
        },
        confirmButton = {
            HabfitButton(
                text = "SAVE CHANGES",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, goal)
                    }
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = SecondaryText) }
        }
    )
}
