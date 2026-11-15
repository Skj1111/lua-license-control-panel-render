package com.example.domain

import java.net.URI
import java.security.MessageDigest
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

sealed interface ValidationResult<out T> {
    data class Valid<T>(val value: T) : ValidationResult<T>
    data class Invalid(val error: String) : ValidationResult<Nothing>
}

data class KeyGenInput(
    val durationAmount: Int,
    val maxDevices: Int,
    val customKey: String?
)

data class KeyManageInput(
    val maxDevices: Int,
    val expiresAtMillis: Long?
)

object LicenseValidationEngine {
    const val USERNAME_CHANGE_COOLDOWN_MS = 72L * 60L * 60L * 1000L
    const val GENERATED_LICENSE_KEY_MAX_LENGTH = 50
    const val ANNOUNCEMENT_MESSAGE_MAX_LENGTH = 300
    const val ANNOUNCEMENT_TITLE_MAX_LENGTH = 80
    const val MAINTENANCE_MESSAGE_MAX_LENGTH = 300
    const val MAINTENANCE_TITLE_MAX_LENGTH = 80
    const val TELEGRAM_CHANNEL_MAX_LENGTH = 256
    const val LOCAL_PASSWORD_MIN_LENGTH = 8
    const val MAX_DURATION_MINUTES = 5_256_000L
    const val RECENT_AUDIT_LOG_LIMIT = 15

    private val USERNAME_REGEX = Regex("^[A-Za-z0-9](?:[A-Za-z0-9 _.-]{1,30}[A-Za-z0-9])?$")
    private val EXACT_CUSTOM_KEY_REGEX = Regex("^[A-Za-z0-9-]{4,64}$")
    private val TELEGRAM_PRIVATE_INVITE_REGEX = Regex("^/(?:\\+([A-Za-z0-9_-]+)|joinchat/([A-Za-z0-9_-]+))$", RegexOption.IGNORE_CASE)
    private val TELEGRAM_PUBLIC_PATH_REGEX = Regex("^/[A-Za-z0-9_][A-Za-z0-9_/-]*$")
    private val CONTROL_CHARS_REGEX = Regex("[\\u0000-\\u0008\\u000B\\u000C\\u000E-\\u001F\\u007F]")
    private val secureRandom = SecureRandom()
    private const val PANEL_HASH_SALT = "licensex-android-panel-shared-secret"

    fun validateLicenseGenerationInput(
        durationAmountRaw: String,
        durationUnit: DurationUnit,
        maxDevicesRaw: String,
        customEnabled: Boolean,
        customPrefixRaw: String
    ): ValidationResult<KeyGenInput> {
        val durationAmount = durationAmountRaw.trim().toIntOrNull()
            ?: return ValidationResult.Invalid("Enter a whole activation duration between 1 and 5,256,000.")
        if (durationAmount < 1 || durationAmount > 5_256_000) {
            return ValidationResult.Invalid("Enter a whole activation duration between 1 and 5,256,000.")
        }
        val totalMinutes = durationAmount.toLong() * durationUnit.minutesMultiplier
        if (totalMinutes > MAX_DURATION_MINUTES) {
            return ValidationResult.Invalid("License duration cannot exceed 10 years.")
        }

        val maxDevices = maxDevicesRaw.trim().toIntOrNull()
            ?: return ValidationResult.Invalid("Enter a whole device limit between 1 and 100.")
        if (maxDevices < 1 || maxDevices > 100) {
            return ValidationResult.Invalid("Enter a whole device limit between 1 and 100.")
        }

        val trimmedCustom = customPrefixRaw.trim()
        if (customEnabled && trimmedCustom.isEmpty()) {
            return ValidationResult.Invalid("Enter an exact custom key or disable custom key mode.")
        }
        if (customEnabled && !EXACT_CUSTOM_KEY_REGEX.matches(trimmedCustom)) {
            return ValidationResult.Invalid("Custom key must be 4–64 characters using only letters, numbers, and hyphens.")
        }

        return ValidationResult.Valid(
            KeyGenInput(
                durationAmount = durationAmount,
                maxDevices = maxDevices,
                customKey = if (customEnabled) trimmedCustom else null
            )
        )
    }

    fun validateLicenseManagementInput(
        maxDevicesRaw: String,
        requiresExpiry: Boolean,
        expiryRaw: String
    ): ValidationResult<KeyManageInput> {
        val maxDevices = maxDevicesRaw.trim().toIntOrNull()
            ?: return ValidationResult.Invalid("Enter a whole device limit between 1 and 100.")
        if (maxDevices < 1 || maxDevices > 100) {
            return ValidationResult.Invalid("Enter a whole device limit between 1 and 100.")
        }
        if (requiresExpiry) {
            val parsed = parseUtcOrLocalDateTime(expiryRaw)
                ?: return ValidationResult.Invalid("Enter a valid expiry date and time (yyyy-MM-dd HH:mm).")
            return ValidationResult.Valid(KeyManageInput(maxDevices = maxDevices, expiresAtMillis = parsed))
        }
        return ValidationResult.Valid(KeyManageInput(maxDevices = maxDevices, expiresAtMillis = null))
    }

    fun validateTelegramHttpsUrl(value: String): String? {
        val raw = value.trim()
        if (raw.isEmpty()) return "Enter a Telegram HTTPS channel URL"
        if (raw.length > TELEGRAM_CHANNEL_MAX_LENGTH) return "Telegram channel URL must be 256 characters or fewer"
        val uri = try {
            URI(raw)
        } catch (_: Exception) {
            return "Use a complete HTTPS link, for example https://t.me/channel"
        }
        val scheme = uri.scheme?.lowercase(Locale.US)
        val host = uri.host?.lowercase(Locale.US)
        if (scheme != "https" || host !in setOf("t.me", "telegram.me")) {
            return "Only HTTPS Telegram links from t.me or telegram.me are allowed"
        }
        if (!uri.rawQuery.isNullOrEmpty() || !uri.rawFragment.isNullOrEmpty()) {
            return "Telegram link must not contain a query or fragment"
        }
        val path = uri.path ?: ""
        if (path.isEmpty() || path == "/") {
            return "Telegram link must include a channel or invite path"
        }
        if (TELEGRAM_PRIVATE_INVITE_REGEX.matches(path)) return null
        if (path.startsWith("/+") || path.lowercase(Locale.US).startsWith("/joinchat")) {
            return "Telegram private invite link must contain a valid invite hash"
        }
        if (!TELEGRAM_PUBLIC_PATH_REGEX.matches(path)) {
            return "Telegram channel URL must contain a supported public or private invite path"
        }
        return null
    }

    fun normalizeTelegramChannelUrl(value: String): String {
        val error = validateTelegramHttpsUrl(value)
        if (error != null) throw IllegalArgumentException(error)
        val raw = value.trim()
        val uri = URI(raw)
        val path = uri.path ?: ""
        if (TELEGRAM_PRIVATE_INVITE_REGEX.matches(path)) return raw
        val cleanedPath = path.trimEnd('/')
        return "https://${uri.host!!.lowercase(Locale.US)}$cleanedPath"
    }

    fun normalizeUsername(value: String): String {
        val normalized = value.trim().replace(Regex("\\s+"), " ")
        if (normalized.length < 3 || normalized.length > 32 || !USERNAME_REGEX.matches(normalized)) {
            throw IllegalArgumentException(
                "Username must be 3–32 characters and may contain letters, numbers, spaces, dots, hyphens, or underscores"
            )
        }
        return normalized
    }

    fun usernameToKeyPrefix(username: String?): String {
        val cleaned = (username ?: "")
            .trim()
            .uppercase(Locale.US)
            .replace(Regex("\\s+"), "-")
            .replace(Regex("[^A-Z0-9-]"), "")
            .replace(Regex("-+"), "-")
            .trim('-')
            .take(24)
        return cleaned.ifEmpty { "LUA" }
    }

    fun normalizeKeyPrefix(prefix: String?): String {
        val cleaned = (prefix?.trim().takeUnless { it.isNullOrEmpty() } ?: "LUA")
            .replace(Regex("[^A-Za-z0-9_-]"), "-")
            .replace(Regex("[-_]+"), "-")
            .trim('-')
            .uppercase(Locale.US)
        if (cleaned.isEmpty() || cleaned.length > 24) {
            throw IllegalArgumentException("Custom key name must contain 1–24 letters or numbers")
        }
        return cleaned
    }

    fun generateLicenseKey(prefix: String = "LUA"): String {
        val safePrefix = normalizeKeyPrefix(prefix)
        val alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        val suffix = buildString(24) {
            repeat(24) {
                append(alphabet[secureRandom.nextInt(alphabet.length)])
            }
        }
        val availablePrefixLen = (GENERATED_LICENSE_KEY_MAX_LENGTH - suffix.length - 1).coerceAtLeast(1)
        return "${safePrefix.take(availablePrefixLen)}-$suffix"
    }

    fun generateReferralCode(): String {
        val alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        val suffix = buildString(20) {
            repeat(20) {
                append(alphabet[secureRandom.nextInt(alphabet.length)])
            }
        }
        return "REF-$suffix"
    }

    fun maskKey(key: String): String {
        val parts = key.split("-", limit = 2)
        val prefix = parts.getOrNull(0) ?: key
        val suffix = parts.getOrNull(1) ?: ""
        return "$prefix-****${suffix.takeLast(4)}"
    }

    fun hashDevice(deviceId: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest("$PANEL_HASH_SALT:${deviceId.trim()}".toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun hashPassword(password: String): String {
        val saltBytes = ByteArray(16).also { secureRandom.nextBytes(it) }
        val saltHex = saltBytes.joinToString("") { "%02x".format(it) }
        val spec = PBEKeySpec(password.toCharArray(), saltBytes, 65_536, 256)
        val derived = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
        val digestHex = derived.joinToString("") { "%02x".format(it) }
        return "pbkdf2$$saltHex$$digestHex"
    }

    fun verifyPassword(password: String, storedHash: String?): Boolean {
        if (storedHash.isNullOrBlank()) return false
        val parts = storedHash.split("$")
        if (parts.size != 3 || parts[0] != "pbkdf2") return false
        return try {
            val saltBytes = parts[1].chunked(2).map { it.toInt(16).toByte() }.toByteArray()
            val expected = parts[2].chunked(2).map { it.toInt(16).toByte() }.toByteArray()
            val spec = PBEKeySpec(password.toCharArray(), saltBytes, 65_536, expected.size * 8)
            val actual = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
            MessageDigest.isEqual(expected, actual)
        } catch (_: Exception) {
            false
        }
    }

    fun effectiveStatus(
        storedStatus: LicenseStatus,
        expiresAtMillis: Long?,
        nowMillis: Long = System.currentTimeMillis()
    ): LicenseStatus {
        return if (
            storedStatus != LicenseStatus.REVOKED &&
            (storedStatus == LicenseStatus.EXPIRED || (expiresAtMillis != null && expiresAtMillis <= nowMillis))
        ) {
            LicenseStatus.EXPIRED
        } else {
            storedStatus
        }
    }

    fun normalizeControlText(value: String, maxLength: Int, fieldLabel: String): String {
        val normalized = value.replace(CONTROL_CHARS_REGEX, "").trim()
        if (normalized.length > maxLength) {
            throw IllegalArgumentException("$fieldLabel cannot exceed $maxLength characters")
        }
        return normalized
    }

    fun formatDuration(minutes: Int?): String {
        if (minutes == null) return "Legacy fixed expiry"
        return when {
            minutes % 43_200 == 0 -> {
                val amount = minutes / 43_200
                "$amount ${if (amount == 1) "month" else "months"} (30 days/month)"
            }
            minutes % 10_080 == 0 -> {
                val amount = minutes / 10_080
                "$amount ${if (amount == 1) "week" else "weeks"}"
            }
            minutes % 1_440 == 0 -> {
                val amount = minutes / 1_440
                "$amount ${if (amount == 1) "day" else "days"}"
            }
            minutes % 60 == 0 -> {
                val amount = minutes / 60
                "$amount ${if (amount == 1) "hour" else "hours"}"
            }
            else -> "$minutes ${if (minutes == 1) "minute" else "minutes"}"
        }
    }

    fun formatTimestamp(millis: Long?): String {
        if (millis == null) return "—"
        val formatter = SimpleDateFormat("MMM d, yyyy · HH:mm", Locale.getDefault())
        return formatter.format(Date(millis))
    }

    fun formatInputDateTime(millis: Long?): String {
        if (millis == null) return ""
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        return formatter.format(Date(millis))
    }

    fun parseUtcOrLocalDateTime(raw: String): Long? {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return null
        val patterns = listOf("yyyy-MM-dd HH:mm", "yyyy-MM-dd'T'HH:mm", "yyyy-MM-dd")
        for (pattern in patterns) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.US).apply {
                    isLenient = false
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                val parsed = sdf.parse(trimmed)
                if (parsed != null) return parsed.time
            } catch (_: Exception) {
                // try next pattern
            }
        }
        return null
    }

    fun isWindowActive(
        enabled: Boolean,
        message: String,
        startAtMillis: Long?,
        endAtMillis: Long?,
        nowMillis: Long = System.currentTimeMillis()
    ): Boolean {
        return enabled &&
            message.isNotBlank() &&
            (startAtMillis == null || startAtMillis <= nowMillis) &&
            (endAtMillis == null || endAtMillis >= nowMillis)
    }
}
