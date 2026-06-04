package com.example.data

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

/**
 * Handles security authentication with the Firebase Console project:
 * Project URL Reference: https://console.firebase.google.com/project/acet-medtrack/overview
 */
class FirebaseAuthService(private val context: Context) {
    
    private val tag = "FirebaseAuthService"

    // Safe lazy initialization of Firebase Auth.
    // Programmatically references project 'acet-medtrack' using FirebaseOptions fallback if google-services.json is not configured natively.
    private val firebaseAuth: FirebaseAuth? by lazy {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                try {
                    FirebaseApp.initializeApp(context)
                } catch (ex: Exception) {
                    Log.w(tag, "Default config missing. Implementing programmatic fallback for project 'acet-medtrack'")
                    val options = com.google.firebase.FirebaseOptions.Builder()
                        .setApplicationId("1:366794442070:android:b96b08877e5109042402f7")
                        .setProjectId("acet-medtrack")
                        .setApiKey("AIzaSyCd0Q6ohw72UW41alpa2DyHOeommE1tdFA")
                        .setStorageBucket("acet-medtrack.firebasestorage.app")
                        .build()
                    FirebaseApp.initializeApp(context, options)
                }
            }
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.e(tag, "Firebase initialization error: ${e.message}")
            null
        }
    }

    /**
     * Check if Firebase client is ready
     */
    fun isFirebaseReady(): Boolean {
        return firebaseAuth != null
    }

    /**
     * Firebase user sign-up using Email/Password
     */
    suspend fun signUpWithFirebase(email: String, password: String): SupabaseAuthService.AuthResponse {
        val auth = firebaseAuth ?: return SupabaseAuthService.AuthResponse(
            success = false, 
            errorMessage = "Firebase is not configured yet. Download google-services.json from console.firebase.google.com and place it in your app module!"
        )

        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            SupabaseAuthService.AuthResponse(
                success = true,
                email = firebaseUser?.email ?: email,
                fullName = firebaseUser?.displayName ?: "MedTrack Patient",
                accessToken = firebaseUser?.uid
            )
        } catch (e: Exception) {
            Log.w(tag, "Firebase SignUp error", e)
            SupabaseAuthService.AuthResponse(
                success = false,
                errorMessage = "Firebase registration error: ${FirebaseErrorTranslator.translate(e)}"
            )
        }
    }

    /**
     * Firebase user login using Email/Password
     */
    suspend fun signInWithFirebase(email: String, password: String): SupabaseAuthService.AuthResponse {
        val auth = firebaseAuth ?: return SupabaseAuthService.AuthResponse(
            success = false, 
            errorMessage = "Firebase is not configured yet. Download google-services.json from console.firebase.google.com and place it in your app module!"
        )

        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            val token = firebaseUser?.uid // or firebaseUser?.getIdToken(true)?.await()?.token
            SupabaseAuthService.AuthResponse(
                success = true,
                email = firebaseUser?.email ?: email,
                fullName = firebaseUser?.displayName ?: "MedTrack Patient",
                accessToken = token
            )
        } catch (e: Exception) {
            Log.w(tag, "Firebase Login error", e)
            SupabaseAuthService.AuthResponse(
                success = false,
                errorMessage = "Firebase login authentication error: ${FirebaseErrorTranslator.translate(e)}"
            )
        }
    }

    /**
     * Signs out the current user session
     */
    fun signOut() {
        try {
            firebaseAuth?.signOut()
        } catch (e: Exception) {
            Log.e(tag, "Failed to sign out from Firebase", e)
        }
    }
}
