package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.domain.LicenseValidationEngine

@Database(
    entities = [
        UserEntity::class,
        LicenseEntity::class,
        DeviceBindingEntity::class,
        ReferralEntity::class,
        AuditLogEntity::class,
        SystemAnnouncementEntity::class,
        ServerMaintenanceEntity::class,
        TelegramSettingsEntity::class,
        TelegramChannelEntity::class,
        TelegramAccountScopeEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LicenseXDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun licenseDao(): LicenseDao
    abstract fun deviceBindingDao(): DeviceBindingDao
    abstract fun referralDao(): ReferralDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun systemConfigDao(): SystemConfigDao
    abstract fun telegramDao(): TelegramDao

    suspend fun ensureSeeded() {
        if (userDao().countUsers() > 0) return

        val now = System.currentTimeMillis()
        val dayMs = 86_400_000L

        // Seeded Super Owner matching setup_owner.ts (NEXA / NEXA@2026)
        val superOwnerId = userDao().insertUser(
            UserEntity(
                openId = "local_super_owner_nexa",
                name = "NEXA",
                email = "nexa@licensex.local",
                username = "NEXA",
                passwordHash = LicenseValidationEngine.hashPassword("NEXA@2026"),
                role = "super_owner",
                accessStatus = "active",
                accessExpiresAtMillis = null,
                createdAtMillis = now - 14 * dayMs,
                lastSignedInMillis = now - 120_000L
            )
        )

        val ownerId = userDao().insertUser(
            UserEntity(
                openId = "local_owner_atlas",
                name = "ATLAS",
                email = "atlas@licensex.local",
                username = "ATLAS",
                passwordHash = LicenseValidationEngine.hashPassword("ATLAS@2026"),
                role = "owner",
                accessStatus = "active",
                accessExpiresAtMillis = now + 90 * dayMs,
                createdAtMillis = now - 10 * dayMs,
                lastSignedInMillis = now - 3_600_000L
            )
        )

        val adminId = userDao().insertUser(
            UserEntity(
                openId = "local_admin_nova",
                name = "NOVA",
                email = "nova@licensex.local",
                username = "NOVA",
                passwordHash = LicenseValidationEngine.hashPassword("NOVA@2026"),
                role = "admin",
                accessStatus = "active",
                accessExpiresAtMillis = now + 30 * dayMs,
                createdAtMillis = now - 5 * dayMs,
                lastSignedInMillis = now - 7_200_000L
            )
        )

        userDao().insertUser(
            UserEntity(
                openId = "local_pending_rookie",
                name = "ROOKIE",
                email = "rookie@licensex.local",
                username = null,
                passwordHash = LicenseValidationEngine.hashPassword("ROOKIE@2026"),
                role = "admin",
                accessStatus = "pending",
                createdAtMillis = now - dayMs,
                lastSignedInMillis = now - 14_400_000L
            )
        )

        // Seed licenses
        val activeHash1 = LicenseValidationEngine.hashDevice("PIXEL8PRO-9F1A2B3C")
        val license1Id = licenseDao().insertLicense(
            LicenseEntity(
                keyValue = "NEXA-9X4M2K8P7Q1R5T3V6W8Y2Z4B",
                status = "active",
                activatedAtMillis = now - 2 * dayMs,
                expiresAtMillis = now + 28 * dayMs,
                durationMinutes = 43_200,
                maxDevices = 2,
                boundDeviceId = activeHash1,
                createdByUserId = superOwnerId,
                createdAtMillis = now - 3 * dayMs
            )
        )
        deviceBindingDao().insertBinding(
            DeviceBindingEntity(
                licenseKeyId = license1Id,
                deviceHash = activeHash1,
                firstSeenAtMillis = now - 2 * dayMs,
                lastSeenAtMillis = now - 600_000L
            )
        )

        licenseDao().insertLicense(
            LicenseEntity(
                keyValue = "NEXA-7H3J9L2N4Q6S8U1W5Y3A9C7E",
                status = "unused",
                activatedAtMillis = null,
                expiresAtMillis = null,
                durationMinutes = 10_080,
                maxDevices = 1,
                createdByUserId = superOwnerId,
                createdAtMillis = now - dayMs
            )
        )

        licenseDao().insertLicense(
            LicenseEntity(
                keyValue = "ATLAS-VIP-2026",
                status = "revoked",
                activatedAtMillis = now - 7 * dayMs,
                expiresAtMillis = now + 23 * dayMs,
                durationMinutes = 43_200,
                maxDevices = 3,
                createdByUserId = ownerId,
                createdAtMillis = now - 8 * dayMs
            )
        )

        licenseDao().insertLicense(
            LicenseEntity(
                keyValue = "NOVA-4D6F8H2J5K7M9P3R5T7V9X2Z",
                status = "unused",
                activatedAtMillis = null,
                expiresAtMillis = null,
                durationMinutes = 1_440,
                maxDevices = 1,
                createdByUserId = adminId,
                createdAtMillis = now - 3_600_000L
            )
        )

        // Seed referrals
        referralDao().insertReferral(
            ReferralEntity(
                code = "REF-ADMIN-DEMO2026",
                role = "admin",
                createdByUserId = ownerId,
                redeemedByUserId = adminId,
                status = "redeemed",
                expiresAtMillis = now + 30 * dayMs,
                createdAtMillis = now - 6 * dayMs,
                redeemedAtMillis = now - 5 * dayMs
            )
        )
        referralDao().insertReferral(
            ReferralEntity(
                code = "REF-ACTIVE-OPEN2026",
                role = "admin",
                createdByUserId = superOwnerId,
                status = "active",
                expiresAtMillis = now + 30 * dayMs,
                createdAtMillis = now - 12 * 3_600_000L
            )
        )

        // Seed singleton system configuration rows
        systemConfigDao().upsertAnnouncement(
            SystemAnnouncementEntity(
                id = 1,
                version = 1,
                enabled = false,
                title = "SYSTEM ANNOUNCEMENT",
                message = "Welcome to LicenseX Control Panel. All validation nodes online.",
                displayMode = "every_login",
                textSize = "normal",
                updatedByUserId = superOwnerId
            )
        )
        systemConfigDao().upsertMaintenance(
            ServerMaintenanceEntity(
                id = 1,
                version = 1,
                enabled = false,
                title = "SERVER MAINTENANCE",
                message = "Server Under Maintenance. Please wait...",
                updatedByUserId = superOwnerId
            )
        )
        telegramDao().upsertSettings(
            TelegramSettingsEntity(
                id = 1,
                enabled = true,
                updatedByUserId = superOwnerId
            )
        )
        telegramDao().upsertChannel(
            TelegramChannelEntity(
                userId = superOwnerId,
                channelUrl = "https://t.me/licensex_updates",
                enabled = true
            )
        )
        telegramDao().upsertChannel(
            TelegramChannelEntity(
                userId = ownerId,
                channelUrl = "https://t.me/atlas_lua_channel",
                enabled = true
            )
        )

        auditLogDao().insertLog(
            AuditLogEntity(
                actorUserId = superOwnerId,
                actorName = "NEXA",
                action = "key_created",
                entityType = "license",
                entityId = license1Id,
                keyPreview = LicenseValidationEngine.maskKey("NEXA-9X4M2K8P7Q1R5T3V6W8Y2Z4B"),
                details = "durationMinutes=43200;activation=pending;maxDevices=2",
                createdAtMillis = now - 3 * dayMs
            )
        )
        auditLogDao().insertLog(
            AuditLogEntity(
                actorUserId = null,
                actorName = "system",
                action = "device_bound",
                entityType = "license",
                entityId = license1Id,
                keyPreview = LicenseValidationEngine.maskKey("NEXA-9X4M2K8P7Q1R5T3V6W8Y2Z4B"),
                details = "server-authoritative validation accepted",
                createdAtMillis = now - 2 * dayMs
            )
        )
    }

    companion object {
        @Volatile
        private var INSTANCE: LicenseXDatabase? = null

        fun getInstance(context: Context): LicenseXDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    LicenseXDatabase::class.java,
                    "licensex_panel.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
