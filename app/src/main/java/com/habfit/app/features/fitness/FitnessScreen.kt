package com.habfit.app.features.fitness

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.habfit.app.domain.model.FitnessGoal
import com.habfit.app.domain.model.Workout
import com.habfit.app.ui.components.HabfitButton
import com.habfit.app.ui.components.HabfitCard
import com.habfit.app.ui.components.HabfitSectionTitle
import com.habfit.app.ui.components.HabfitTextField
import com.habfit.app.ui.theme.Background
import com.habfit.app.ui.theme.CardBackground
import com.habfit.app.ui.theme.ErrorColor
import com.habfit.app.ui.theme.GoldReward
import com.habfit.app.ui.theme.PrimaryNeonGreen
import com.habfit.app.ui.theme.PrimaryText
import com.habfit.app.ui.theme.SecondaryText

@Composable
fun FitnessScreen(
    viewModel: FitnessViewModel = hiltViewModel()
) {
    val goals by viewModel.goals.collectAsState()
    val workouts by viewModel.workouts.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    var showLogDialog by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Background,
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showLogDialog = true },
                    containerColor = PrimaryNeonGreen,
                    contentColor = Color.Black,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Log Workout")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                Text(
                    text = "FITNESS TRACKER",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
                Spacer(modifier = Modifier.height(12.dp))
                FitnessTabSelector(
                    selectedTab = selectedTab,
                    onSelectTab = { viewModel.selectTab(it) }
                )
            }

            if (selectedTab == 0) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HabfitSectionTitle(title = "Active Fitness Goals")
                            Text(
                                text = "+ Add Goal",
                                color = PrimaryNeonGreen,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { showGoalDialog = true }
                            )
                        }
                    }

                    if (goals.isEmpty()) {
                        item {
                            Text(text = "No fitness goals yet. Tap + Add Goal!", color = SecondaryText, fontSize = 13.sp)
                        }
                    } else {
                        items(goals, key = { it.id }) { goal ->
                            FitnessGoalItem(goal = goal, onDelete = { viewModel.deleteGoal(goal.id) })
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

                    item { Spacer(modifier = Modifier.height(20.dp)) }
                    item { HabfitSectionTitle(title = "Workout History") }

                    if (workouts.isEmpty()) {
                        item {
                            Text(text = "No workouts logged yet. Tap + to log your first session!", color = SecondaryText, fontSize = 13.sp)
                        }
                    } else {
                        items(workouts, key = { it.id }) { workout ->
                            WorkoutHistoryItem(
                                workout = workout,
                                onDelete = { viewModel.deleteWorkout(workout.id) }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            } else {
                FitnessMapContent()
            }
        }
    }

    if (showLogDialog) {
        LogWorkoutDialog(
            onDismiss = { showLogDialog = false },
            onConfirm = { title, type, mins, cals, dist, intensity, notes ->
                viewModel.logWorkout(title, type, mins, cals, dist, intensity, notes)
                showLogDialog = false
            }
        )
    }

    if (showGoalDialog) {
        AddFitnessGoalDialog(
            onDismiss = { showGoalDialog = false },
            onConfirm = { title, type, target, unit ->
                viewModel.addGoal(title, type, target, unit)
                showGoalDialog = false
            }
        )
    }
}

@Composable
fun FitnessTabSelector(
    selectedTab: Int,
    onSelectTab: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackground)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(if (selectedTab == 0) PrimaryNeonGreen else Color.Transparent)
                .clickable { onSelectTab(0) }
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Workouts & Goals",
                color = if (selectedTab == 0) Color.Black else SecondaryText,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(if (selectedTab == 1) PrimaryNeonGreen else Color.Transparent)
                .clickable { onSelectTab(1) }
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nearby Gyms",
                color = if (selectedTab == 1) Color.Black else SecondaryText,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun FitnessGoalItem(
    goal: FitnessGoal,
    onDelete: () -> Unit
) {
    val progress = (goal.currentValue / goal.targetValue.coerceAtLeast(1f)).coerceIn(0f, 1f)
    HabfitCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = goal.title, color = PrimaryText, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = SecondaryText.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = PrimaryNeonGreen,
                trackColor = Color.White.copy(alpha = 0.1f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${goal.currentValue.toInt()} / ${goal.targetValue.toInt()} ${goal.unit}",
                    color = SecondaryText,
                    fontSize = 12.sp
                )
                Text(
                    text = "${(progress * 100).toInt()}% Done",
                    color = PrimaryNeonGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun WorkoutHistoryItem(
    workout: Workout,
    onDelete: () -> Unit
) {
    HabfitCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = getWorkoutIcon(workout.type)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PrimaryNeonGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = workout.type, tint = PrimaryNeonGreen)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = workout.title, color = PrimaryText, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${workout.durationMinutes} mins  •  ${workout.caloriesBurned} kcal" +
                            if (workout.distanceKm > 0) "  •  ${workout.distanceKm} km" else "",
                    color = SecondaryText,
                    fontSize = 12.sp
                )
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = SecondaryText.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
            }
        }
    }
}

fun getWorkoutIcon(type: String): ImageVector {
    return when (type.lowercase()) {
        "running" -> Icons.Default.DirectionsRun
        "cycling" -> Icons.Default.DirectionsBike
        "walking" -> Icons.Default.DirectionsWalk
        "yoga" -> Icons.Default.SelfImprovement
        else -> Icons.Default.FitnessCenter
    }
}

@Composable
fun FitnessMapContent() {
    val centerPos = LatLng(1.3521, 103.8198)
    val gymA = LatLng(1.3540, 103.8240)
    val gymB = LatLng(1.3490, 103.8150)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(centerPos, 13f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(mapType = MapType.NORMAL),
            uiSettings = MapUiSettings(zoomControlsEnabled = false)
        ) {
            Marker(
                state = remember { MarkerState(position = gymA) },
                title = "PowerFit Gym & Studio",
                snippet = "0.8 km away • Open 24/7"
            )
            Marker(
                state = remember { MarkerState(position = gymB) },
                title = "Olympic CrossFit Arena",
                snippet = "1.4 km away • Functional Training"
            )
        }
    }
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
    var targetText by remember { mutableStateOf("20") }
    var unit by remember { mutableStateOf("km") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBackground,
        title = { Text("Add Fitness Goal", color = PrimaryText, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
        text = {
            Column {
                HabfitTextField(value = title, onValueChange = { title = it }, label = "Goal Title")
                Spacer(modifier = Modifier.height(12.dp))
                Row {
                    Box(modifier = Modifier.weight(1f)) {
                        HabfitTextField(value = targetText, onValueChange = { targetText = it }, label = "Target Value")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        HabfitTextField(value = unit, onValueChange = { unit = it }, label = "Unit (km, sessions, kcal)")
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
                    onConfirm(title, "Custom", target, unit)
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = SecondaryText) }
        }
    )
}
