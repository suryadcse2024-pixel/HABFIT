package com.habfit.app.features.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habfit.app.ui.components.HabfitButton
import com.habfit.app.ui.theme.Background
import com.habfit.app.ui.theme.PrimaryNeonGreen
import com.habfit.app.ui.theme.PrimaryText
import com.habfit.app.ui.theme.SecondaryText
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { 5 })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            OnboardingPage(page)
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp)
        ) {
            HabfitButton(
                text = if (pagerState.currentPage == 4) "GENERATE MY HABFIT PLAN" else "NEXT",
                onClick = {
                    if (pagerState.currentPage < 4) {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        onFinished()
                    }
                }
            )
        }
    }
}

@Composable
fun OnboardingPage(pageIndex: Int) {
    val title = when (pageIndex) {
        0 -> "What's your main goal?"
        1 -> "Fitness Level"
        2 -> "Preferred Activities"
        3 -> "Daily Available Time"
        4 -> "Choose Your Habits"
        else -> ""
    }

    val subtitle = when (pageIndex) {
        0 -> "Help us personalize your experience"
        1 -> "Select where you are in your journey"
        2 -> "What do you enjoy doing most?"
        3 -> "How much time can you commit?"
        4 -> "Start with these basic habits"
        else -> ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp))
        Text(
            text = title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryText,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = subtitle,
            fontSize = 16.sp,
            color = SecondaryText,
            textAlign = TextAlign.Center
        )
        
        // Options would go here in a real implementation
        Spacer(modifier = Modifier.weight(1f))
    }
}
