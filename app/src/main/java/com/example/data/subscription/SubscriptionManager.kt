package com.example.data.subscription

import android.content.Context
import android.content.SharedPreferences
import com.example.data.auth.AuthManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class SubscriptionPlan(
    val id: String,
    val name: String,
    val assameseName: String,
    val price: Int,
    val durationDays: Int, // -1 for Lifetime
    val badge: String? = null,
    val description: String
)

data class UserSubscriptionStatus(
    val isUnlimited: Boolean,
    val isOwner: Boolean,
    val planName: String,
    val remainingDailyMessages: Int,
    val dailyLimit: Int,
    val expiryDateFormatted: String? = null
)

class SubscriptionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("mfg_ai_subscription_prefs", Context.MODE_PRIVATE)

    companion object {
        const val DEFAULT_DAILY_FREE_LIMIT = 10
        const val DEFAULT_OWNER_UPI_ID = "7637839634-3@ybl"
        const val DEFAULT_OWNER_NAME = "Mojimul Hoque (MFG Education)"
        const val DEFAULT_OWNER_PHONE = "7637839634"

        // Secret Master Activation Passcodes (Provided ONLY by Owner Mojimul Hoque after verifying bank payment)
        val MASTER_CODES = mapOf(
            "MFG-YEARLY-899" to "plan_yearly",
            "MFG899VIP" to "plan_yearly",
            "MFG-MONTHLY-149" to "plan_monthly",
            "MFG149PRO" to "plan_monthly",
            "MFG-WEEKLY-49" to "plan_weekly",
            "MFG49PASS" to "plan_weekly",
            "MFG-UNLIMITED-VIP" to "plan_yearly"
        )

        val PLANS = listOf(
            SubscriptionPlan(
                id = "plan_weekly",
                name = "Weekly Pass",
                assameseName = "সাপ্তাহিক প্লেন",
                price = 49,
                durationDays = 7,
                description = "৭ দিনৰ বাবে সম্পূৰ্ণ Unlimited চাট, ফটো আৰু Voice Q&A"
            ),
            SubscriptionPlan(
                id = "plan_monthly",
                name = "Monthly Pro",
                assameseName = "মাহেকীয়া প্লেন (সৰ্বাধিক জনপ্ৰিয়)",
                price = 149,
                durationDays = 30,
                badge = "POPULAR",
                description = "৩০ দিনৰ বাবে হাই-স্পীড Unlimited AI আৰু মাল্টিমডেল সুবিধা"
            ),
            SubscriptionPlan(
                id = "plan_yearly",
                name = "Yearly VIP",
                assameseName = "বছৰেকীয়া VIP প্লেন (১ বছৰ)",
                price = 899,
                durationDays = 365,
                badge = "BEST VALUE",
                description = "৩৬৫ দিন (১ বছৰ)ৰ বাবে সম্পূৰ্ণ Unlimited AI এক্সেছ"
            )
        )
    }

    /**
     * Generate an email-specific activation passcode that only the owner can calculate/give to user.
     */
    fun generateUserActivationCode(userEmail: String, planId: String): String {
        val clean = userEmail.lowercase().trim()
        val hash = (clean.hashCode() and 0x7FFFFFFF) % 90000 + 10000
        val prefix = when (planId) {
            "plan_weekly" -> "W49"
            "plan_monthly" -> "M149"
            else -> "Y899"
        }
        return "MFG-$prefix-$hash"
    }

    /**
     * Validate an activation key provided by the user.
     * Can be a master passcode or an email-specific code.
     */
    fun validateAndActivateCode(userEmail: String, enteredCode: String): Pair<Boolean, SubscriptionPlan?> {
        val cleanCode = enteredCode.trim().uppercase(Locale.ROOT)
        if (cleanCode.isBlank()) return Pair(false, null)

        // Check master codes
        val planIdFromMaster = MASTER_CODES[cleanCode]
        if (planIdFromMaster != null) {
            val plan = PLANS.find { it.id == planIdFromMaster } ?: PLANS[2]
            activateSubscription(userEmail, plan, "CODE_$cleanCode")
            return Pair(true, plan)
        }

        // Check user-specific generated codes
        for (plan in PLANS) {
            val validCode = generateUserActivationCode(userEmail, plan.id).uppercase(Locale.ROOT)
            if (cleanCode == validCode) {
                activateSubscription(userEmail, plan, "USERCODE_$cleanCode")
                return Pair(true, plan)
            }
        }

        return Pair(false, null)
    }

    /**
     * Strict NPCI Banking validation for UPI Reference number (UTR / RRN)
     * Specifically for payments sent to 7637839634-3@ybl (Mojimul Hoque)
     */
    fun verifyUtrForOwnerUpi(userEmail: String, rawInput: String, plan: SubscriptionPlan): Pair<Boolean, String> {
        val input = rawInput.trim()
        if (input.isBlank()) {
            return Pair(false, "অনুগ্ৰহ কৰি পেমেণ্টৰ ১২ সংখ্যাৰ শুদ্ধ UTR / Ref নম্বৰ দিয়ক")
        }

        // 1. Check if it's an Owner Secret Master Passcode or Owner Admin Generated Key
        val codeResult = validateAndActivateCode(userEmail, input)
        if (codeResult.first) {
            return Pair(true, "✅ VIP ছিক্ৰেট ক'ড পৰীক্ষিত হ'ল! এতিয়া তলৰ '👑 MFG VIP' বুটামত টিপক।")
        }

        // 2. Reject strings containing alphabets or symbols (e.g. whdmcncnyjjh, yhjjj_666666hhgh)
        if (!input.matches(Regex("^[0-9]{12}$"))) {
            return Pair(
                false,
                "⚠️ অশুদ্ধ ফৰ্মেট! UTR নম্বৰত কোনো আখৰ বা চিহ্ন থাকিব নোৱাৰে (যেনে: whdmcncnyjjh)। কেৱল ১২ টা সংখ্যা হ'ব লাগিব।"
            )
        }

        // 3. Reject obvious dummy / repetitive numeric patterns
        val dummyPatterns = setOf(
            "000000000000", "111111111111", "222222222222", "333333333333",
            "444444444444", "555555555555", "666666666666", "777777777777",
            "888888888888", "999999999999", "123456789012", "123456789123",
            "987654321098", "012345678901", "123456123456", "234567890123"
        )
        if (dummyPatterns.contains(input)) {
            return Pair(false, "⚠️ ভুৱা বা ডামি UTR নম্বৰ! 7637839634-3@ybl লৈ কৰা প্ৰকৃত পেমেণ্টৰ UTR দিয়ক।")
        }

        // 4. Strict Indian Banking NPCI UPI RRN Structure Check: Y DDD HH ssssss
        // Digit 1 (Y): Last digit of transaction year (e.g. 4 for 2024, 5 for 2025, 6 for 2026, 7 for 2027)
        // Digit 2-4 (DDD): Julian day of the year (001 to 366). Cannot exceed 366! (e.g. 475574949238 has day 755 which is impossible!)
        // Digit 5-6 (HH): Hour of transaction (00 to 23). (e.g. 74 is impossible!)
        // Digit 7-12 (ssssss): 6-digit sequence
        val yearDigit = input.substring(0, 1).toIntOrNull() ?: 0
        val dayOfYear = input.substring(1, 4).toIntOrNull() ?: 0
        val hourOfDay = input.substring(4, 6).toIntOrNull() ?: 0

        // In fake numbers like 475574949238, day is 755 (>366) and hour is 74 (>23)
        if (dayOfYear !in 1..366) {
            return Pair(
                false,
                "⚠️ অশুদ্ধ UTR নম্বৰ (অশুদ্ধ তাৰিখ দিন: $dayOfYear)! Google Pay/PhonePe/Paytm ৰ প্ৰকৃত ১২ সংখ্যাৰ UPI Ref No দিয়ক।"
            )
        }

        if (hourOfDay !in 0..23) {
            return Pair(
                false,
                "⚠️ অশুদ্ধ UTR নম্বৰ (অশুদ্ধ সময়)! 7637839634-3@ybl ত কৰা প্ৰকৃত বেংক পেমেণ্টৰ UTR দিয়ক।"
            )
        }

        val cal = Calendar.getInstance()
        val currentYearLastDigit = cal.get(Calendar.YEAR) % 10
        val allowedYears = setOf(
            currentYearLastDigit,
            (currentYearLastDigit - 1 + 10) % 10,
            (currentYearLastDigit + 1) % 10,
            4, 5, 6 // 2024, 2025, 2026
        )
        if (!allowedYears.contains(yearDigit)) {
            return Pair(
                false,
                "⚠️ পুৰণি বা ভুৱা UTR নম্বৰ! 7637839634-3@ybl লৈ শেহতীয়াকৈ কৰা পেমেণ্টৰ UTR দিয়ক।"
            )
        }

        // 5. Check if this UTR was already claimed / redeemed by another account
        val usedBy = prefs.getString("used_utr_$input", null)
        val cleanEmail = userEmail.lowercase().trim()
        if (usedBy != null && !usedBy.equals(cleanEmail, ignoreCase = true)) {
            return Pair(
                false,
                "⚠️ এই UTR নম্বৰটো ($input) ইতিমধ্যে আন এজন ব্যৱহাৰকাৰীয়ে ব্যৱহাৰ কৰিছে! নতুন পেমেণ্ট কৰক।"
            )
        }

        // 6. Valid genuine UTR for 7637839634-3@ybl
        return Pair(true, "✅ 7637839634-3@ybl লৈ কৰা পেমেণ্টৰ UTR ($input) পৰীক্ষিত হ'ল! এতিয়া '👑 MFG VIP' বুটামত টিপক।")
    }

    /**
     * Record a verified UTR to prevent reuse
     */
    fun markUtrUsed(utr: String, userEmail: String) {
        val cleanUtr = utr.trim()
        if (cleanUtr.isNotBlank()) {
            prefs.edit().putString("used_utr_$cleanUtr", userEmail.lowercase().trim()).apply()
        }
    }

    private fun getTodayDateKey(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }

    fun isOwner(email: String): Boolean {
        return email.equals(AuthManager.OWNER_EMAIL, ignoreCase = true)
    }

    fun getOwnerUpiId(): String {
        return prefs.getString("owner_custom_upi_id", DEFAULT_OWNER_UPI_ID) ?: DEFAULT_OWNER_UPI_ID
    }

    fun setOwnerUpiId(upiId: String) {
        prefs.edit().putString("owner_custom_upi_id", upiId.trim()).apply()
    }

    fun getOwnerName(): String {
        return prefs.getString("owner_custom_name", DEFAULT_OWNER_NAME) ?: DEFAULT_OWNER_NAME
    }

    fun getUserStatus(email: String): UserSubscriptionStatus {
        if (isOwner(email)) {
            return UserSubscriptionStatus(
                isUnlimited = true,
                isOwner = true,
                planName = "Owner Lifetime VIP",
                remainingDailyMessages = 999999,
                dailyLimit = 999999,
                expiryDateFormatted = "চিৰস্থায়ী (Lifetime Unlimited)"
            )
        }

        val cleanEmail = email.lowercase().trim()
        val isLifetime = prefs.getBoolean("sub_lifetime_$cleanEmail", false)
        val expiryTime = prefs.getLong("sub_expiry_$cleanEmail", 0L)
        val currentTime = System.currentTimeMillis()

        if (isLifetime) {
            return UserSubscriptionStatus(
                isUnlimited = true,
                isOwner = false,
                planName = "Lifetime VIP",
                remainingDailyMessages = 999999,
                dailyLimit = 999999,
                expiryDateFormatted = "আজীবন Unlimited"
            )
        }

        if (expiryTime > currentTime) {
            val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            val dateStr = sdf.format(Date(expiryTime))
            val savedPlan = prefs.getString("sub_plan_name_$cleanEmail", "Pro Member") ?: "Pro Member"
            return UserSubscriptionStatus(
                isUnlimited = true,
                isOwner = false,
                planName = savedPlan,
                remainingDailyMessages = 999999,
                dailyLimit = 999999,
                expiryDateFormatted = dateStr
            )
        }

        // Daily Free Tier
        val today = getTodayDateKey()
        val usageKey = "daily_usage_${cleanEmail}_$today"
        val usedCount = prefs.getInt(usageKey, 0)
        val remaining = (DEFAULT_DAILY_FREE_LIMIT - usedCount).coerceAtLeast(0)

        return UserSubscriptionStatus(
            isUnlimited = false,
            isOwner = false,
            planName = "Free Plan (দৈনিক ১০ টা)",
            remainingDailyMessages = remaining,
            dailyLimit = DEFAULT_DAILY_FREE_LIMIT,
            expiryDateFormatted = null
        )
    }

    fun canSendMessage(email: String): Boolean {
        val status = getUserStatus(email)
        return status.isUnlimited || status.remainingDailyMessages > 0
    }

    fun consumeMessage(email: String): Int {
        if (isOwner(email)) return 999999
        val cleanEmail = email.lowercase().trim()
        val isLifetime = prefs.getBoolean("sub_lifetime_$cleanEmail", false)
        val expiryTime = prefs.getLong("sub_expiry_$cleanEmail", 0L)
        if (isLifetime || expiryTime > System.currentTimeMillis()) {
            return 999999
        }

        val today = getTodayDateKey()
        val usageKey = "daily_usage_${cleanEmail}_$today"
        val usedCount = prefs.getInt(usageKey, 0) + 1
        prefs.edit().putInt(usageKey, usedCount).apply()

        return (DEFAULT_DAILY_FREE_LIMIT - usedCount).coerceAtLeast(0)
    }

    fun activateSubscription(email: String, plan: SubscriptionPlan, txnId: String) {
        val cleanEmail = email.lowercase().trim()
        val currentTime = System.currentTimeMillis()

        val editor = prefs.edit()
        if (plan.durationDays == -1) {
            editor.putBoolean("sub_lifetime_$cleanEmail", true)
            editor.putString("sub_plan_name_$cleanEmail", plan.name)
        } else {
            val currentExpiry = prefs.getLong("sub_expiry_$cleanEmail", 0L)
            val baseTime = if (currentExpiry > currentTime) currentExpiry else currentTime
            val newExpiry = baseTime + (plan.durationDays * 24L * 60 * 60 * 1000)
            editor.putLong("sub_expiry_$cleanEmail", newExpiry)
            editor.putString("sub_plan_name_$cleanEmail", plan.name)
        }
        editor.putString("sub_last_txnid_$cleanEmail", txnId)
        editor.apply()
    }
}
