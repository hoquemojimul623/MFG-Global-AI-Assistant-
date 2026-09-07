package com.example.data.auth

import android.content.Context
import android.content.SharedPreferences
import com.example.data.security.SecurityGuard

data class UserProfile(
    val name: String,
    val email: String,
    val isOwner: Boolean = false
)

class AuthManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("mfg_education_auth_prefs", Context.MODE_PRIVATE)

    companion object {
        const val OWNER_EMAIL = "mojimulk2@gmail.com"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USERS_PREFIX = "user_pwd_hash_"
        private const val KEY_USER_NAME_PREFIX = "user_fullname_"
        private const val KEY_LOGIN_ATTEMPTS_PREFIX = "login_attempts_"
        private const val KEY_LOCKOUT_TIME_PREFIX = "lockout_time_"
        private const val MAX_FAILED_ATTEMPTS = 5
        private const val LOCKOUT_DURATION_MS = 60 * 1000L // 1 minute lockout
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUserProfile(): UserProfile? {
        if (!isLoggedIn()) return null
        val name = prefs.getString(KEY_USER_NAME, "User") ?: "User"
        val email = prefs.getString(KEY_USER_EMAIL, "") ?: ""
        val isOwner = email.equals(OWNER_EMAIL, ignoreCase = true)
        return UserProfile(name = name, email = email, isOwner = isOwner)
    }

    fun register(fullName: String, email: String, password: String): Pair<Boolean, String> {
        val cleanEmail = SecurityGuard.sanitizeInput(email.lowercase())
        val cleanName = SecurityGuard.sanitizeInput(fullName)
        val cleanPwd = password.trim()

        if (cleanEmail.isEmpty() || cleanPwd.isEmpty() || cleanName.isEmpty()) {
            return Pair(false, "অনুগ্রহ কৰি আটাইকেইটা ফিল্ড পূৰণ কৰক।")
        }
        if (cleanPwd.length < 6) {
            return Pair(false, "পাছৱৰ্ড অন্ততঃ ৬ টা আখৰৰ হ'ব লাগিব (নিৰাপত্তাৰ স্বাৰ্থত)।")
        }

        val existingUser = prefs.getString("$KEY_USERS_PREFIX$cleanEmail", null)
        if (existingUser != null) {
            return Pair(false, "এই ইমেইলটো ইতিমধ্যে ৰেজিস্টাৰ কৰা হৈছে। লগইন কৰক।")
        }

        // SHA-256 cryptographic salted hash for military-grade protection
        val hashedPassword = SecurityGuard.hashPassword(cleanPwd, cleanEmail)

        prefs.edit()
            .putString("$KEY_USERS_PREFIX$cleanEmail", hashedPassword)
            .putString("$KEY_USER_NAME_PREFIX$cleanEmail", cleanName)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_NAME, cleanName)
            .putString(KEY_USER_EMAIL, cleanEmail)
            .apply()

        return Pair(true, "একাউণ্ট সফলতাৰে আৰু সম্পূৰ্ণ সুৰক্ষিতভাৱে তৈয়াৰ কৰা হ'ল!")
    }

    fun login(email: String, password: String): Pair<Boolean, String> {
        val cleanEmail = SecurityGuard.sanitizeInput(email.lowercase())
        val cleanPwd = password.trim()

        if (cleanEmail.isEmpty() || cleanPwd.isEmpty()) {
            return Pair(false, "ইমেইল আৰু পাছৱৰ্ড লিখক।")
        }

        // Check Brute Force Protection Lockout
        val lockoutUntil = prefs.getLong("$KEY_LOCKOUT_TIME_PREFIX$cleanEmail", 0L)
        val currentTime = System.currentTimeMillis()
        if (currentTime < lockoutUntil) {
            val remainingSec = (lockoutUntil - currentTime) / 1000
            return Pair(false, "সুৰক্ষা সতৰ্কতা: একাধিক ভুল প্ৰচেষ্টা! $remainingSec ছেকেণ্ড অপেক্ষা কৰক।")
        }

        val savedPasswordHash = prefs.getString("$KEY_USERS_PREFIX$cleanEmail", null)
        if (savedPasswordHash == null) {
            return Pair(false, "এই ইমেইলৰ কোনো একাউন্ট পোৱা নগ'ল। ৰেজিস্টাৰ কৰক।")
        }

        val inputHash = SecurityGuard.hashPassword(cleanPwd, cleanEmail)

        if (savedPasswordHash != inputHash) {
            val currentAttempts = prefs.getInt("$KEY_LOGIN_ATTEMPTS_PREFIX$cleanEmail", 0) + 1
            if (currentAttempts >= MAX_FAILED_ATTEMPTS) {
                // Lock account for 1 minute against brute-force attacks
                prefs.edit()
                    .putLong("$KEY_LOCKOUT_TIME_PREFIX$cleanEmail", currentTime + LOCKOUT_DURATION_MS)
                    .putInt("$KEY_LOGIN_ATTEMPTS_PREFIX$cleanEmail", 0)
                    .apply()
                return Pair(false, "সুৰক্ষা সতৰ্কতা: ৫ বাৰ ভুল পাছৱৰ্ড দিয়াৰ বাবে একাউণ্ট ১ মিনিটৰ বাবে লক কৰা হ'ল।")
            } else {
                prefs.edit().putInt("$KEY_LOGIN_ATTEMPTS_PREFIX$cleanEmail", currentAttempts).apply()
                val attemptsLeft = MAX_FAILED_ATTEMPTS - currentAttempts
                return Pair(false, "ভুল পাছৱৰ্ড! (বাকী প্ৰচেষ্টা: $attemptsLeft বাৰ)")
            }
        }

        // Reset failed attempts upon successful login
        val savedName = prefs.getString("$KEY_USER_NAME_PREFIX$cleanEmail", "User") ?: "User"

        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_NAME, savedName)
            .putString(KEY_USER_EMAIL, cleanEmail)
            .putInt("$KEY_LOGIN_ATTEMPTS_PREFIX$cleanEmail", 0)
            .remove("$KEY_LOCKOUT_TIME_PREFIX$cleanEmail")
            .apply()

        return Pair(true, "লগইন সফল আৰু সুৰক্ষিত হ'ল!")
    }

    fun logout() {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .remove(KEY_USER_NAME)
            .remove(KEY_USER_EMAIL)
            .apply()
    }
}
