package com.habfit.app.features.community

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.habfit.app.domain.model.ContentPost
import com.habfit.app.domain.model.CreatorProfile
import com.habfit.app.ui.components.HabfitButton
import com.habfit.app.ui.components.HabfitCard
import com.habfit.app.ui.components.HabfitSectionTitle
import com.habfit.app.ui.components.HabfitTextField
import com.habfit.app.ui.theme.Background
import com.habfit.app.ui.theme.CardBackground
import com.habfit.app.ui.theme.ErrorColor
import com.habfit.app.ui.theme.PrimaryNeonGreen
import com.habfit.app.ui.theme.PrimaryText
import com.habfit.app.ui.theme.SecondaryText

@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel = hiltViewModel()
) {
    val posts by viewModel.posts.collectAsState()
    val creators by viewModel.creators.collectAsState()
    var showCreatePostDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Scaffold(
        containerColor = Background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreatePostDialog = true },
                containerColor = PrimaryNeonGreen,
                contentColor = Color.Black,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Post")
            }
        }
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
                modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
            )

            // Featured Creators Row
            if (creators.isNotEmpty()) {
                HabfitSectionTitle(title = "Featured Coaches")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    creators.forEach { creator ->
                        CreatorCard(
                            creator = creator,
                            onToggleFollow = { viewModel.toggleFollow(creator) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            HabfitSectionTitle(title = "Feed")

            if (posts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No community posts yet. Be the first to share your win!", color = SecondaryText)
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(posts, key = { it.id }) { post ->
                        PostItem(
                            post = post,
                            onLike = { viewModel.toggleLike(post) },
                            onShare = {
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "${post.title}\n\n${post.body}\n\nShared via HABFIT")
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share HABFIT Post")
                                context.startActivity(shareIntent)
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    if (showCreatePostDialog) {
        CreatePostDialog(
            onDismiss = { showCreatePostDialog = false },
            onConfirm = { title, body, tag ->
                viewModel.createPost(title, body, tag)
                showCreatePostDialog = false
            }
        )
    }
}

@Composable
fun CreatorCard(
    creator: CreatorProfile,
    onToggleFollow: () -> Unit
) {
    HabfitCard(modifier = Modifier.width(180.dp)) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(PrimaryNeonGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryNeonGreen)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = creator.name, color = PrimaryText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = creator.specialization, color = SecondaryText, fontSize = 11.sp, maxLines = 1)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (creator.isFollowed) Color.White.copy(alpha = 0.1f) else PrimaryNeonGreen)
                    .clickable { onToggleFollow() }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (creator.isFollowed) "Following" else "Follow",
                    color = if (creator.isFollowed) PrimaryText else Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun PostItem(
    post: ContentPost,
    onLike: () -> Unit,
    onShare: () -> Unit
) {
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = post.creatorName, color = PrimaryText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = "${post.creatorSpecialization} • ${post.timeAgo}", color = SecondaryText, fontSize = 11.sp)
                }
                if (post.tag.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrimaryNeonGreen.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = post.tag, color = PrimaryNeonGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            if (post.title.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = post.title, color = PrimaryText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = post.body,
                color = PrimaryText.copy(alpha = 0.9f),
                fontSize = 13.sp,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onLike() }
                ) {
                    Icon(
                        imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLiked) ErrorColor else SecondaryText,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${post.likesCount}",
                        color = if (post.isLiked) ErrorColor else SecondaryText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                IconButton(onClick = onShare, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = SecondaryText, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun CreatePostDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, body: String, tag: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("HabfitWin") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBackground,
        title = { Text("Share with Community", color = PrimaryText, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
        text = {
            Column {
                HabfitTextField(value = title, onValueChange = { title = it }, label = "Post Title (e.g. 7-Day Streak Achieved!)")
                Spacer(modifier = Modifier.height(12.dp))
                HabfitTextField(value = body, onValueChange = { body = it }, label = "What's on your mind? Share your workout win...")
                Spacer(modifier = Modifier.height(12.dp))
                HabfitTextField(value = tag, onValueChange = { tag = it }, label = "Tag (e.g. HabitWin, HIIT, Mobility)")
            }
        },
        confirmButton = {
            HabfitButton(
                text = "PUBLISH POST",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                onClick = {
                    if (body.isNotBlank()) {
                        onConfirm(title, body, tag)
                    }
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = SecondaryText) }
        }
    )
}
