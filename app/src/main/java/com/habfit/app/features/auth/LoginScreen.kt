package com.habfit.app.features.auth

import android.util.Log

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import com.habfit.app.BuildConfig
import com.habfit.app.ui.components.HabfitCard
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.habfit.app.ui.components.HabfitButton
import com.habfit.app.ui.components.HabfitTextField
import com.habfit.app.ui.theme.Background
import com.habfit.app.ui.theme.CardBackground
import com.habfit.app.ui.theme.ErrorColor
import com.habfit.app.ui.theme.PrimaryNeonGreen
import com.habfit.app.ui.theme.PrimaryText
import com.habfit.app.ui.theme.SecondaryText

@Composable
fun LoginScreen(
    onNavigateToSignup: () -> Unit,
    onLoginSuccess: (String) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val error by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Google Sign-In Launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (idToken != null) {
                viewModel.signInWithGoogle(idToken, onLoginSuccess)
            } else {
                viewModel.setErrorMessage("Google Sign-In failed: ID Token is null. Check Firebase Console.")
            }
        } catch (e: ApiException) {
            val message = when (e.statusCode) {
                7 -> "Network Error. Please check your internet connection."
                10 -> "Developer Error (10). This usually means the SHA-1 fingerprint or Web Client ID is incorrect in Firebase."
                12500 -> "Sign-in failed. Please update Google Play Services."
                12501 -> "Sign-in cancelled."
                else -> "Google Sign-In Error (${e.statusCode}): ${e.localizedMessage}"
            }
            if (e.statusCode != 12501) { // Don't show error if user just cancelled
                viewModel.setErrorMessage(message)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "HABFIT",
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            color = PrimaryNeonGreen,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Welcome Back",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryText
        )
        Text(
            text = "Your consistency journey continues",
            fontSize = 14.sp,
            color = SecondaryText
        )
        Spacer(modifier = Modifier.height(32.dp))

        if (error != null) {
            Text(
                text = error ?: "",
                color = ErrorColor,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        HabfitCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = CardBackground.copy(alpha = 0.6f)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HabfitTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = PrimaryNeonGreen) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                HabfitTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    isPassword = true,
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryNeonGreen) }
                )
                Spacer(modifier = Modifier.height(24.dp))

                if (isLoading) {
                    CircularProgressIndicator(color = PrimaryNeonGreen)
                } else {
                    HabfitButton(
                        text = "LOGIN",
                        onClick = {
                            viewModel.login(email, password, onLoginSuccess)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (!isLoading) {
            // Google Sign-In Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBackground)
                    .border(1.dp, SecondaryText.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .clickable {
                        if (BuildConfig.GOOGLE_WEB_CLIENT_ID == "YOUR_GOOGLE_WEB_CLIENT_ID_HERE") {
                            viewModel.setErrorMessage("Please set GOOGLE_WEB_CLIENT_ID in local.properties")
                            return@clickable
                        }
                        
                        // Diagnostic Logging
                        Log.d("HabfitAuth", "Starting Google Sign-In")
                        Log.d("HabfitAuth", "Package Name: ${context.packageName}")
                        Log.d("HabfitAuth", "Web Client ID: ${BuildConfig.GOOGLE_WEB_CLIENT_ID}")

                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                            .requestEmail()
                            .build()
                        val googleSignInClient = GoogleSignIn.getClient(context, gso)
                        
                        // Clear previous sign-in state to allow account selection every time
                        googleSignInClient.signOut().addOnCompleteListener {
                            googleSignInLauncher.launch(googleSignInClient.signInIntent)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = "Google Icon",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Continue with Google",
                        color = PrimaryText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        Row {
            Text(text = "Don't have an account? ", color = SecondaryText)
            Text(
                text = "Create Account",
                color = PrimaryNeonGreen,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToSignup() }
            )
        }
    }
}
