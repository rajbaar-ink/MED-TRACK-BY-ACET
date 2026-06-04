package com.example.data

import com.google.firebase.auth.FirebaseAuthException

/**
 * Standardized utility to translate Firebase Authentication error codes
 * into highly descriptive, human-readable helper messages for UI alerts.
 */
object FirebaseErrorTranslator {
    
    fun translate(exception: Exception): String {
        if (exception is FirebaseAuthException) {
            return when (exception.errorCode) {
                "ERROR_INVALID_CUSTOM_TOKEN" -> 
                    "The custom token format is incorrect or has expired. Please try logging in again."
                "ERROR_CUSTOM_TOKEN_MISMATCH" -> 
                    "Authentication mismatch. The custom token corresponds to a different application audience."
                "ERROR_INVALID_CREDENTIAL" -> 
                    "Invalid login credentials provided. Please double-check your email and password."
                "ERROR_INVALID_EMAIL" -> 
                    "The email address is improperly formatted. Please use a valid email (e.g. name@domain.com)."
                "ERROR_WRONG_PASSWORD" -> 
                    "Incorrect credentials. The password is invalid or does not match."
                "ERROR_USER_NOT_FOUND" -> 
                    "No medical profile registered under this email. Please sign up to create an account."
                "ERROR_USER_DISABLED" -> 
                    "This medical user account has been disabled by a system administrator."
                "ERROR_TOO_MANY_REQUESTS" -> 
                    "Too many failed login attempts. We have temporarily blocked requests from this device. Please try again shortly."
                "ERROR_EMAIL_ALREADY_IN_USE" -> 
                    "This email address is already registered to another patient profile."
                "ERROR_WEAK_PASSWORD" -> 
                    "The chosen password is too weak. It must be at least 6 characters long."
                "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> 
                    "An account already exists with this email but using a different login method."
                "ERROR_OPERATION_NOT_ALLOWED" -> 
                    "Email and Password authentication is not enabled on this server. Contact our medical administrator."
                else -> 
                    exception.localizedMessage ?: "A server-side provider authentication error occurred (${exception.errorCode})."
            }
        }
        return exception.localizedMessage ?: "Unknown secure server-side connection integrity error."
    }
}
