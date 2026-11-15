package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["openId"], unique = true),
        Index(value = ["username"], unique = true)
    ]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val openId: String,
    val name: String? = null,
    val email: String? = null,
    val username: String? = null,
    val passwordHash: String? = null,
    val usernameChangedAtMillis: Long? = null,
    val loginMethod: String = "password",
    val role: String = "admin",
    val accessStatus: String = "pending",
    val accessExpiresAtMillis: Long? = null,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val updatedAtMillis: Long = System.currentTimeMillis(),
    val lastSignedInMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "licenses",
    indices = [Index(value = ["keyValue"], unique = true)]
)
data class LicenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val keyValue: String,
    val status: String = "unused",
    val expiresAtMillis: Long? = null,
    val activatedAtMillis: Long? = null,
    val durationMinutes: Int? = null,
    val maxDevices: Int = 1,
    val boundDeviceId: String? = null,
    val createdByUserId: Long? = null,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val updatedAtMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "device_bindings",
    indices = [
        Index(value = ["deviceHash"], unique = true),
        Index(value = ["licenseKeyId"])
    ]
)
data class DeviceBindingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val licenseKeyId: Long,
    val deviceHash: String,
    val firstSeenAtMillis: Long = System.currentTimeMillis(),
    val lastSeenAtMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "referrals",
    indices = [Index(value = ["code"], unique = true)]
)
data class ReferralEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val role: String,
    val createdByUserId: Long,
    val redeemedByUserId: Long? = null,
    val status: String = "active",
    val expiresAtMillis: Long,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val redeemedAtMillis: Long? = null
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val actorUserId: Long? = null,
    val actorName: String? = null,
    val action: String,
    val entityType: String,
    val entityId: Long? = null,
    val keyPreview: String? = null,
    val details: String? = null,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "system_announcements")
data class SystemAnnouncementEntity(
    @PrimaryKey val id: Int = 1,
    val version: Int = 1,
    val enabled: Boolean = false,
    val title: String = "SYSTEM ANNOUNCEMENT",
    val message: String = "",
    val startAtMillis: Long? = null,
    val endAtMillis: Long? = null,
    val displayMode: String = "every_login",
    val textSize: String = "normal",
    val updatedByUserId: Long? = null,
    val updatedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "server_maintenance")
data class ServerMaintenanceEntity(
    @PrimaryKey val id: Int = 1,
    val version: Int = 1,
    val enabled: Boolean = false,
    val title: String = "SERVER MAINTENANCE",
    val message: String = "",
    val startAtMillis: Long? = null,
    val endAtMillis: Long? = null,
    val updatedByUserId: Long? = null,
    val updatedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "telegram_settings")
data class TelegramSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val enabled: Boolean = false,
    val updatedByUserId: Long? = null,
    val updatedAtMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telegram_channels",
    indices = [Index(value = ["userId"], unique = true)]
)
data class TelegramChannelEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val channelUrl: String,
    val enabled: Boolean = true,
    val updatedAtMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "telegram_account_scopes",
    indices = [Index(value = ["accountUserId"], unique = true)]
)
data class TelegramAccountScopeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ownerUserId: Long,
    val accountUserId: Long,
    val sourceReferralId: Long? = null,
    val status: String = "active",
    val updatedAtMillis: Long = System.currentTimeMillis()
)
