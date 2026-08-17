package com.habfit.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habfit.app.domain.model.Habit
import com.habfit.app.domain.model.Workout
import com.habfit.app.ui.theme.CardBackground
import com.habfit.app.ui.theme.ErrorColor
import com.habfit.app.ui.theme.PrimaryNeonGreen
import com.habfit.app.ui.theme.PrimaryText
import com.habfit.app.ui.theme.SecondaryText

@Composable
fun HabfitCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = CardBackground,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        content()
    }
}

@Composable
fun HabfitButton(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = PrimaryNeonGreen,
    contentColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@Composable
fun HabfitTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        leadingIcon = leadingIcon,
        trailingIcon = if (isPassword) {
            {
                val image = if (passwordVisible)
                    Icons.Default.Visibility
                else Icons.Default.VisibilityOff

                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description, tint = SecondaryText)
                }
            }
        } else null,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryNeonGreen,
            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
            focusedLabelColor = PrimaryNeonGreen,
            unfocusedLabelColor = SecondaryText,
            focusedTextColor = PrimaryText,
            unfocusedTextColor = PrimaryText,
            cursorColor = PrimaryNeonGreen
        ),
        singleLine = true,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = keyboardOptions
    )
}

@Composable
fun HabfitSectionTitle(
    modifier: Modifier = Modifier,
    title: String
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = PrimaryText,
        modifier = modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun HabfitGoalProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = PrimaryNeonGreen
) {
    val clampedProgress = progress.coerceIn(0f, 1f)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(3.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val progressWidth = canvasWidth * clampedProgress
            
            if (progressWidth > 0) {
                drawRoundRect(
                    color = color,
                    size = androidx.compose.ui.geometry.Size(progressWidth, canvasHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(canvasHeight / 2, canvasHeight / 2)
                )
                
                if (clampedProgress < 1f) {
                    drawCircle(
                        color = color,
                        radius = 3.dp.toPx(),
                        center = androidx.compose.ui.geometry.Offset(progressWidth, canvasHeight / 2)
                    )
                }
            }
        }
    }
}

@Composable
fun HabfitStatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    subValue: String? = null
) {
    HabfitCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = label, color = SecondaryText, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = PrimaryText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            subValue?.let {
                Text(text = it, color = PrimaryNeonGreen, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun HabitCard(
    habit: Habit,
    isCompleted: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    val accentColor = remember(habit.category) {
        when (habit.category.lowercase()) {
            "health" -> Color(0xFF4D91FF)
            "fitness" -> PrimaryNeonGreen
            "mind" -> Color(0xFFFFD700)
            "routine" -> Color(0xFFA55BFF)
            "food" -> Color(0xFFFF8C00)
            else -> SecondaryText
        }
    }

    HabfitCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = CardBackground
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getHabitIcon(habit.category, habit.name),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.name,
                    color = PrimaryText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔥 ${habit.streak}", color = PrimaryNeonGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = habit.target, color = SecondaryText, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { if (isCompleted) 1f else 0.3f },
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(4.dp)
                        .clip(CircleShape),
                    color = accentColor,
                    trackColor = Color.White.copy(alpha = 0.05f),
                    strokeCap = StrokeCap.Round
                )
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More", tint = SecondaryText)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(CardBackground)
                ) {
                    DropdownMenuItem(
                        text = { Text("Delete", color = ErrorColor) },
                        onClick = {
                            onDelete()
                            showMenu = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted) PrimaryNeonGreen else Color.White.copy(alpha = 0.08f))
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Default.Check else Icons.Default.Add,
                    contentDescription = if (isCompleted) "Completed" else "Complete",
                    tint = if (isCompleted) Color.Black else PrimaryText,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun WorkoutHistoryItem(
    workout: Workout,
    onDelete: () -> Unit = {}
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

fun getHabitIcon(category: String, name: String = ""): ImageVector {
    val n = name.lowercase()
    return when {
        n.contains("water") || n.contains("drink") || category.lowercase() == "health" -> Icons.Default.WaterDrop
        n.contains("exercise") || n.contains("gym") || n.contains("workout") || category.lowercase() == "fitness" -> Icons.Default.FitnessCenter
        n.contains("meditation") || n.contains("yoga") || category.lowercase() == "mind" -> Icons.Default.SelfImprovement
        n.contains("read") || n.contains("book") -> Icons.AutoMirrored.Filled.MenuBook
        n.contains("sleep") || n.contains("bed") -> Icons.Default.Hotel
        n.contains("walk") || n.contains("step") -> Icons.AutoMirrored.Filled.DirectionsWalk
        n.contains("food") || n.contains("eat") || category.lowercase() == "food" -> Icons.Default.Fastfood
        else -> Icons.AutoMirrored.Filled.DirectionsRun
    }
}

fun getWorkoutIcon(type: String): ImageVector {
    return when (type.lowercase()) {
        "running" -> Icons.AutoMirrored.Filled.DirectionsRun
        "cycling" -> Icons.AutoMirrored.Filled.DirectionsWalk // Using walk as fallback for bike if not mirrored
        "walking" -> Icons.AutoMirrored.Filled.DirectionsWalk
        "yoga" -> Icons.Default.SelfImprovement
        else -> Icons.Default.FitnessCenter
    }
}
