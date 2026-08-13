package com.habfit.app.features.community

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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.habfit.app.ui.components.HabfitCard
import com.habfit.app.ui.theme.Background
import com.habfit.app.ui.theme.CardBackground
import com.habfit.app.ui.theme.PrimaryNeonGreen
import com.habfit.app.ui.theme.PrimaryText
import com.habfit.app.ui.theme.SecondaryText

@Composable
fun CommunityScreen() {
    Scaffold(
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "COMMUNITY",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            LazyColumn {
                items(5) {
                    PostItem()
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun PostItem() {
    HabfitCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = SecondaryText)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "FitnessPro", color = PrimaryText, fontWeight = FontWeight.Bold)
                    Text(text = "2 hours ago", color = SecondaryText, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Just finished a killer morning HIIT session! Feeling energized. #Habfit #Consistency",
                color = PrimaryText,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            // Placeholder for image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Post Image Placeholder", color = SecondaryText)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row {
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Red.copy(alpha = 0.6f))
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = SecondaryText)
                }
            }
        }
    }
}
