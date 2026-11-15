package com.example.domain

enum class UserRole(val wire: String) {
    SUPER_OWNER("super_owner"),
    OWNER("owner"),
    ADMIN("admin");

    val label: String
        get() = when (this) {
            SUPER_OWNER -> "super owner"
            OWNER -> "owner"
            ADMIN -> "admin"
        }

    val isOwnerLevel: Boolean
        get() = this == SUPER_OWNER || this == OWNER

    companion object {
        fun fromWire(value: String?): UserRole =
            entries.firstOrNull { it.wire == value } ?: ADMIN
    }
}

enum class AccessStatus(val wire: String) {
    PENDING("pending"),
    ACTIVE("active"),
    REVOKED("revoked");

    companion object {
        fun fromWire(value: String?): AccessStatus =
            entries.firstOrNull { it.wire == value } ?: PENDING
    }
}

enum class LicenseStatus(val wire: String) {
    ACTIVE("active"),
    EXPIRED("expired"),
    REVOKED("revoked"),
    UNUSED("unused");

    companion object {
        fun fromWire(value: String?): LicenseStatus =
            entries.firstOrNull { it.wire == value } ?: UNUSED
    }
}

enum class DurationUnit(val label: String, val minutesMultiplier: Long) {
    MINUTES("minutes", 1L),
    HOURS("hours", 60L),
    DAYS("days", 1_440L),
    WEEKS("weeks", 10_080L),
    MONTHS("months", 43_200L);
}

enum class ReferralStatus(val wire: String) {
    ACTIVE("active"),
    REDEEMED("redeemed"),
    REVOKED("revoked");

    companion object {
        fun fromWire(value: String?): ReferralStatus =
            entries.firstOrNull { it.wire == value } ?: ACTIVE
    }
}

enum class AnnouncementDisplayMode(val wire: String, val label: String) {
    EVERY_LOGIN("every_login", "Every successful login"),
    ONCE_PER_DAY("once_per_day", "Once per day");

    companion object {
        fun fromWire(value: String?): AnnouncementDisplayMode =
            entries.firstOrNull { it.wire == value } ?: EVERY_LOGIN
    }
}

enum class AnnouncementTextSize(val wire: String, val label: String) {
    SMALL("small", "Small"),
    NORMAL("normal", "Normal"),
    LARGE("large", "Large"),
    FESTIVAL_LARGE("festival_large", "Festival / Large");

    companion object {
        fun fromWire(value: String?): AnnouncementTextSize =
            entries.firstOrNull { it.wire == value } ?: NORMAL
    }
}

enum class TelegramScopeStatus(val wire: String) {
    ACTIVE("active"),
    REVIEW("review");

    companion object {
        fun fromWire(value: String?): TelegramScopeStatus =
            entries.firstOrNull { it.wire == value } ?: ACTIVE
    }
}

enum class ValidationStatus(val wire: String, val defaultMessage: String) {
    VALID("valid", "License is valid for this device."),
    NOT_REGISTERED("not_registered", "License key is not registered."),
    INVALID("invalid", "License key has been revoked."),
    EXPIRED("expired", "License key has expired."),
    DEVICE_MISMATCH("device_mismatch", "License key is bound to a different device."),
    DEVICE_MAX_REACHED("device_max_reached", "Maximum device limit reached for this license.");
}

data class ValidationResponse(
    val success: Boolean,
    val status: ValidationStatus,
    val message: String,
    val expiresAtMillis: Long? = null
)

data class DashboardStats(
    val totalKeys: Int = 0,
    val activeKeys: Int = 0,
    val revokedKeys: Int = 0,
    val totalDeviceBindings: Int = 0
)

data class LicenseUiModel(
    val id: Long,
    val keyValue: String,
    val effectiveStatus: LicenseStatus,
    val storedStatus: LicenseStatus,
    val expiresAtMillis: Long?,
    val activatedAtMillis: Long?,
    val durationMinutes: Int?,
    val maxDevices: Int,
    val boundDeviceId: String?,
    val usedDevices: Int,
    val createdByUserId: Long?,
    val createdAtMillis: Long,
    val creatorDisplayName: String = "Legacy",
    val creatorRole: UserRole? = null
)

data class ReferralOverviewRow(
    val userId: Long,
    val creatorName: String,
    val role: UserRole,
    val total: Int,
    val active: Int,
    val redeemed: Int,
    val revoked: Int
)

data class TelegramChannelWithUser(
    val id: Long,
    val userId: Long,
    val username: String?,
    val role: UserRole,
    val channelUrl: String,
    val enabled: Boolean,
    val updatedAtMillis: Long
)

data class TelegramScopeCandidate(
    val referralId: Long,
    val referralCreatorId: Long,
    val accountUserId: Long?,
    val accountUsername: String?,
    val status: ReferralStatus,
    val redeemedAtMillis: Long?,
    val confirmedScopeStatus: TelegramScopeStatus? = null
)

data class TelegramSetupState(
    val eligible: Boolean,
    val hasSavedChannel: Boolean,
    val needsSetup: Boolean
)
