package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY lastSignedInMillis DESC")
    fun observeAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users ORDER BY lastSignedInMillis DESC")
    suspend fun getAllUsers(): List<UserEntity>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun observeUserById(id: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(username) = LOWER(:username) LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT COUNT(*) FROM users")
    suspend fun countUsers(): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUserById(id: Long)
}

@Dao
interface LicenseDao {
    @Query("SELECT * FROM licenses ORDER BY createdAtMillis DESC")
    fun observeAllLicenses(): Flow<List<LicenseEntity>>

    @Query("SELECT * FROM licenses WHERE createdByUserId = :userId ORDER BY createdAtMillis DESC")
    fun observeLicensesByCreator(userId: Long): Flow<List<LicenseEntity>>

    @Query("SELECT * FROM licenses ORDER BY createdAtMillis DESC")
    suspend fun getAllLicenses(): List<LicenseEntity>

    @Query("SELECT * FROM licenses WHERE id = :id LIMIT 1")
    suspend fun getLicenseById(id: Long): LicenseEntity?

    @Query("SELECT * FROM licenses WHERE UPPER(keyValue) = UPPER(:keyValue) LIMIT 1")
    suspend fun getLicenseByKey(keyValue: String): LicenseEntity?

    @Query("SELECT COUNT(*) FROM licenses")
    suspend fun countAllLicenses(): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertLicense(license: LicenseEntity): Long

    @Update
    suspend fun updateLicense(license: LicenseEntity)

    @Query("UPDATE licenses SET createdByUserId = NULL WHERE createdByUserId = :userId")
    suspend fun clearCreatorForUser(userId: Long)

    @Query("DELETE FROM licenses WHERE id = :id")
    suspend fun deleteLicenseById(id: Long)

    @Query("DELETE FROM licenses")
    suspend fun deleteAllLicenses(): Int
}

@Dao
interface DeviceBindingDao {
    @Query("SELECT * FROM device_bindings ORDER BY firstSeenAtMillis DESC")
    fun observeAllBindings(): Flow<List<DeviceBindingEntity>>

    @Query("SELECT * FROM device_bindings WHERE licenseKeyId = :licenseId ORDER BY firstSeenAtMillis DESC")
    fun observeBindingsForLicense(licenseId: Long): Flow<List<DeviceBindingEntity>>

    @Query("SELECT * FROM device_bindings WHERE licenseKeyId = :licenseId ORDER BY firstSeenAtMillis DESC")
    suspend fun getBindingsForLicense(licenseId: Long): List<DeviceBindingEntity>

    @Query("SELECT * FROM device_bindings WHERE deviceHash = :deviceHash LIMIT 1")
    suspend fun getBindingByDeviceHash(deviceHash: String): DeviceBindingEntity?

    @Query("SELECT * FROM device_bindings WHERE id = :id LIMIT 1")
    suspend fun getBindingById(id: Long): DeviceBindingEntity?

    @Query("SELECT COUNT(*) FROM device_bindings WHERE licenseKeyId = :licenseId")
    suspend fun countBindingsForLicense(licenseId: Long): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertBinding(binding: DeviceBindingEntity): Long

    @Update
    suspend fun updateBinding(binding: DeviceBindingEntity)

    @Query("DELETE FROM device_bindings WHERE id = :id")
    suspend fun deleteBindingById(id: Long)

    @Query("DELETE FROM device_bindings WHERE licenseKeyId = :licenseId")
    suspend fun deleteBindingsForLicense(licenseId: Long)

    @Query("DELETE FROM device_bindings")
    suspend fun deleteAllBindings()
}

@Dao
interface ReferralDao {
    @Query("SELECT * FROM referrals ORDER BY createdAtMillis DESC")
    fun observeAllReferrals(): Flow<List<ReferralEntity>>

    @Query("SELECT * FROM referrals ORDER BY createdAtMillis DESC")
    suspend fun getAllReferrals(): List<ReferralEntity>

    @Query("SELECT * FROM referrals WHERE id = :id LIMIT 1")
    suspend fun getReferralById(id: Long): ReferralEntity?

    @Query("SELECT * FROM referrals WHERE UPPER(code) = UPPER(:code) LIMIT 1")
    suspend fun getReferralByCode(code: String): ReferralEntity?

    @Query("SELECT COUNT(*) FROM referrals")
    suspend fun countAllReferrals(): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertReferral(referral: ReferralEntity): Long

    @Update
    suspend fun updateReferral(referral: ReferralEntity)

    @Query("DELETE FROM referrals WHERE id = :id")
    suspend fun deleteReferralById(id: Long)

    @Query("DELETE FROM referrals WHERE createdByUserId = :userId")
    suspend fun deleteReferralsByCreator(userId: Long)

    @Query("DELETE FROM referrals")
    suspend fun deleteAllReferrals(): Int
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY createdAtMillis DESC LIMIT :limit")
    fun observeRecentLogs(limit: Int = 15): Flow<List<AuditLogEntity>>

    @Insert
    suspend fun insertLog(log: AuditLogEntity): Long
}

@Dao
interface SystemConfigDao {
    @Query("SELECT * FROM system_announcements WHERE id = 1 LIMIT 1")
    fun observeAnnouncement(): Flow<SystemAnnouncementEntity?>

    @Query("SELECT * FROM system_announcements WHERE id = 1 LIMIT 1")
    suspend fun getAnnouncement(): SystemAnnouncementEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAnnouncement(announcement: SystemAnnouncementEntity)

    @Query("SELECT * FROM server_maintenance WHERE id = 1 LIMIT 1")
    fun observeMaintenance(): Flow<ServerMaintenanceEntity?>

    @Query("SELECT * FROM server_maintenance WHERE id = 1 LIMIT 1")
    suspend fun getMaintenance(): ServerMaintenanceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMaintenance(maintenance: ServerMaintenanceEntity)
}

@Dao
interface TelegramDao {
    @Query("SELECT * FROM telegram_settings WHERE id = 1 LIMIT 1")
    fun observeSettings(): Flow<TelegramSettingsEntity?>

    @Query("SELECT * FROM telegram_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): TelegramSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSettings(settings: TelegramSettingsEntity)

    @Query("SELECT * FROM telegram_channels ORDER BY updatedAtMillis DESC")
    fun observeAllChannels(): Flow<List<TelegramChannelEntity>>

    @Query("SELECT * FROM telegram_channels WHERE userId = :userId LIMIT 1")
    fun observeChannelForUser(userId: Long): Flow<TelegramChannelEntity?>

    @Query("SELECT * FROM telegram_channels WHERE userId = :userId LIMIT 1")
    suspend fun getChannelForUser(userId: Long): TelegramChannelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertChannel(channel: TelegramChannelEntity)

    @Query("SELECT * FROM telegram_account_scopes")
    fun observeAllScopes(): Flow<List<TelegramAccountScopeEntity>>

    @Query("SELECT * FROM telegram_account_scopes WHERE accountUserId = :accountUserId LIMIT 1")
    suspend fun getScopeForAccount(accountUserId: Long): TelegramAccountScopeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertScope(scope: TelegramAccountScopeEntity)
}
