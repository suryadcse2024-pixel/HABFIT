package com.habfit.app.features.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habfit.app.ui.components.HabfitCard
import com.habfit.app.ui.components.HabfitSectionTitle
import com.habfit.app.ui.components.HabfitStatCard
import com.habfit.app.ui.theme.Background
import com.habfit.app.ui.theme.CardBackground
import com.habfit.app.ui.theme.PrimaryNeonGreen
import com.habfit.app.ui.theme.PrimaryText
import com.habfit.app.ui.theme.SecondaryText

@Composable
fun HomeScreen() {
    Scaffold(
        containerColor = Background,
        topBar = { HomeTopBar() }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            item { LifeScoreSection() }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item { HabfitSectionTitle(title = "Weekly Activity") }
            item { WeeklyActivitySection() }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item { HabfitSectionTitle(title = "Today's Mission") }
            item { MissionSection() }
            item { Spacer(modifier = Modifier.height(80.dp)) } // Space for bottom nav
        }
    }
}

@Composable
fun HomeTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Good Morning, User", color = PrimaryText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = "Let's make today count.", color = SecondaryText, fontSize = 14.sp)
        }
        IconButton(onClick = { }) {
            Icon(Icons.Default.Notifications, contentDescription = null, tint = PrimaryText)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(CardBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = SecondaryText)
        }
    }
}

@Composable
fun LifeScoreSection() {
    HabfitCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "LIFE SCORE", color = SecondaryText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "78 / 100", color = PrimaryText, fontSize = 32.sp, fontWeight = FontWeight.Black)
                Text(text = "+8% this week", color = PrimaryNeonGreen, fontSize = 14.sp)
            }
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(PrimaryNeonGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "78%", color = PrimaryNeonGreen, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun WeeklyActivitySection() {
    Row(modifier = Modifier.fillMaxWidth()) {
        HabfitStatCard(label = "Steps", value = "7,842", subValue = "80% of goal", modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(16.dp))
        HabfitStatCard(label = "Calories", value = "420", subValue = "Burned", modifier = Modifier.weight(1f))
    }
}

@Composable
fun MissionSection() {
    Column {
        MissionItem("Drink 2L Water", "+10 HAB Coins", true)
        MissionItem("8,000 Steps", "+20 HAB Coins", false)
        MissionItem("Complete Workout", "+30 HAB Coins", false)
    }
}

@Composable
fun MissionItem(title: String, reward: String, isCompleted: Boolean) {
    HabfitCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = PrimaryText, fontWeight = FontWeight.SemiBold)
                Text(text = reward, color = Color(0xFFFFD700), fontSize = 12.sp)
            }
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = PrimaryNeonGreen
                )
            }
        }
    }
}
