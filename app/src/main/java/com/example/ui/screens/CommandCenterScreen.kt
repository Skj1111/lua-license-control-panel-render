package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.local.UserEntity
import com.example.domain.LicenseUiModel
import com.example.domain.LicenseValidationEngine
import com.example.domain.UserRole
import com.example.ui.components.CyberOutlineButton
import com.example.ui.components.EyebrowText
import com.example.ui.components.HudPanelCard
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberPink
import com.example.ui.theme.CyberViolet
import com.example.ui.theme.DisplayFontFamily
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.LicenseXViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CommandCenterScreen(
    user: UserEntity,
    viewModel: LicenseXViewModel,
    onNavigateSuperOwner: () -> Unit,
    onNavigateSystemControl: () -> Unit,
    onNavigateAnnouncementControl: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    val role = UserRole.fromWire(user.role)
    val isSuperOwner = role == UserRole.SUPER_OWNER
    val isOwner = role.isOwnerLevel

    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val keys by viewModel.visibleHomeLicenses.collectAsStateWithLifecycle()
    val referrals by viewModel.allReferrals.collectAsStateWithLifecycle()
    val users by viewModel.allUsers.collectAsStateWithLifecycle()
    val auditLogs by viewModel.recentAuditLogs.collectAsStateWithLifecycle()
    val telegramSettings by viewModel.telegramSettings.collectAsStateWithLifecycle()
    val myTelegramChannel by viewModel.myTelegramChannel.collectAsStateWithLifecycle()
    val telegramSetupState by viewModel.telegramSetupState.collectAsStateWithLifecycle()
    val managedChannels by viewModel.managedTelegramChannels.collectAsStateWithLifecycle()
    val scopeCandidates by viewModel.telegramScopeCandidates.collectAsStateWithLifecycle()
    val selectedBindingsKeyId by viewModel.selectedKeyForBindings.collectAsStateWithLifecycle()
    val bindings by viewModel.selectedKeyBindings.collectAsStateWithLifecycle()
    val validationResult by viewModel.validationTestResult.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var managingKey by remember { mutableStateOf<LicenseUiModel?>(null) }
    var confirmDeleteKeyId by remember { mutableStateOf<Long?>(null) }
    var confirmRemoveOwnerId by remember { mutableStateOf<Long?>(null) }
    var usernameDialogOpen by remember { mutableStateOf(false) }
    var usernameInitialSetup by remember { mutableStateOf(false) }

    LaunchedEffect(user.username) {
        if (user.username.isNullOrBlank()) {
            usernameInitialSetup = true
            usernameDialogOpen = true
        }
    }

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
    }

    val usernameChangedAt = user.usernameChangedAtMillis
    val nextUsernameChangeAt = usernameChangedAt?.plus(LicenseValidationEngine.USERNAME_CHANGE_COOLDOWN_MS)
    val canEditUsername = nextUsernameChangeAt == null || System.currentTimeMillis() >= nextUsernameChangeAt

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Box(modifier = Modifier.widthIn(max = 960.dp).fillMaxWidth()) {
                HudPanelCard(contentPadding = PaddingValues(0.dp)) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = painterResource(id = R.drawable.img_hud_banner),
                            contentDescription = stringResource(R.string.cd_hud_banner),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .alpha(0.45f)
                        )
                        Column(modifier = Modifier.padding(20.dp)) {
                            EyebrowText("SECURE LICENSE NETWORK / ONLINE")
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = stringResource(R.string.command_center_title),
                                style = MaterialTheme.typography.displayLarge,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = stringResource(R.string.command_center_subtitle),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                            if (isSuperOwner) {
                                Spacer(Modifier.height(14.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CyberOutlineButton(
                                        text = "SUPER OWNER OVERVIEW",
                                        leadingIcon = { Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                        onClick = onNavigateSuperOwner,
                                        modifier = Modifier.testTag("nav_super_owner_button")
                                    )
                                    CyberOutlineButton(
                                        text = "SYSTEM CONTROL",
                                        leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                        onClick = onNavigateSystemControl,
                                        modifier = Modifier.testTag("nav_system_control_button")
                                    )
                                    CyberOutlineButton(
                                        text = "ANNOUNCEMENT CONTROL",
                                        leadingIcon = { Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                        onClick = onNavigateAnnouncementControl,
                                        modifier = Modifier.testTag("nav_announcement_control_button")
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!user.username.isNullOrBlank()) {
            item {
                Box(modifier = Modifier.widthIn(max = 960.dp).fillMaxWidth()) {
                    HudPanelCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                EyebrowText("PANEL IDENTITY")
                                Spacer(Modifier.height(4.dp))
                                Text(user.username, style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Key prefix: ${LicenseValidationEngine.usernameToKeyPrefix(user.username)}-… · " +
                                        if (canEditUsername) "Username change available."
                                        else "Next change: ${LicenseValidationEngine.formatTimestamp(nextUsernameChangeAt)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                            Spacer(Modifier.widthIn(min = 10.dp))
                            CyberOutlineButton(
                                text = "EDIT",
                                enabled = canEditUsername,
                                onClick = {
                                    usernameInitialSetup = false
                                    usernameDialogOpen = true
                                },
                                modifier = Modifier.testTag("edit_username_button")
                            )
                        }
                    }
                }
            }
        }

        item {
            Box(modifier = Modifier.widthIn(max = 960.dp).fillMaxWidth()) {
                TelegramExtensionCard(
                    role = role,
                    extensionEnabled = telegramSettings?.enabled ?: false,
                    myChannel = myTelegramChannel,
                    setupState = telegramSetupState,
                    managedChannels = managedChannels,
                    scopeCandidates = scopeCandidates,
                    onToggleExtension = { viewModel.setTelegramEnabled(it) },
                    onSaveChannel = { url, targetId, enabled, onDone ->
                        viewModel.saveTelegramChannel(url, targetId, enabled, onDone)
                    },
                    onConfirmScope = { accId, ownerId, refId ->
                        viewModel.confirmTelegramScope(accId, ownerId, refId)
                    }
                )
            }
        }

        if (isOwner) {
            item {
                Box(modifier = Modifier.widthIn(max = 960.dp).fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            StatTile("TOTAL KEYS", stats.totalKeys, Icons.Default.Key, CyberCyan, Modifier.weight(1f))
                            StatTile("ACTIVE KEYS", stats.activeKeys, Icons.Default.VerifiedUser, CyberEmerald, Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            StatTile("REVOKED KEYS", stats.revokedKeys, Icons.Default.Shield, CyberPink, Modifier.weight(1f))
                            StatTile("DEVICE BINDINGS", stats.totalDeviceBindings, Icons.Default.LinkOff, CyberViolet, Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        item {
            Box(modifier = Modifier.widthIn(max = 960.dp).fillMaxWidth()) {
                GenerateKeyCard { amount, unit, maxDev, customEnabled, customPrefix ->
                    viewModel.generateKey(amount, unit, maxDev, customEnabled, customPrefix) { generatedKey ->
                        copyToClipboard("LicenseX Key", "{\"key\":\"$generatedKey\"}")
                    }
                }
            }
        }

        if (isOwner) {
            item {
                Box(modifier = Modifier.widthIn(max = 960.dp).fillMaxWidth()) {
                    CreateReferralCard(
                        referrals = referrals,
                        onCreateReferral = { r, days -> viewModel.createReferral(r, days) },
                        onCopyCode = { code ->
                            copyToClipboard("Referral Code", code)
                            onShowMessage("Referral copied: $code")
                        },
                        onRevokeReferral = { viewModel.revokeReferral(it) },
                        onDeleteReferral = { viewModel.deleteReferral(it) }
                    )
                }
            }
        }

        item {
            Box(modifier = Modifier.widthIn(max = 960.dp).fillMaxWidth()) {
                KeyRegistryCard(
                    keys = keys,
                    isOwner = isOwner,
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    onSelectBindings = { viewModel.selectKeyForBindings(it) },
                    onCopyKeyJson = { keyVal ->
                        val payload = "{\"key\":\"$keyVal\"}"
                        copyToClipboard("LicenseX Key", payload)
                        onShowMessage("Copied JSON: $payload")
                    },
                    onToggleRevoke = { viewModel.toggleKeyRevoked(it) },
                    onManageKey = { managingKey = it },
                    onDeleteKey = { confirmDeleteKeyId = it }
                )
            }
        }

        if (isOwner) {
            item {
                Box(modifier = Modifier.widthIn(max = 960.dp).fillMaxWidth()) {
                    UserRolesCard(
                        currentUser = user,
                        users = users,
                        onToggleAdminAccess = { viewModel.toggleAdminAccess(it) },
                        onRemoveOwnerPrompt = { confirmRemoveOwnerId = it },
                        onClearUser = { viewModel.deleteUser(it) }
                    )
                }
            }
            item {
                Box(modifier = Modifier.widthIn(max = 960.dp).fillMaxWidth()) {
                    AuditLogCard(logs = auditLogs)
                }
            }
            item {
                Box(modifier = Modifier.widthIn(max = 960.dp).fillMaxWidth()) {
                    ValidationTransportCard(
                        lastResult = validationResult,
                        onValidateKey = { k, dev -> viewModel.testValidateLicense(k, dev) }
                    )
                }
            }
        }
    }

    if (selectedBindingsKeyId != null) {
        BoundDevicesDialog(
            bindings = bindings,
            onUnbind = { viewModel.unbindDevice(it) },
            onDismiss = { viewModel.selectKeyForBindings(null) }
        )
    }

    managingKey?.let { key ->
        ManageLicenseDialog(
            license = key,
            onSave = { status, reqExpiry, expiryRaw, maxDev ->
                viewModel.updateLicense(key.id, status, reqExpiry, expiryRaw, maxDev) {
                    managingKey = null
                }
            },
            onDismiss = { managingKey = null }
        )
    }

    confirmDeleteKeyId?.let { keyId ->
        ConfirmDangerDialog(
            title = "DELETE LICENSE?",
            message = "This permanently removes the key and all of its device bindings.",
            confirmText = "DELETE KEY",
            onConfirm = {
                viewModel.deleteKey(keyId)
                confirmDeleteKeyId = null
            },
            onDismiss = { confirmDeleteKeyId = null }
        )
    }

    confirmRemoveOwnerId?.let { targetId ->
        ConfirmDangerDialog(
            title = "REMOVE OWNER ACCESS?",
            message = "This revokes the selected Owner account and changes its role to Admin. Audit history is retained.",
            confirmText = "REMOVE OWNER",
            onConfirm = {
                viewModel.removeOwnerAccess(targetId)
                confirmRemoveOwnerId = null
            },
            onDismiss = { confirmRemoveOwnerId = null }
        )
    }

    if (usernameDialogOpen) {
        UsernameDialog(
            isInitialSetup = usernameInitialSetup,
            currentEmailOrId = user.email ?: user.openId,
            initialValue = user.username ?: "",
            onSave = { requested ->
                viewModel.saveUsername(requested) {
                    usernameDialogOpen = false
                }
            },
            onDismiss = { usernameDialogOpen = false }
        )
    }
}

@Composable
private fun StatTile(
    label: String,
    value: Int,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    HudPanelCard(modifier = modifier, contentPadding = PaddingValues(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = CyberCyan)
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = value.toString().padStart(2, '0'),
            fontFamily = DisplayFontFamily,
            fontSize = 34.sp,
            color = TextPrimary
        )
    }
}
