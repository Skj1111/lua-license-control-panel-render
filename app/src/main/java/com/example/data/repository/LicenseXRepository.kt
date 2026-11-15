package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.room.withTransaction
import com.example.data.local.AuditLogEntity
import com.example.data.local.DeviceBindingEntity
import com.example.data.local.LicenseEntity
import com.example.data.local.LicenseXDatabase
import com.example.data.local.ReferralEntity
import com.example.data.local.ServerMaintenanceEntity
import com.example.data.local.SystemAnnouncementEntity
import com.example.data.local.TelegramAccountScopeEntity
import com.example.data.local.TelegramChannelEntity
import com.example.data.local.TelegramSettingsEntity
import com.example.data.local.UserEntity
import com.example.domain.AccessStatus
import com.example.domain.AnnouncementDisplayMode
import com.example.domain.AnnouncementTextSize
import com.example.domain.DashboardStats
import com.example.domain.DurationUnit
import com.example.domain.LicenseStatus
import com.example.domain.LicenseUiModel
import com.example.domain.LicenseValidationEngine
import com.example.domain.ReferralOverviewRow
import com.example.domain.ReferralStatus
import com.example.domain.TelegramChannelWithUser
import com.example.domain.TelegramScopeCandidate
import com.example.domain.TelegramScopeStatus
import com.example.domain.TelegramSetupState
import com.example.domain.UserRole
import com.example.domain.ValidationResponse
import com.example.domain.ValidationStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class LicenseXRepository(
    context: Context,
    private val db: LicenseXDatabase
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("licensex_session", Context.MODE_PRIVATE)

    private val _currentUserId = MutableStateFlow<Long?>(
        prefs.getLong(KEY_CURRENT_USER_ID, -1L).takeIf { it > 0L }
    )
    val currentUserId: StateFlow<Long?> = _currentUserId.asStateFlow()

    private val failedAttempts = ConcurrentHashMap<String, Pair<Int, Long>>()

    suspend fun initialize() {
        db.ensureSeeded()
        val savedId = _currentUserId.value
        if (savedId != null && db.userDao().getUserById(savedId) == null) {
            setSessionUser(null)
        }
    }

    private fun setSessionUser(userId: Long?) {
        _currentUserId.value = userId
        prefs.edit().apply {
            if (userId == null) remove(KEY_CURRENT_USER_ID) else putLong(KEY_CURRENT_USER_ID, userId)
        }.apply()
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val currentUserFlow: Flow<UserEntity?> = _currentUserId.flatMapLatest { id ->
        if (id == null) flowOf(null) else db.userDao().observeUserById(id)
    }

    val allUsersFlow: Flow<List<UserEntity>> = db.userDao().observeAllUsers()
    val allReferralsFlow: Flow<List<ReferralEntity>> = db.referralDao().observeAllReferrals()
    val recentAuditLogsFlow: Flow<List<AuditLogEntity>> =
        db.auditLogDao().observeRecentLogs(LicenseValidationEngine.RECENT_AUDIT_LOG_LIMIT)
    val announcementFlow: Flow<SystemAnnouncementEntity?> = db.systemConfigDao().observeAnnouncement()
    val maintenanceFlow: Flow<ServerMaintenanceEntity?> = db.systemConfigDao().observeMaintenance()
    val telegramSettingsFlow: Flow<TelegramSettingsEntity?> = db.telegramDao().observeSettings()

    val allLicensesUiFlow: Flow<List<LicenseUiModel>> = combine(
        db.licenseDao().observeAllLicenses(),
        db.deviceBindingDao().observeAllBindings(),
        db.userDao().observeAllUsers()
    ) { licenses, bindings, users ->
        val bindingCounts = bindings.groupingBy { it.licenseKeyId }.eachCount()
        val userMap = users.associateBy { it.id }
        val now = System.currentTimeMillis()
        licenses.map { entity ->
            val stored = LicenseStatus.fromWire(entity.status)
            val effective = LicenseValidationEngine.effectiveStatus(stored, entity.expiresAtMillis, now)
            val creator = entity.createdByUserId?.let { userMap[it] }
            LicenseUiModel(
                id = entity.id,
                keyValue = entity.keyValue,
                effectiveStatus = effective,
                storedStatus = stored,
                expiresAtMillis = entity.expiresAtMillis,
                activatedAtMillis = entity.activatedAtMillis,
                durationMinutes = entity.durationMinutes,
                maxDevices = entity.maxDevices,
                boundDeviceId = entity.boundDeviceId,
                usedDevices = bindingCounts[entity.id] ?: 0,
                createdByUserId = entity.createdByUserId,
                createdAtMillis = entity.createdAtMillis,
                creatorDisplayName = creator?.username ?: creator?.name ?: creator?.email ?: "Removed account / legacy key",
                creatorRole = creator?.role?.let { UserRole.fromWire(it) }
            )
        }
    }

    fun observeStats(): Flow<DashboardStats> = combine(
        db.licenseDao().observeAllLicenses(),
        db.deviceBindingDao().observeAllBindings()
    ) { licenses, bindings ->
        val now = System.currentTimeMillis()
        DashboardStats(
            totalKeys = licenses.size,
            activeKeys = licenses.count {
                it.status == LicenseStatus.ACTIVE.wire &&
                    (it.expiresAtMillis == null || it.expiresAtMillis > now)
            },
            revokedKeys = licenses.count { it.status == LicenseStatus.REVOKED.wire },
            totalDeviceBindings = bindings.size
        )
    }

    fun observeBindingsForKey(keyId: Long): Flow<List<DeviceBindingEntity>> =
        db.deviceBindingDao().observeBindingsForLicense(keyId)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val myTelegramChannelFlow: Flow<TelegramChannelEntity?> = _currentUserId.flatMapLatest { id ->
        if (id == null) flowOf(null) else db.telegramDao().observeChannelForUser(id)
    }

    fun observeTelegramSetupState(): Flow<TelegramSetupState> = combine(
        currentUserFlow,
        myTelegramChannelFlow
    ) { user, channel ->
        if (user == null) {
            TelegramSetupState(eligible = false, hasSavedChannel = false, needsSetup = false)
        } else {
            val role = UserRole.fromWire(user.role)
            val eligible = AccessStatus.fromWire(user.accessStatus) == AccessStatus.ACTIVE
            val hasSaved = channel != null && channel.enabled && channel.channelUrl.isNotBlank()
            TelegramSetupState(
                eligible = eligible,
                hasSavedChannel = hasSaved,
                needsSetup = eligible && !hasSaved && (role == UserRole.SUPER_OWNER || role == UserRole.OWNER || role == UserRole.ADMIN)
            )
        }
    }

    fun observeManagedTelegramChannels(actor: UserEntity): Flow<List<TelegramChannelWithUser>> = combine(
        db.telegramDao().observeAllChannels(),
        db.userDao().observeAllUsers(),
        db.referralDao().observeAllReferrals()
    ) { channels, users, referrals ->
        val userMap = users.associateBy { it.id }
        val actorRole = UserRole.fromWire(actor.role)
        val filtered = when (actorRole) {
            UserRole.SUPER_OWNER -> channels
            UserRole.OWNER -> {
                val referredAdminIds = referrals.filter {
                    it.createdByUserId == actor.id &&
                        it.role == UserRole.ADMIN.wire &&
                        it.status == ReferralStatus.REDEEMED.wire &&
                        it.redeemedByUserId != null
                }.mapNotNull { it.redeemedByUserId }.toSet()
                channels.filter { it.userId in referredAdminIds }
            }
            UserRole.ADMIN -> channels.filter { it.userId == actor.id }
        }
        filtered.mapNotNull { ch ->
            val owner = userMap[ch.userId] ?: return@mapNotNull null
            TelegramChannelWithUser(
                id = ch.id,
                userId = ch.userId,
                username = owner.username ?: owner.name,
                role = UserRole.fromWire(owner.role),
                channelUrl = ch.channelUrl,
                enabled = ch.enabled,
                updatedAtMillis = ch.updatedAtMillis
            )
        }
    }

    fun observeTelegramScopeCandidates(): Flow<List<TelegramScopeCandidate>> = combine(
        db.referralDao().observeAllReferrals(),
        db.userDao().observeAllUsers(),
        db.telegramDao().observeAllScopes()
    ) { referrals, users, scopes ->
        val userMap = users.associateBy { it.id }
        val scopeMap = scopes.associateBy { it.accountUserId }
        referrals.filter {
            it.role == UserRole.ADMIN.wire &&
                (it.status == ReferralStatus.REDEEMED.wire || it.redeemedByUserId == null)
        }.map { ref ->
            val redeemedUser = ref.redeemedByUserId?.let { userMap[it] }
            val scope = ref.redeemedByUserId?.let { scopeMap[it] }
            TelegramScopeCandidate(
                referralId = ref.id,
                referralCreatorId = ref.createdByUserId,
                accountUserId = ref.redeemedByUserId,
                accountUsername = redeemedUser?.username ?: redeemedUser?.name,
                status = ReferralStatus.fromWire(ref.status),
                redeemedAtMillis = ref.redeemedAtMillis,
                confirmedScopeStatus = scope?.status?.let { TelegramScopeStatus.fromWire(it) }
            )
        }
    }

    fun observeReferralOverview(): Flow<List<ReferralOverviewRow>> = combine(
        db.userDao().observeAllUsers(),
        db.referralDao().observeAllReferrals()
    ) { users, referrals ->
        val grouped = referrals.groupBy { it.createdByUserId }
        users.map { user ->
            val owned = grouped[user.id].orEmpty()
            ReferralOverviewRow(
                userId = user.id,
                creatorName = user.username ?: user.name ?: user.email ?: "User ${user.id}",
                role = UserRole.fromWire(user.role),
                total = owned.size,
                active = owned.count { it.status == ReferralStatus.ACTIVE.wire },
                redeemed = owned.count { it.status == ReferralStatus.REDEEMED.wire },
                revoked = owned.count { it.status == ReferralStatus.REVOKED.wire }
            )
        }.sortedByDescending { it.total }
    }

    suspend fun login(username: String, password: String): Result<UserEntity> {
        val normalized = username.trim().lowercase()
        val now = System.currentTimeMillis()
        val rate = failedAttempts[normalized]
        if (rate != null && rate.second > now && rate.first >= 5) {
            return Result.failure(IllegalStateException("Too many failed login attempts. Try again later."))
        }
        val user = db.userDao().getUserByUsername(username.trim())
        if (user == null || !LicenseValidationEngine.verifyPassword(password, user.passwordHash)) {
            val nextCount = if (rate != null && rate.second > now) rate.first + 1 else 1
            failedAttempts[normalized] = nextCount to (now + 15 * 60_000L)
            return Result.failure(IllegalArgumentException("Invalid username or password."))
        }
        failedAttempts.remove(normalized)
        val updated = user.copy(lastSignedInMillis = now, updatedAtMillis = now)
        db.userDao().updateUser(updated)
        setSessionUser(updated.id)
        return Result.success(updated)
    }

    suspend fun register(username: String, password: String): Result<UserEntity> {
        val cleanUser = try {
            LicenseValidationEngine.normalizeUsername(username)
        } catch (e: IllegalArgumentException) {
            return Result.failure(e)
        }
        if (password.length < LicenseValidationEngine.LOCAL_PASSWORD_MIN_LENGTH) {
            return Result.failure(IllegalArgumentException("Password must be at least 8 characters."))
        }
        if (db.userDao().getUserByUsername(cleanUser) != null) {
            return Result.failure(IllegalStateException("That username already exists."))
        }
        val now = System.currentTimeMillis()
        val newUser = UserEntity(
            openId = "local_${UUID.randomUUID()}",
            name = cleanUser,
            email = "${cleanUser.lowercase().replace(' ', '.')}@licensex.local",
            username = cleanUser,
            passwordHash = LicenseValidationEngine.hashPassword(password),
            role = UserRole.ADMIN.wire,
            accessStatus = AccessStatus.PENDING.wire,
            createdAtMillis = now,
            updatedAtMillis = now,
            lastSignedInMillis = now
        )
        val id = db.userDao().insertUser(newUser)
        val inserted = newUser.copy(id = id)
        recordAudit(id, cleanUser, "account_registered", "user", id, null, "status=pending")
        return Result.success(inserted)
    }

    fun logout() {
        setSessionUser(null)
    }

    suspend fun changeUsername(actorId: Long, requestedUsername: String): Result<String> {
        return try {
            val clean = LicenseValidationEngine.normalizeUsername(requestedUsername)
            val current = db.userDao().getUserById(actorId)
                ?: return Result.failure(IllegalStateException("User not found"))
            if (current.username == clean) return Result.success(clean)
            val changedAt = current.usernameChangedAtMillis
            if (changedAt != null) {
                val nextAllowed = changedAt + LicenseValidationEngine.USERNAME_CHANGE_COOLDOWN_MS
                if (System.currentTimeMillis() < nextAllowed) {
                    return Result.failure(
                        IllegalStateException(
                            "Username can be changed again after ${LicenseValidationEngine.formatTimestamp(nextAllowed)}"
                        )
                    )
                }
            }
            val existing = db.userDao().getUserByUsername(clean)
            if (existing != null && existing.id != actorId) {
                return Result.failure(IllegalStateException("That username is already in use"))
            }
            val now = System.currentTimeMillis()
            db.userDao().updateUser(
                current.copy(
                    username = clean,
                    name = clean,
                    usernameChangedAtMillis = now,
                    updatedAtMillis = now
                )
            )
            recordAudit(
                actorId,
                clean,
                if (current.username == null) "username_set" else "username_changed",
                "user",
                actorId,
                null,
                "username=$clean"
            )
            Result.success(clean)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateLicenseKey(
        actor: UserEntity,
        durationAmount: Int,
        durationUnit: DurationUnit,
        maxDevices: Int,
        customKey: String?
    ): Result<LicenseEntity> {
        return try {
            val durationMinutes = (durationAmount.toLong() * durationUnit.minutesMultiplier).toInt()
            val keyValue = if (!customKey.isNullOrBlank()) {
                val existing = db.licenseDao().getLicenseByKey(customKey)
                val now = System.currentTimeMillis()
                if (existing != null && existing.status != LicenseStatus.REVOKED.wire &&
                    (existing.expiresAtMillis == null || existing.expiresAtMillis > now)
                ) {
                    return Result.failure(
                        IllegalStateException("Custom key $customKey already exists until it expires or is deleted")
                    )
                }
                customKey
            } else {
                val prefix = LicenseValidationEngine.usernameToKeyPrefix(actor.username)
                LicenseValidationEngine.generateLicenseKey(prefix)
            }
            val now = System.currentTimeMillis()
            val entity = LicenseEntity(
                keyValue = keyValue,
                status = LicenseStatus.UNUSED.wire,
                durationMinutes = durationMinutes,
                maxDevices = maxDevices,
                createdByUserId = actor.id,
                createdAtMillis = now,
                updatedAtMillis = now
            )
            val id = db.licenseDao().insertLicense(entity)
            recordAudit(
                actor.id,
                actor.username ?: actor.name,
                "key_created",
                "license",
                id,
                keyValue,
                "durationMinutes=$durationMinutes;activation=pending;maxDevices=$maxDevices"
            )
            Result.success(entity.copy(id = id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setKeyStatus(actor: UserEntity, keyId: Long, status: LicenseStatus): Result<Unit> {
        val row = db.licenseDao().getLicenseById(keyId)
            ?: return Result.failure(IllegalStateException("License key not found"))
        if (!canAccessLicense(actor, row.createdByUserId)) {
            return Result.failure(IllegalAccessException("You can only manage keys you created"))
        }
        db.licenseDao().updateLicense(
            row.copy(status = status.wire, updatedAtMillis = System.currentTimeMillis())
        )
        recordAudit(
            actor.id,
            actor.username ?: actor.name,
            if (status == LicenseStatus.REVOKED) "key_revoked" else "key_status_updated",
            "license",
            keyId,
            row.keyValue,
            "status=${status.wire}"
        )
        return Result.success(Unit)
    }

    suspend fun updateLicense(
        actor: UserEntity,
        keyId: Long,
        status: LicenseStatus,
        expiresAtMillis: Long?,
        maxDevices: Int
    ): Result<Unit> {
        val row = db.licenseDao().getLicenseById(keyId)
            ?: return Result.failure(IllegalStateException("License key not found"))
        if (!canAccessLicense(actor, row.createdByUserId)) {
            return Result.failure(IllegalAccessException("You can only manage keys you created"))
        }
        val boundCount = db.deviceBindingDao().countBindingsForLicense(keyId)
        if (maxDevices < boundCount) {
            return Result.failure(
                IllegalArgumentException("Maximum devices cannot be below the $boundCount currently bound device(s)")
            )
        }
        db.licenseDao().updateLicense(
            row.copy(
                status = status.wire,
                expiresAtMillis = expiresAtMillis ?: row.expiresAtMillis,
                maxDevices = maxDevices,
                updatedAtMillis = System.currentTimeMillis()
            )
        )
        recordAudit(
            actor.id,
            actor.username ?: actor.name,
            "key_updated",
            "license",
            keyId,
            row.keyValue,
            "status=${status.wire};maxDevices=$maxDevices"
        )
        return Result.success(Unit)
    }

    suspend fun deleteKey(actor: UserEntity, keyId: Long): Result<Unit> {
        if (!UserRole.fromWire(actor.role).isOwnerLevel) {
            return Result.failure(IllegalAccessException("Only the owner can delete license keys"))
        }
        val row = db.licenseDao().getLicenseById(keyId)
            ?: return Result.failure(IllegalStateException("License key not found"))
        db.withTransaction {
            db.deviceBindingDao().deleteBindingsForLicense(keyId)
            db.licenseDao().deleteLicenseById(keyId)
        }
        recordAudit(actor.id, actor.username ?: actor.name, "key_deleted", "license", keyId, row.keyValue)
        return Result.success(Unit)
    }

    suspend fun unbindDevice(actor: UserEntity, bindingId: Long): Result<Unit> {
        val binding = db.deviceBindingDao().getBindingById(bindingId)
            ?: return Result.failure(IllegalStateException("Device binding not found"))
        val row = db.licenseDao().getLicenseById(binding.licenseKeyId)
            ?: return Result.failure(IllegalStateException("License key not found"))
        if (!canAccessLicense(actor, row.createdByUserId)) {
            return Result.failure(IllegalAccessException("You can only manage keys you created"))
        }
        db.withTransaction {
            db.deviceBindingDao().deleteBindingById(bindingId)
            val remaining = db.deviceBindingDao().getBindingsForLicense(row.id)
            val nextDevice = remaining.firstOrNull()?.deviceHash
            db.licenseDao().updateLicense(
                row.copy(
                    boundDeviceId = nextDevice,
                    status = if (nextDevice != null) row.status else LicenseStatus.UNUSED.wire,
                    updatedAtMillis = System.currentTimeMillis()
                )
            )
        }
        recordAudit(
            actor.id,
            actor.username ?: actor.name,
            "device_unbound",
            "binding",
            bindingId,
            row.keyValue,
            "licenseId=${row.id}"
        )
        return Result.success(Unit)
    }

    suspend fun createReferral(actor: UserEntity, role: UserRole, durationDays: Int): Result<ReferralEntity> {
        if (!UserRole.fromWire(actor.role).isOwnerLevel) {
            return Result.failure(IllegalAccessException("Owner access required"))
        }
        if (durationDays !in 1..3650) {
            return Result.failure(IllegalArgumentException("Duration must be 1–3650 days"))
        }
        val now = System.currentTimeMillis()
        val expiresAt = now + durationDays * 86_400_000L
        val code = LicenseValidationEngine.generateReferralCode()
        val entity = ReferralEntity(
            code = code,
            role = role.wire,
            createdByUserId = actor.id,
            status = ReferralStatus.ACTIVE.wire,
            expiresAtMillis = expiresAt,
            createdAtMillis = now
        )
        val id = db.referralDao().insertReferral(entity)
        recordAudit(
            actor.id,
            actor.username ?: actor.name,
            "referral_created",
            "referral",
            id,
            null,
            "role=${role.wire};days=$durationDays"
        )
        return Result.success(entity.copy(id = id))
    }

    suspend fun revokeReferral(actor: UserEntity, referralId: Long): Result<Unit> {
        val ref = db.referralDao().getReferralById(referralId)
            ?: return Result.failure(IllegalStateException("Referral not found"))
        if (ref.status != ReferralStatus.ACTIVE.wire) {
            return Result.failure(IllegalStateException("Only active referrals can be revoked"))
        }
        db.referralDao().updateReferral(ref.copy(status = ReferralStatus.REVOKED.wire))
        recordAudit(actor.id, actor.username ?: actor.name, "referral_revoked", "referral", referralId)
        return Result.success(Unit)
    }

    suspend fun deleteReferral(actor: UserEntity, referralId: Long): Result<Unit> {
        val ref = db.referralDao().getReferralById(referralId)
            ?: return Result.failure(IllegalStateException("Referral not found"))
        val expired = ref.expiresAtMillis <= System.currentTimeMillis()
        if (ref.status == ReferralStatus.ACTIVE.wire && !expired) {
            return Result.failure(IllegalStateException("Only revoked, redeemed, or expired referrals can be deleted"))
        }
        db.referralDao().deleteReferralById(referralId)
        recordAudit(actor.id, actor.username ?: actor.name, "referral_deleted", "referral", referralId)
        return Result.success(Unit)
    }

    suspend fun redeemReferral(actorId: Long, codeRaw: String): Result<UserRole> {
        val code = codeRaw.trim().uppercase()
        val ref = db.referralDao().getReferralByCode(code)
            ?: return Result.failure(IllegalArgumentException("Referral is invalid, expired, already used, or revoked"))
        val now = System.currentTimeMillis()
        if (ref.status != ReferralStatus.ACTIVE.wire || ref.expiresAtMillis <= now) {
            return Result.failure(IllegalArgumentException("Referral is invalid, expired, already used, or revoked"))
        }
        val user = db.userDao().getUserById(actorId)
            ?: return Result.failure(IllegalStateException("User not found"))
        db.withTransaction {
            db.userDao().updateUser(
                user.copy(
                    role = ref.role,
                    accessStatus = AccessStatus.ACTIVE.wire,
                    accessExpiresAtMillis = ref.expiresAtMillis,
                    updatedAtMillis = now
                )
            )
            db.referralDao().updateReferral(
                ref.copy(
                    status = ReferralStatus.REDEEMED.wire,
                    redeemedByUserId = actorId,
                    redeemedAtMillis = now
                )
            )
        }
        recordAudit(
            actorId,
            user.username ?: user.name,
            "referral_redeemed",
            "referral",
            ref.id,
            null,
            "role=${ref.role}"
        )
        return Result.success(UserRole.fromWire(ref.role))
    }

    suspend fun setAdminAccess(actor: UserEntity, targetId: Long, status: AccessStatus): Result<Unit> {
        val target = db.userDao().getUserById(targetId)
            ?: return Result.failure(IllegalStateException("User not found"))
        if (target.id == actor.id) {
            return Result.failure(IllegalArgumentException("Owner role cannot be changed"))
        }
        db.userDao().updateUser(
            target.copy(
                role = UserRole.ADMIN.wire,
                accessStatus = status.wire,
                updatedAtMillis = System.currentTimeMillis()
            )
        )
        recordAudit(
            actor.id,
            actor.username ?: actor.name,
            if (status == AccessStatus.ACTIVE) "user_access_restored" else "user_access_revoked",
            "user",
            targetId,
            null,
            "targetUserId=$targetId"
        )
        return Result.success(Unit)
    }

    suspend fun removeOwnerAccess(actor: UserEntity, targetId: Long): Result<Unit> {
        if (UserRole.fromWire(actor.role) != UserRole.SUPER_OWNER || actor.id == targetId) {
            return Result.failure(IllegalAccessException("Only the Super Owner can remove another Owner account"))
        }
        val target = db.userDao().getUserById(targetId)
            ?: return Result.failure(IllegalStateException("User not found"))
        if (UserRole.fromWire(target.role) != UserRole.OWNER) {
            return Result.failure(IllegalArgumentException("Target account is not an Owner"))
        }
        db.userDao().updateUser(
            target.copy(
                role = UserRole.ADMIN.wire,
                accessStatus = AccessStatus.REVOKED.wire,
                accessExpiresAtMillis = null,
                updatedAtMillis = System.currentTimeMillis()
            )
        )
        recordAudit(
            actor.id,
            actor.username ?: actor.name,
            "owner_access_removed",
            "user",
            targetId,
            null,
            "targetUserId=$targetId;previousRole=owner"
        )
        return Result.success(Unit)
    }

    suspend fun deleteUser(actor: UserEntity, targetId: Long): Result<Unit> {
        val target = db.userDao().getUserById(targetId)
            ?: return Result.failure(IllegalStateException("User not found"))
        if (target.id == actor.id) {
            return Result.failure(IllegalArgumentException("You cannot delete your own account"))
        }
        val now = System.currentTimeMillis()
        val clearable = target.accessStatus == AccessStatus.REVOKED.wire ||
            (target.accessExpiresAtMillis != null && target.accessExpiresAtMillis <= now)
        if (!clearable) {
            return Result.failure(IllegalStateException("Only revoked or expired accounts can be cleared"))
        }
        db.withTransaction {
            db.licenseDao().clearCreatorForUser(targetId)
            db.referralDao().deleteReferralsByCreator(targetId)
            db.userDao().deleteUserById(targetId)
        }
        recordAudit(actor.id, actor.username ?: actor.name, "user_deleted", "user", targetId)
        return Result.success(Unit)
    }

    suspend fun deleteAllKeys(actor: UserEntity): Result<Int> {
        if (UserRole.fromWire(actor.role) != UserRole.SUPER_OWNER) {
            return Result.failure(IllegalAccessException("Super Owner access required"))
        }
        val count = db.withTransaction {
            val total = db.licenseDao().countAllLicenses()
            db.deviceBindingDao().deleteAllBindings()
            db.licenseDao().deleteAllLicenses()
            total
        }
        recordAudit(actor.id, actor.username ?: actor.name, "all_keys_deleted", "season", null, null, "deletedCount=$count")
        return Result.success(count)
    }

    suspend fun deleteAllReferrals(actor: UserEntity): Result<Int> {
        if (UserRole.fromWire(actor.role) != UserRole.SUPER_OWNER) {
            return Result.failure(IllegalAccessException("Super Owner access required"))
        }
        val count = db.withTransaction {
            val total = db.referralDao().countAllReferrals()
            db.referralDao().deleteAllReferrals()
            total
        }
        recordAudit(actor.id, actor.username ?: actor.name, "all_referrals_deleted", "season", null, null, "deletedCount=$count")
        return Result.success(count)
    }

    suspend fun updateSystemAnnouncement(
        actor: UserEntity,
        enabled: Boolean,
        titleRaw: String,
        messageRaw: String,
        startAtMillis: Long?,
        endAtMillis: Long?,
        displayMode: AnnouncementDisplayMode,
        textSize: AnnouncementTextSize
    ): Result<Unit> {
        return try {
            if (UserRole.fromWire(actor.role) != UserRole.SUPER_OWNER) {
                return Result.failure(IllegalAccessException("Super Owner access required"))
            }
            val title = LicenseValidationEngine.normalizeControlText(
                titleRaw,
                LicenseValidationEngine.ANNOUNCEMENT_TITLE_MAX_LENGTH,
                "Announcement title"
            ).ifEmpty { "SYSTEM ANNOUNCEMENT" }
            val message = LicenseValidationEngine.normalizeControlText(
                messageRaw,
                LicenseValidationEngine.ANNOUNCEMENT_MESSAGE_MAX_LENGTH,
                "Announcement message"
            )
            if (startAtMillis != null && endAtMillis != null && startAtMillis > endAtMillis) {
                return Result.failure(IllegalArgumentException("Announcement start must be before its end"))
            }
            val current = db.systemConfigDao().getAnnouncement()
            val nextVersion = (current?.version ?: 0) + 1
            db.systemConfigDao().upsertAnnouncement(
                SystemAnnouncementEntity(
                    id = 1,
                    version = nextVersion,
                    enabled = enabled,
                    title = title,
                    message = message,
                    startAtMillis = startAtMillis,
                    endAtMillis = endAtMillis,
                    displayMode = displayMode.wire,
                    textSize = textSize.wire,
                    updatedByUserId = actor.id,
                    updatedAtMillis = System.currentTimeMillis()
                )
            )
            recordAudit(
                actor.id,
                actor.username ?: actor.name,
                if (enabled) "announcement_enabled_or_updated" else "announcement_disabled",
                "system_announcement",
                1,
                null,
                "version=$nextVersion;displayMode=${displayMode.wire};textSize=${textSize.wire}"
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateServerMaintenance(
        actor: UserEntity,
        enabled: Boolean,
        titleRaw: String,
        messageRaw: String,
        startAtMillis: Long?,
        endAtMillis: Long?
    ): Result<Unit> {
        return try {
            if (UserRole.fromWire(actor.role) != UserRole.SUPER_OWNER) {
                return Result.failure(IllegalAccessException("Super Owner access required"))
            }
            val title = LicenseValidationEngine.normalizeControlText(
                titleRaw,
                LicenseValidationEngine.MAINTENANCE_TITLE_MAX_LENGTH,
                "Maintenance title"
            ).ifEmpty { "SERVER MAINTENANCE" }
            val message = LicenseValidationEngine.normalizeControlText(
                messageRaw,
                LicenseValidationEngine.MAINTENANCE_MESSAGE_MAX_LENGTH,
                "Maintenance message"
            )
            if (enabled && message.isEmpty()) {
                return Result.failure(IllegalArgumentException("Maintenance message is required while maintenance is enabled"))
            }
            if (startAtMillis != null && endAtMillis != null && startAtMillis > endAtMillis) {
                return Result.failure(IllegalArgumentException("Maintenance start must be before its end"))
            }
            val current = db.systemConfigDao().getMaintenance()
            val nextVersion = (current?.version ?: 0) + 1
            db.systemConfigDao().upsertMaintenance(
                ServerMaintenanceEntity(
                    id = 1,
                    version = nextVersion,
                    enabled = enabled,
                    title = title,
                    message = message,
                    startAtMillis = startAtMillis,
                    endAtMillis = endAtMillis,
                    updatedByUserId = actor.id,
                    updatedAtMillis = System.currentTimeMillis()
                )
            )
            recordAudit(
                actor.id,
                actor.username ?: actor.name,
                if (enabled) "maintenance_enabled_or_updated" else "maintenance_disabled",
                "server_maintenance",
                1,
                null,
                "version=$nextVersion"
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTelegramSettings(actor: UserEntity, enabled: Boolean): Result<Unit> {
        if (UserRole.fromWire(actor.role) != UserRole.SUPER_OWNER) {
            return Result.failure(IllegalAccessException("Super Owner access required"))
        }
        db.telegramDao().upsertSettings(
            TelegramSettingsEntity(
                id = 1,
                enabled = enabled,
                updatedByUserId = actor.id,
                updatedAtMillis = System.currentTimeMillis()
            )
        )
        return Result.success(Unit)
    }

    suspend fun saveTelegramChannel(
        actor: UserEntity,
        targetUserId: Long,
        channelUrlRaw: String,
        enabled: Boolean
    ): Result<Unit> {
        return try {
            val actorRole = UserRole.fromWire(actor.role)
            val normalizedUrl = LicenseValidationEngine.normalizeTelegramChannelUrl(channelUrlRaw)
            val existing = db.telegramDao().getChannelForUser(targetUserId)
            if (actorRole == UserRole.ADMIN && targetUserId == actor.id && existing != null) {
                return Result.failure(IllegalAccessException("Admins can create the first Telegram channel but cannot edit it later"))
            }
            if (actorRole == UserRole.ADMIN && targetUserId != actor.id) {
                return Result.failure(IllegalAccessException("You do not have Telegram permission for this account"))
            }
            db.telegramDao().upsertChannel(
                TelegramChannelEntity(
                    id = existing?.id ?: 0L,
                    userId = targetUserId,
                    channelUrl = normalizedUrl,
                    enabled = enabled,
                    updatedAtMillis = System.currentTimeMillis()
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun confirmTelegramScope(
        actor: UserEntity,
        accountUserId: Long,
        ownerUserId: Long,
        sourceReferralId: Long?
    ): Result<Unit> {
        if (UserRole.fromWire(actor.role) != UserRole.SUPER_OWNER) {
            return Result.failure(IllegalAccessException("Only the Super Owner can confirm Telegram account scope"))
        }
        val existing = db.telegramDao().getScopeForAccount(accountUserId)
        db.telegramDao().upsertScope(
            TelegramAccountScopeEntity(
                id = existing?.id ?: 0L,
                ownerUserId = ownerUserId,
                accountUserId = accountUserId,
                sourceReferralId = sourceReferralId,
                status = TelegramScopeStatus.ACTIVE.wire,
                updatedAtMillis = System.currentTimeMillis()
            )
        )
        return Result.success(Unit)
    }

    /**
     * Exact Android port of `validateCanonicalLicense` from `server/license.ts`:
     * handles first-activation clock start, max-device enforcement, and safe
     * replacement rebinding from retired (expired/revoked) licenses.
     */
    suspend fun validateCanonicalLicense(keyRaw: String, deviceIdRaw: String): ValidationResponse {
        val normalizedKey = keyRaw.trim().uppercase()
        val trimmedDevice = deviceIdRaw.trim()
        if (normalizedKey.isEmpty() || trimmedDevice.isEmpty()) {
            return ValidationResponse(false, ValidationStatus.NOT_REGISTERED, "A valid key and device_id are required.")
        }
        val deviceHash = LicenseValidationEngine.hashDevice(trimmedDevice)
        val now = System.currentTimeMillis()

        val result = db.withTransaction {
            val row = db.licenseDao().getLicenseByKey(normalizedKey)
                ?: return@withTransaction ValidationResponse(
                    false,
                    ValidationStatus.NOT_REGISTERED,
                    ValidationStatus.NOT_REGISTERED.defaultMessage
                )

            val storedStatus = LicenseStatus.fromWire(row.status)
            if (storedStatus == LicenseStatus.REVOKED) {
                return@withTransaction ValidationResponse(
                    false,
                    ValidationStatus.INVALID,
                    ValidationStatus.INVALID.defaultMessage
                )
            }
            if (storedStatus == LicenseStatus.EXPIRED || (row.expiresAtMillis != null && row.expiresAtMillis <= now)) {
                if (row.status != LicenseStatus.EXPIRED.wire) {
                    db.licenseDao().updateLicense(row.copy(status = LicenseStatus.EXPIRED.wire, updatedAtMillis = now))
                }
                return@withTransaction ValidationResponse(
                    false,
                    ValidationStatus.EXPIRED,
                    ValidationStatus.EXPIRED.defaultMessage
                )
            }
            if (row.boundDeviceId != null && row.boundDeviceId != deviceHash && row.maxDevices <= 1) {
                return@withTransaction ValidationResponse(
                    false,
                    ValidationStatus.DEVICE_MISMATCH,
                    ValidationStatus.DEVICE_MISMATCH.defaultMessage
                )
            }

            var existingBinding = db.deviceBindingDao().getBindingByDeviceHash(deviceHash)
            if (existingBinding != null && existingBinding.licenseKeyId != row.id) {
                val sourceLicense = db.licenseDao().getLicenseById(existingBinding.licenseKeyId)
                    ?: return@withTransaction ValidationResponse(
                        false,
                        ValidationStatus.DEVICE_MISMATCH,
                        ValidationStatus.DEVICE_MISMATCH.defaultMessage
                    )
                val sourceRetired = sourceLicense.status == LicenseStatus.REVOKED.wire ||
                    sourceLicense.status == LicenseStatus.EXPIRED.wire ||
                    (sourceLicense.expiresAtMillis != null && sourceLicense.expiresAtMillis <= now)
                if (!sourceRetired) {
                    return@withTransaction ValidationResponse(
                        false,
                        ValidationStatus.DEVICE_MISMATCH,
                        ValidationStatus.DEVICE_MISMATCH.defaultMessage
                    )
                }
                val targetCount = db.deviceBindingDao().countBindingsForLicense(row.id)
                if (targetCount >= row.maxDevices) {
                    return@withTransaction ValidationResponse(
                        false,
                        ValidationStatus.DEVICE_MAX_REACHED,
                        ValidationStatus.DEVICE_MAX_REACHED.defaultMessage
                    )
                }
                db.deviceBindingDao().updateBinding(
                    existingBinding.copy(licenseKeyId = row.id, lastSeenAtMillis = now)
                )
                val nextSourceBinding = db.deviceBindingDao().getBindingsForLicense(sourceLicense.id).firstOrNull()
                db.licenseDao().updateLicense(
                    sourceLicense.copy(
                        boundDeviceId = nextSourceBinding?.deviceHash,
                        updatedAtMillis = now
                    )
                )
                existingBinding = existingBinding.copy(licenseKeyId = row.id)
            }

            if (existingBinding == null) {
                val count = db.deviceBindingDao().countBindingsForLicense(row.id)
                if (count >= row.maxDevices) {
                    return@withTransaction ValidationResponse(
                        false,
                        ValidationStatus.DEVICE_MAX_REACHED,
                        ValidationStatus.DEVICE_MAX_REACHED.defaultMessage
                    )
                }
                db.deviceBindingDao().insertBinding(
                    DeviceBindingEntity(
                        licenseKeyId = row.id,
                        deviceHash = deviceHash,
                        firstSeenAtMillis = now,
                        lastSeenAtMillis = now
                    )
                )
            } else {
                db.deviceBindingDao().updateBinding(existingBinding.copy(lastSeenAtMillis = now))
            }

            val activatesNow = row.activatedAtMillis == null && row.expiresAtMillis == null && row.durationMinutes != null
            val activatedAt = if (activatesNow) now else row.activatedAtMillis
            val expiresAt = if (activatesNow) now + row.durationMinutes!!.toLong() * 60_000L else row.expiresAtMillis

            db.licenseDao().updateLicense(
                row.copy(
                    boundDeviceId = row.boundDeviceId ?: deviceHash,
                    status = LicenseStatus.ACTIVE.wire,
                    activatedAtMillis = activatedAt,
                    expiresAtMillis = expiresAt,
                    updatedAtMillis = now
                )
            )
            ValidationResponse(
                success = true,
                status = ValidationStatus.VALID,
                message = ValidationStatus.VALID.defaultMessage,
                expiresAtMillis = expiresAt
            )
        }

        if (result.success) {
            recordAudit(null, "system", "device_bound", "license", null, normalizedKey, "server-authoritative validation accepted")
        }
        return result
    }

    private fun canAccessLicense(actor: UserEntity, createdByUserId: Long?): Boolean {
        return UserRole.fromWire(actor.role).isOwnerLevel || createdByUserId == actor.id
    }

    private suspend fun recordAudit(
        actorUserId: Long?,
        actorName: String?,
        action: String,
        entityType: String,
        entityId: Long? = null,
        rawKeyForMask: String? = null,
        details: String? = null
    ) {
        db.auditLogDao().insertLog(
            AuditLogEntity(
                actorUserId = actorUserId,
                actorName = actorName ?: "system",
                action = action,
                entityType = entityType,
                entityId = entityId,
                keyPreview = rawKeyForMask?.let { LicenseValidationEngine.maskKey(it) },
                details = details
            )
        )
    }

    companion object {
        private const val KEY_CURRENT_USER_ID = "current_user_id"
    }
}
