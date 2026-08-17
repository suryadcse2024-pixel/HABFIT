package com.habfit.app.features.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
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
import com.habfit.app.ui.components.AddHabitDialog
import com.habfit.app.ui.components.HabitCard
import com.habfit.app.ui.theme.Background
import com.habfit.app.ui.theme.PrimaryNeonGreen
import com.habfit.app.ui.theme.PrimaryText
import com.habfit.app.ui.theme.SecondaryText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HabitScreen(
    viewModel: HabitViewModel = hiltViewModel()
) {
    val habits by viewModel.habits.collectAsState()
    val weeklyStats by viewModel.weeklyStats.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    
    val headerLabel = remember(selectedDate) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = sdf.format(Date())
        if (selectedDate == today) {
            "Today, ${SimpleDateFormat("MMMM d", Locale.getDefault()).format(Date())}"
        } else {
            val date = sdf.parse(selectedDate) ?: Date()
            SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(date)
        }
    }

    Scaffold(
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = headerLabel,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weeklyStats.forEach { stats ->
                    DayItem(
                        stats = stats,
                        onClick = { viewModel.selectDate(stats.date) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (habits.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            Text("No habits for this day", color = SecondaryText)
                        }
                    }
                } else {
                    items(habits, key = { it.habit.id }) { uiModel ->
                        HabitCard(
                            habit = uiModel.habit,
                            isCompleted = uiModel.isCompleted,
                            onToggle = { viewModel.toggleHabitCompletion(uiModel.habit) },
                            onDelete = { viewModel.deleteHabit(uiModel.habit.id) }
                        )
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    AddHabitButton(onClick = { showAddDialog = true })
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }

    if (showAddDialog) {
        AddHabitDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, category, target, freq, reminder ->
                viewModel.addHabit(name, category, target, freq, reminder)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun DayItem(
    stats: DailyStats,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .then(
                if (stats.isSelected) {
                    Modifier.border(2.dp, PrimaryNeonGreen, CircleShape).padding(8.dp)
                } else {
                    Modifier.padding(8.dp)
                }
            )
    ) {
        Text(
            text = stats.dayLabel,
            fontSize = 12.sp,
            color = if (stats.isSelected) PrimaryNeonGreen else SecondaryText,
            fontWeight = if (stats.isSelected) FontWeight.Bold else FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${stats.completed}/${stats.total}",
            fontSize = 11.sp,
            color = if (stats.isSelected) PrimaryText else SecondaryText.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun AddHabitButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .clickable { onClick() }
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Add, contentDescription = null, tint = PrimaryNeonGreen)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "ADD HABIT",
                color = PrimaryText,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}
