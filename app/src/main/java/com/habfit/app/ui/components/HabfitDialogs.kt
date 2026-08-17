package com.habfit.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.habfit.app.ui.theme.Background
import com.habfit.app.ui.theme.CardBackground
import com.habfit.app.ui.theme.PrimaryNeonGreen
import com.habfit.app.ui.theme.PrimaryText
import com.habfit.app.ui.theme.SecondaryText

@Composable
fun AddHabitDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, category: String, target: String, frequency: String, reminder: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Health") }
    var selectedFreq by remember { mutableStateOf("Daily") }
    var target by remember { mutableStateOf("1 time") }
    var reminder by remember { mutableStateOf("08:00 AM") }

    val categories = listOf("Health", "Fitness", "Mind", "Routine", "Food")
    val frequencies = listOf("Daily", "Weekdays", "Weekends")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBackground,
        title = {
            Text("Add New Habit", color = PrimaryText, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        },
        text = {
            Column {
                HabfitTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Habit Name (e.g. Read 20 mins)"
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text("Category", color = SecondaryText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    categories.forEach { cat ->
                        val isSel = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSel) PrimaryNeonGreen else Background)
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (isSel) Color.Black else SecondaryText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Frequency", color = SecondaryText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    frequencies.forEach { freq ->
                        val isSel = selectedFreq == freq
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSel) PrimaryNeonGreen else Background)
                                .clickable { selectedFreq = freq }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = freq,
                                color = if (isSel) Color.Black else SecondaryText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HabfitTextField(
                    value = reminder,
                    onValueChange = { reminder = it },
                    label = "Reminder Time (e.g. 08:00 AM)"
                )
            }
        },
        confirmButton = {
            HabfitButton(
                text = "SAVE HABIT",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, selectedCategory, target, selectedFreq, reminder)
                    }
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SecondaryText)
            }
        }
    )
}

@Composable
fun LogWorkoutDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, type: String, duration: Int, calories: Int, dist: Float, intensity: String, notes: String) -> Unit
) {
    var title by remember { mutableStateOf("Strength Training Session") }
    var selectedType by remember { mutableStateOf("Strength") }
    var durationText by remember { mutableStateOf("35") }
    var caloriesText by remember { mutableStateOf("280") }
    var distanceText by remember { mutableStateOf("0") }
    var notes by remember { mutableStateOf("") }

    val types = listOf("Strength", "Running", "HIIT", "Yoga", "Cycling", "Walking")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBackground,
        title = { Text("Log Workout", color = PrimaryText, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
        text = {
            Column {
                HabfitTextField(value = title, onValueChange = { title = it }, label = "Workout Title")
                Spacer(modifier = Modifier.height(12.dp))
                Text("Activity Type", color = SecondaryText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    types.take(3).forEach { t ->
                        val isSel = selectedType == t
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) PrimaryNeonGreen else Background)
                                .clickable { selectedType = t }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(text = t, color = if (isSel) Color.Black else SecondaryText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    types.drop(3).forEach { t ->
                        val isSel = selectedType == t
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) PrimaryNeonGreen else Background)
                                .clickable { selectedType = t }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(text = t, color = if (isSel) Color.Black else SecondaryText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row {
                    Box(modifier = Modifier.weight(1f)) {
                        HabfitTextField(value = durationText, onValueChange = { durationText = it }, label = "Duration (mins)")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        HabfitTextField(value = caloriesText, onValueChange = { caloriesText = it }, label = "Calories (kcal)")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                HabfitTextField(value = distanceText, onValueChange = { distanceText = it }, label = "Distance (km) - optional")
            }
        },
        confirmButton = {
            HabfitButton(
                text = "LOG WORKOUT",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                onClick = {
                    val duration = durationText.toIntOrNull() ?: 30
                    val calories = caloriesText.toIntOrNull() ?: 250
                    val dist = distanceText.toFloatOrNull() ?: 0f
                    onConfirm(title, selectedType, duration, calories, dist, "Medium", notes)
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = SecondaryText) }
        }
    )
}

@Composable
fun AddFitnessGoalDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, type: String, target: Float, unit: String) -> Unit
) {
    var title by remember { mutableStateOf("Run 20km this week") }
    var selectedType by remember { mutableStateOf("Running") }
    var targetText by remember { mutableStateOf("20") }
    var unit by remember { mutableStateOf("km") }

    val types = listOf("Workouts", "Running", "Steps", "Calories", "Cycling")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBackground,
        title = { Text("Add Fitness Goal", color = PrimaryText, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
        text = {
            Column {
                HabfitTextField(value = title, onValueChange = { title = it }, label = "Goal Title")
                Spacer(modifier = Modifier.height(12.dp))
                
                Text("Goal Type", color = SecondaryText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    types.forEach { t ->
                        val isSel = selectedType == t
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) PrimaryNeonGreen else Background)
                                .clickable { 
                                    selectedType = t
                                    unit = when(t) {
                                        "Running", "Cycling" -> "km"
                                        "Steps" -> "steps"
                                        "Calories" -> "kcal"
                                        else -> "sessions"
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(text = t, color = if (isSel) Color.Black else SecondaryText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                Row {
                    Box(modifier = Modifier.weight(1f)) {
                        HabfitTextField(value = targetText, onValueChange = { targetText = it }, label = "Target Value")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        HabfitTextField(value = unit, onValueChange = { unit = it }, label = "Unit")
                    }
                }
            }
        },
        confirmButton = {
            HabfitButton(
                text = "SET GOAL",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                onClick = {
                    val target = targetText.toFloatOrNull() ?: 10f
                    onConfirm(title, selectedType, target, unit)
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = SecondaryText) }
        }
    )
}
