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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.viewinterop.AndroidView
import com.habfit.app.domain.model.Gym
import com.habfit.app.ui.components.HabfitGoalProgressBar
import com.habfit.app.ui.theme.GoldReward
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import androidx.compose.ui.platform.LocalContext
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.habfit.app.domain.model.FitnessGoal
import com.habfit.app.ui.components.AddFitnessGoalDialog
import com.habfit.app.ui.components.HabfitCard
import com.habfit.app.ui.components.HabfitSectionTitle
import com.habfit.app.ui.components.LogWorkoutDialog
import com.habfit.app.ui.components.WorkoutHistoryItem
import com.habfit.app.ui.theme.Background
import com.habfit.app.ui.theme.CardBackground
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
    val nearbyGyms by viewModel.nearbyGyms.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()

    var showLogDialog by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) {
            viewModel.refreshLocation()
        }
    }

    LaunchedEffect(selectedTab) {
        if (selectedTab == 1) {
            val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            
            if (!hasFine && !hasCoarse) {
                permissionLauncher.launch(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ))
            } else {
                viewModel.refreshLocation()
            }
        }
    }

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
                .padding(bottom = padding.calculateBottomPadding())
        ) {
            Column(modifier = Modifier
                .background(Background) // Solid background for header
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp)) {
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
                Box(modifier = Modifier
                    .weight(1f)
                    .clipToBounds()
                ) {
                    FitnessMapContent(
                        gyms = nearbyGyms,
                        userLocation = userLocation
                    )
                }
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
            HabfitGoalProgressBar(progress = progress)
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
fun FitnessMapContent(
    gyms: List<Gym>,
    userLocation: android.location.Location?
) {
    val singapore = GeoPoint(1.3521, 103.8198)
    val centerPos = if (userLocation != null) GeoPoint(userLocation.latitude, userLocation.longitude) else singapore

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(15.0)
                    controller.setCenter(centerPos)
                    
                    // Slightly Less Aggressive Dark Mode Filter
                    val matrix = ColorMatrix()
                    matrix.setSaturation(0.1f) // Keep a tiny bit of color for contrast
                    val inverse = ColorMatrix(floatArrayOf(
                        -0.7f, 0f, 0f, 0f, 200f,
                        0f, -0.7f, 0f, 0f, 200f,
                        0f, 0f, -0.7f, 0f, 200f,
                        0f, 0f, 0f, 1f, 0f
                    ))
                    matrix.postConcat(inverse)
                    
                    val filter = ColorMatrixColorFilter(matrix)
                    overlayManager.tilesOverlay.setColorFilter(filter)
                }
            },
            update = { view ->
                view.controller.setCenter(centerPos)
                view.overlays.clear()
                
                // User Location Marker
                userLocation?.let {
                    val userMarker = Marker(view)
                    userMarker.position = GeoPoint(it.latitude, it.longitude)
                    userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    userMarker.title = "You are here"
                    userMarker.icon = view.context.getDrawable(org.osmdroid.library.R.drawable.ic_menu_mylocation)
                    userMarker.icon.setTint(android.graphics.Color.CYAN)
                    view.overlays.add(userMarker)
                }

                // Gym Markers
                gyms.forEach { gym ->
                    val marker = Marker(view)
                    marker.position = GeoPoint(gym.latitude, gym.longitude)
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    marker.title = gym.name
                    marker.snippet = "${gym.distance} away • ${gym.openingHours}"
                    
                    // Using a solid default pin
                    val icon = view.context.getDrawable(org.osmdroid.library.R.drawable.marker_default)
                    icon?.setTint(android.graphics.Color.GREEN)
                    marker.icon = icon
                    
                    view.overlays.add(marker)
                }
                view.invalidate()
            }
        )

        // Gym Cards at the bottom
        if (gyms.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 80.dp), // Increased padding to avoid AI FAB
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(gyms) { gym ->
                    GymInfoCard(gym = gym)
                }
            }
        }
    }
}

@Composable
fun GymInfoCard(gym: Gym) {
    HabfitCard(
        modifier = Modifier.width(260.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = gym.name, color = PrimaryText, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                Text(text = "⭐ ${gym.rating}", color = GoldReward, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = gym.address, color = SecondaryText, fontSize = 12.sp, maxLines = 1)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(PrimaryNeonGreen.copy(alpha = 0.1f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = gym.distance, color = PrimaryNeonGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = gym.openingHours, color = SecondaryText, fontSize = 11.sp)
            }
        }
    }
}
