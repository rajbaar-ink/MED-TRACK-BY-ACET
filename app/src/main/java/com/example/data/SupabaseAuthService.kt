package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

/**
 * SupabaseAuthService handles the Email/Password authentication flow directly with Supabase's REST Auth engine.
 */
class SupabaseAuthService(
    private val supabaseUrl: String = "https://your-project-ref.supabase.co", // Fallback placeholder
    private val supabaseAnonKey: String = "your-supabase-anon-key"             // Fallback placeholder
) {
    private val client = OkHttpClient()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    data class AuthResponse(
        val success: Boolean,
        val email: String? = null,
        val fullName: String? = null,
        val accessToken: String? = null,
        val errorMessage: String? = null
    )

    /**
     * Authenticates a user with email and password (Login).
     */
    suspend fun signInWithEmail(email: String, password: String): AuthResponse = withContext(Dispatchers.IO) {
        val url = "$supabaseUrl/auth/v1/token?grant_type=password"
        
        val json = JSONObject().apply {
            put("email", email)
            put("password", password)
        }

        val body = json.toString().toRequestBody(jsonMediaType)
        
        val request = Request.Builder()
            .url(url)
            .addHeader("apikey", supabaseAnonKey)
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val bodyStr = response.body?.string()
                if (response.isSuccessful && !bodyStr.isNullOrEmpty()) {
                    val rootJson = JSONObject(bodyStr)
                    val accessToken = rootJson.optString("access_token", null)
                    val userObj = rootJson.optJSONObject("user")
                    val userEmail = userObj?.optString("email", email)
                    val metadata = userObj?.optJSONObject("user_metadata")
                    val fullName = metadata?.optString("full_name", "Resident Patient")

                    AuthResponse(
                        success = true,
                        email = userEmail,
                        fullName = fullName,
                        accessToken = accessToken
                    )
                } else {
                    val errorMsg = parseErrorMessage(bodyStr)
                    AuthResponse(success = false, errorMessage = errorMsg)
                }
            }
        } catch (e: IOException) {
            AuthResponse(success = false, errorMessage = "Network offline or server unreachable: ${e.message}")
        } catch (e: Exception) {
            AuthResponse(success = false, errorMessage = "Authentication failure: ${e.message}")
        }
    }

    /**
     * Standard sign-up for a new user with email and password in Supabase.
     */
    suspend fun signUpWithEmail(email: String, password: String, fullName: String): AuthResponse = withContext(Dispatchers.IO) {
        val url = "$supabaseUrl/auth/v1/signup"
        
        val userMetadata = JSONObject().apply {
            put("full_name", fullName)
        }
        val json = JSONObject().apply {
            put("email", email)
            put("password", password)
            put("data", userMetadata)
        }

        val body = json.toString().toRequestBody(jsonMediaType)
        
        val request = Request.Builder()
            .url(url)
            .addHeader("apikey", supabaseAnonKey)
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val bodyStr = response.body?.string()
                if (response.isSuccessful && !bodyStr.isNullOrEmpty()) {
                    val rootJson = JSONObject(bodyStr)
                    val userObj = rootJson.optJSONObject("user")
                    val userEmail = userObj?.optString("email", email)
                    val metadata = userObj?.optJSONObject("user_metadata")
                    val name = metadata?.optString("full_name", fullName)
                    
                    AuthResponse(
                        success = true,
                        email = userEmail,
                        fullName = name,
                        errorMessage = "Registration complete! Please check email folder to verify your credentials if confirmation is active."
                    )
                } else {
                    val errorMsg = parseErrorMessage(bodyStr)
                    AuthResponse(success = false, errorMessage = errorMsg)
                }
            }
        } catch (e: IOException) {
            AuthResponse(success = false, errorMessage = "Network offline or server unreachable: ${e.message}")
        } catch (e: Exception) {
            AuthResponse(success = false, errorMessage = "Registration failure: ${e.message}")
        }
    }

    private fun parseErrorMessage(responseBody: String?): String {
        if (responseBody.isNullOrEmpty()) return "Unknown remote authentication authority error."
        return try {
            val json = JSONObject(responseBody)
            json.optString("error_description", json.optString("msg", "Action unauthorized by database safety policies."))
        } catch (e: Exception) {
            "Error code status received from authentication server request: $responseBody"
        }
    }
}
