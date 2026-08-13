package com.habfit.app.domain.repository

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: FirebaseUser?
    fun authStateFlow(): Flow<FirebaseUser?>
    
    suspend fun login(email: String, password: String): Result<FirebaseUser?>
    suspend fun signup(name: String, email: String, password: String): Result<FirebaseUser?>
    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser?>
    suspend fun logout()
    suspend fun resetPassword(email: String): Result<Unit>
}
