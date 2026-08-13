package com.habfit.app.features.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.habfit.app.ui.components.HabfitButton
import com.habfit.app.ui.components.HabfitCard
import com.habfit.app.ui.components.HabfitTextField
import com.habfit.app.ui.theme.Background
import com.habfit.app.ui.theme.PrimaryNeonGreen
import com.habfit.app.ui.theme.PrimaryText
import com.habfit.app.ui.theme.PurpleAI
import com.habfit.app.ui.theme.SecondaryText

@Composable
fun AIScreen(
    viewModel: AIViewModel = hiltViewModel()
) {
    val response by viewModel.chatResponse.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var prompt by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "HABIT AI",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = PurpleAI
            )
            Text(
                text = "Your Personal AI Coach",
                color = SecondaryText,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(32.dp))

            HabfitCard(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.padding(16.dp)) {
                    if (isLoading) {
                        CircularProgressIndicator(color = PrimaryNeonGreen, modifier = Modifier.align(Alignment.Center))
                    } else if (response.isNotEmpty()) {
                        Text(text = response, color = PrimaryText)
                    } else {
                        Text(text = "Ask me anything about your fitness or habits!", color = SecondaryText)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            HabfitTextField(
                value = prompt,
                onValueChange = { prompt = it },
                label = "Ask your AI coach..."
            )
            Spacer(modifier = Modifier.height(16.dp))
            HabfitButton(
                text = "ASK AI",
                onClick = {
                    if (prompt.isNotEmpty()) {
                        viewModel.askAI(prompt)
                        prompt = ""
                    }
                }
            )
        }
    }
}
