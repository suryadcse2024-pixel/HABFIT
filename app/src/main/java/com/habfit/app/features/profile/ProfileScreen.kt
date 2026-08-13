package com.habfit.app.features.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
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
import com.habfit.app.ui.theme.Background
import com.habfit.app.ui.theme.CardBackground
import com.habfit.app.ui.theme.GoldReward
import com.habfit.app.ui.theme.PrimaryNeonGreen
import com.habfit.app.ui.theme.PrimaryText
import com.habfit.app.ui.theme.SecondaryText

@Composable
fun ProfileScreen() {
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
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(CardBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = SecondaryText, modifier = Modifier.size(50.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Habfit User", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
            Text(text = "Level 5 Habit Master", color = PrimaryNeonGreen, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                ProfileStatCard("HAB Coins", "1,250", GoldReward, Modifier.weight(1f))
                Spacer(modifier = Modifier.width(16.dp))
                ProfileStatCard("Streak", "🔥 12", PrimaryNeonGreen, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                Text(text = "ACHIEVEMENTS", color = SecondaryText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                AchievementItem("30 Day Streak", "Master of consistency")
                AchievementItem("Fitness Warrior", "Completed 50 workouts")
            }
        }
    }
}

@Composable
fun ProfileStatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    HabfitCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, color = SecondaryText, fontSize = 12.sp)
            Text(text = value, color = color, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AchievementItem(title: String, subtitle: String) {
    HabfitCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = GoldReward)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, color = PrimaryText, fontWeight = FontWeight.SemiBold)
                Text(text = subtitle, color = SecondaryText, fontSize = 12.sp)
            }
        }
    }
}
