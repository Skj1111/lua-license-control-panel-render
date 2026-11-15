package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.LicenseXApplication
import com.example.data.local.AuditLogEntity
import com.example.data.local.DeviceBindingEntity
import com.example.data.local.ReferralEntity
import com.example.data.local.ServerMaintenanceEntity
import com.example.data.local.SystemAnnouncementEntity
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
import com.example.domain.TelegramChannelWithUser
import com.example.domain.TelegramScopeCandidate
import com.example.domain.TelegramSetupState
import com.example.domain.UserRole
import com.example.domain.ValidationResponse
import com.example.domain.ValidationResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface AuthGateState {
    data object Loading : AuthGateState
    data object Unauthenticated : AuthGateState
    data class PendingReferral(val user: UserEntity) : AuthGateState
    data class Suspended(val user: UserEntity) : AuthGateState
    data class Active(val user: UserEntity) : AuthGateState
}

@OptIn(ExperimentalCoroutinesApi::class)
class LicenseXViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = (application as LicenseXApplication).repository

    private val _events = MutableSharedFlow<String>(extraBufferCapacity = 16)
    val events = _events.asSharedFlow()

    private val _isInitialized = MutableStateFlow(false)
    private val _isAuthBusy = MutableStateFlow(false)
    val isAuthBusy: StateFlow<Boolean> = _isAuthBusy.asStateFlow()

    private val _selectedKeyForBindings = MutableStateFlow<Long?>(null)
    val selectedKeyForBindings: StateFlow<Long?> = _selectedKeyForBindings.asStateFlow()

    private val _validationTestResult = MutableStateFlow<ValidationResponse?>(null)
    val validationTestResult: StateFlow<ValidationResponse?> = _validationTestResult.asStateFlow()

    init {
        viewModelScope.launch {
            repo.initialize()
            _isInitialized.value = true
        }
    }

    val authGateState: StateFlow<AuthGateState> = combine(
        _isInitialized,
        repo.currentUserFlow
    ) { initialized, user ->
        if (!initialized) {
            AuthGateState.Loading
        } else if (user == null) {
            AuthGateState.Unauthenticated
        } else {
            val expired = user.accessExpiresAtMillis != null && user.accessExpiresAtMillis <= System.currentTimeMillis()
            val status = AccessStatus.fromWire(user.accessStatus)
            when {
                status == AccessStatus.PENDING -> AuthGateState.PendingReferral(user)
                status != AccessStatus.ACTIVE || expired -> AuthGateState.Suspended(user)
                else -> AuthGateState.Active(user)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AuthGateState.Loading)

    val stats: StateFlow<DashboardStats> = repo.observeStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardStats())

    val allLicenses: StateFlow<List<LicenseUiModel>> = repo.allLicensesUiFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val visibleHomeLicenses: StateFlow<List<LicenseUiModel>> = combine(
        repo.currentUserFlow,
        repo.allLicensesUiFlow
    ) { user, licenses ->
        if (user == null) emptyList()
        else {
            val role = UserRole.fromWire(user.role)
            if (role.isOwnerLevel) licenses else licenses.filter { it.createdByUserId == user.id }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repo.allUsersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val allReferrals: StateFlow<List<ReferralEntity>> = repo.allReferralsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val referralOverview: StateFlow<List<ReferralOverviewRow>> = repo.observeReferralOverview()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val auditLogs: StateFlow<List<AuditLogEntity>> = repo.recentAuditLogsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val announcement: StateFlow<SystemAnnouncementEntity?> = repo.announcementFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val maintenance: StateFlow<ServerMaintenanceEntity?> = repo.maintenanceFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val telegramSettings: StateFlow<TelegramSettingsEntity?> = repo.telegramSettingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val myTelegramChannel: StateFlow<TelegramChannelEntity?> = repo.myTelegramChannelFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val telegramSetupState: StateFlow<TelegramSetupState> = repo.observeTelegramSetupState()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            TelegramSetupState(eligible = false, hasSavedChannel = false, needsSetup = false)
        )

    val managedTelegramChannels: StateFlow<List<TelegramChannelWithUser>> = repo.currentUserFlow
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList()) else repo.observeManagedTelegramChannels(user)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val telegramScopeCandidates: StateFlow<List<TelegramScopeCandidate>> = repo.observeTelegramScopeCandidates()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val selectedKeyBindings: StateFlow<List<DeviceBindingEntity>> = _selectedKeyForBindings
        .flatMapLatest { keyId ->
            if (keyId == null) flowOf(emptyList()) else repo.observeBindingsForKey(keyId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun selectKeyForBindings(keyId: Long?) {
        _selectedKeyForBindings.value = keyId
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _isAuthBusy.value = true
            repo.login(username, password).fold(
                onSuccess = { _events.emit("Authenticated as ${it.username ?: it.name}") },
                onFailure = { _events.emit(it.message ?: "Authentication failed") }
            )
            _isAuthBusy.value = false
        }
    }

    fun register(username: String, password: String, onRegistered: () -> Unit) {
        viewModelScope.launch {
            _isAuthBusy.value = true
            repo.register(username, password).fold(
                onSuccess = {
                    _events.emit("Account created — please login")
                    onRegistered()
                },
                onFailure = { _events.emit(it.message ?: "Registration failed") }
            )
            _isAuthBusy.value = false
        }
    }

    fun logout() {
        repo.logout()
    }

    fun redeemReferral(code: String, onNeedsUsername: () -> Unit) {
        val user = (authGateState.value as? AuthGateState.PendingReferral)?.user ?: return
        viewModelScope.launch {
            repo.redeemReferral(user.id, code).fold(
                onSuccess = { role ->
                    _events.emit("Referral accepted: ${role.label}")
                    if (user.username.isNullOrBlank()) onNeedsUsername()
                },
                onFailure = { _events.emit(it.message ?: "Invalid referral code") }
            )
        }
    }

    fun saveUsername(requestedUsername: String, onSaved: () -> Unit) {
        val user = currentActiveOrPendingUser() ?: return
        viewModelScope.launch {
            repo.changeUsername(user.id, requestedUsername).fold(
                onSuccess = { name ->
                    _events.emit("Username saved: $name")
                    onSaved()
                },
                onFailure = { _events.emit(it.message ?: "Could not save username") }
            )
        }
    }

    fun generateKey(
        durationAmountRaw: String,
        durationUnit: DurationUnit,
        maxDevicesRaw: String,
        customEnabled: Boolean,
        customPrefixRaw: String,
        onGenerated: (String) -> Unit
    ) {
        val user = (authGateState.value as? AuthGateState.Active)?.user ?: return
        when (
            val validation = LicenseValidationEngine.validateLicenseGenerationInput(
                durationAmountRaw, durationUnit, maxDevicesRaw, customEnabled, customPrefixRaw
            )
        ) {
            is ValidationResult.Invalid -> {
                viewModelScope.launch { _events.emit(validation.error) }
            }
            is ValidationResult.Valid -> {
                viewModelScope.launch {
                    repo.generateLicenseKey(
                        actor = user,
                        durationAmount = validation.value.durationAmount,
                        durationUnit = durationUnit,
                        maxDevices = validation.value.maxDevices,
                        customKey = validation.value.customKey
                    ).fold(
                        onSuccess = { created ->
                            onGenerated(created.keyValue)
                            _events.emit("Key generated: ${created.keyValue}")
                        },
                        onFailure = { _events.emit(it.message ?: "Failed to generate key") }
                    )
                }
            }
        }
    }

    fun toggleKeyRevoked(key: LicenseUiModel) {
        val user = (authGateState.value as? AuthGateState.Active)?.user ?: return
        val next = if (key.effectiveStatus == LicenseStatus.ACTIVE) LicenseStatus.REVOKED else LicenseStatus.ACTIVE
        viewModelScope.launch {
            repo.setKeyStatus(user, key.id, next).fold(
                onSuccess = { _events.emit("Key status updated to ${next.wire}") },
                onFailure = { _events.emit(it.message ?: "Status update failed") }
            )
        }
    }

    fun updateLicense(
        keyId: Long,
        status: LicenseStatus,
        requiresExpiry: Boolean,
        expiryRaw: String,
        maxDevicesRaw: String,
        onSaved: () -> Unit
    ) {
        val user = (authGateState.value as? AuthGateState.Active)?.user ?: return
        when (
            val validation = LicenseValidationEngine.validateLicenseManagementInput(
                maxDevicesRaw = maxDevicesRaw,
                requiresExpiry = requiresExpiry,
                expiryRaw = expiryRaw
            )
        ) {
            is ValidationResult.Invalid -> viewModelScope.launch { _events.emit(validation.error) }
            is ValidationResult.Valid -> {
                viewModelScope.launch {
                    repo.updateLicense(
                        actor = user,
                        keyId = keyId,
                        status = status,
                        expiresAtMillis = validation.value.expiresAtMillis,
                        maxDevices = validation.value.maxDevices
                    ).fold(
                        onSuccess = {
                            _events.emit("License settings saved")
                            onSaved()
                        },
                        onFailure = { _events.emit(it.message ?: "Save failed") }
                    )
                }
            }
        }
    }

    fun deleteKey(keyId: Long) {
        val user = (authGateState.value as? AuthGateState.Active)?.user ?: return
        viewModelScope.launch {
            repo.deleteKey(user, keyId).fold(
                onSuccess = { _events.emit("Key deleted") },
                onFailure = { _events.emit(it.message ?: "Delete failed") }
            )
        }
    }

    fun unbindDevice(bindingId: Long) {
        val user = (authGateState.value as? AuthGateState.Active)?.user ?: return
        viewModelScope.launch {
            repo.unbindDevice(user, bindingId).fold(
                onSuccess = { _events.emit("Device unbound") },
                onFailure = { _events.emit(it.message ?: "Unbind failed") }
            )
        }
    }

    fun createReferral(role: UserRole, durationDaysRaw: String) {
        val user = (authGateState.value as? AuthGateState.Active)?.user ?: return
        val days = durationDaysRaw.trim().toIntOrNull()
        if (days == null || days !in 1..3650) {
            viewModelScope.launch { _events.emit("Enter a referral duration between 1 and 3,650 days.") }
            return
        }
        viewModelScope.launch {
            repo.createReferral(user, role, days).fold(
                onSuccess = { _events.emit("${role.label} referral created: ${it.code}") },
                onFailure = { _events.emit(it.message ?: "Failed to create referral") }
            )
        }
    }

    fun revokeReferral(referralId: Long) {
        val user = (authGateState.value as? AuthGateState.Active)?.user ?: return
        viewModelScope.launch {
            repo.revokeReferral(user, referralId).fold(
                onSuccess = { _events.emit("Referral revoked") },
                onFailure = { _events.emit(it.message ?: "Could not revoke referral") }
            )
        }
    }

    fun deleteReferral(referralId: Long) {
        val user = (authGateState.value as? AuthGateState.Active)?.user ?: return
        viewModelScope.launch {
            repo.deleteReferral(user, referralId).fold(
                onSuccess = { _events.emit("Referral cleared") },
                onFailure = { _events.emit(it.message ?: "Could not delete referral") }
            )
        }
    }

    fun toggleAdminAccess(target: UserEntity) {
        val user = (authGateState.value as? AuthGateState.Active)?.user ?: return
        val next = if (target.accessStatus == AccessStatus.ACTIVE.wire) AccessStatus.REVOKED else AccessStatus.ACTIVE
        viewModelScope.launch {
            repo.setAdminAccess(user, target.id, next).fold(
                onSuccess = { _events.emit("Account access set to ${next.wire}") },
                onFailure = { _events.emit(it.message ?: "Access update failed") }
            )
        }
    }

    fun removeOwnerAccess(targetId: Long) {
        val user = (authGateState.value as? AuthGateState.Active)?.user ?: return
        viewModelScope.launch {
            repo.removeOwnerAccess(user, targetId).fold(
                onSuccess = { _events.emit("Owner access removed") },
                onFailure = { _events.emit(it.message ?: "Remove owner failed") }
            )
        }
    }

    fun deleteUser(targetId: Long) {
        val user = (authGateState.value as? AuthGateState.Active)?.user ?: return
        viewModelScope.launch {
            repo.deleteUser(user, targetId).fold(
                onSuccess = { _events.emit("Account cleared") },
                onFailure = { _events.emit(it.message ?: "Clear failed") }
            )
        }
    }

    fun deleteAllKeys(onDone: () -> Unit) {
        val user = (authGateState.value as? AuthGateState.Active)?.user ?: return
        viewModelScope.launch {
            repo.deleteAllKeys(user).fold(
                onSuccess = { count ->
                    _events.emit("Deleted $count keys permanently")
                    onDone()
                },
                onFailure = { _events.emit(it.message ?: "Global key delete failed") }
            )
        }
    }

    fun deleteAllReferrals(onDone: () -> Unit) {
        val user = (authGateState.value as? AuthGateState.Active)?.user ?: return
        viewModelScope.launch {
            repo.deleteAllReferrals(user).fold(
                onSuccess = { count ->
                    _events.emit("Deleted $count referrals permanently")
                    onDone()
                },
                onFailure = { _events.emit(it.message ?: "Global referral delete failed") }
            )
        }
    }

    fun saveAnnouncement(
        enabled: Boolean,
        title: String,
        message: String,
        startRaw: String,
        endRaw: String,
        displayMode: AnnouncementDisplayMode,
        textSize: AnnouncementTextSize
    ) {
        val user = (authGateState.value as? AuthGateState.Active)?.user ?: return
        val startMs = if (startRaw.isBlank()) null else LicenseValidationEngine.parseUtcOrLocalDateTime(startRaw)
        val endMs = if (endRaw.isBlank()) null else LicenseValidationEngine.parseUtcOrLocalDateTime(endRaw)
        if (startRaw.isNotBlank() && startMs == null) {
            viewModelScope.launch { _events.emit("Invalid start date/time format (use yyyy-MM-dd HH:mm)") }
            return
        }
        if (endRaw.isNotBlank() && endMs == null) {
            viewModelScope.launch { _events.emit("Invalid end date/time format (use yyyy-MM-dd HH:mm)") }
            return
        }
        viewModelScope.launch {
            repo.updateSystemAnnouncement(user, enabled, title, message, startMs, endMs, displayMode, textSize).fold(
                onSuccess = { _events.emit("System announcement saved") },
                onFailure = { _events.emit(it.message ?: "Failed to save announcement") }
            )
        }
    }

    fun saveMaintenance(
        enabled: Boolean,
        title: String,
        message: String,
        startRaw: String,
        endRaw: String
    ) {
        val user = (authGateState.value as? AuthGateState.Active)?.user ?: return
        val startMs = if (startRaw.isBlank()) null else LicenseValidationEngine.parseUtcOrLocalDateTime(startRaw)
        val endMs = if (endRaw.isBlank()) null else LicenseValidationEngine.parseUtcOrLocalDateTime(endRaw)
        if (startRaw.isNotBlank() && startMs == null) {
            viewModelScope.launch { _events.emit("Invalid start date/time format (use yyyy-MM-dd HH:mm)") }
            return
        }
        if (endRaw.isNotBlank() && endMs == null) {
            viewModelScope.launch { _events.emit("Invalid end date/time format (use yyyy-MM-dd HH:mm)") }
            return
        }
        viewModelScope.launch {
            repo.updateServerMaintenance(user, enabled, title, message, startMs, endMs).fold(
                onSuccess = { _events.emit("Server maintenance settings saved") },
                onFailure = { _events.emit(it.message ?: "Failed to save maintenance") }
            )
        }
    }

    fun setTelegramEnabled(enabled: Boolean) {
        val user = (authGateState.value as? AuthGateState.Active)?.user ?: return
        viewModelScope.launch {
            repo.updateTelegramSettings(user, enabled).fold(
                onSuccess = { _events.emit("Telegram extension ${if (enabled) "enabled" else "disabled"}") },
                onFailure = { _events.emit(it.message ?: "Update failed") }
            )
        }
    }

    fun saveTelegramChannel(channelUrl: String, targetUserId: Long? = null, enabled: Boolean = true, onSaved: () -> Unit = {}) {
        val user = (authGateState.value as? AuthGateState.Active)?.user ?: return
        val error = LicenseValidationEngine.validateTelegramHttpsUrl(channelUrl)
        if (error != null) {
            viewModelScope.launch { _events.emit(error) }
            return
        }
        viewModelScope.launch {
            repo.saveTelegramChannel(user, targetUserId ?: user.id, channelUrl, enabled).fold(
                onSuccess = {
                    _events.emit("Telegram channel saved")
                    onSaved()
                },
                onFailure = { _events.emit(it.message ?: "Could not save Telegram channel") }
            )
        }
    }

    fun confirmTelegramScope(accountUserId: Long, ownerUserId: Long, sourceReferralId: Long?) {
        val user = (authGateState.value as? AuthGateState.Active)?.user ?: return
        viewModelScope.launch {
            repo.confirmTelegramScope(user, accountUserId, ownerUserId, sourceReferralId).fold(
                onSuccess = { _events.emit("Telegram account scope confirmed") },
                onFailure = { _events.emit(it.message ?: "Could not confirm scope") }
            )
        }
    }

    fun testValidateLicense(key: String, deviceId: String) {
        viewModelScope.launch {
            _validationTestResult.value = repo.validateCanonicalLicense(key, deviceId)
        }
    }

    private fun currentActiveOrPendingUser(): UserEntity? = when (val state = authGateState.value) {
        is AuthGateState.Active -> state.user
        is AuthGateState.PendingReferral -> state.user
        else -> null
    }
}
