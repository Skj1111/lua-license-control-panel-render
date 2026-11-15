package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.AuditLogEntity
import com.example.data.local.ReferralEntity
import com.example.data.local.UserEntity
import com.example.domain.AccessStatus
import com.example.domain.DurationUnit
import com.example.domain.LicenseStatus
import com.example.domain.LicenseUiModel
import com.example.domain.LicenseValidationEngine
import com.example.domain.ReferralStatus
import com.example.domain.UserRole
import com.example.domain.ValidationResponse
import com.example.ui.components.CyberOutlineButton
import com.example.ui.components.CyberPrimaryButton
import com.example.ui.components.CyberTextField
import com.example.ui.components.EyebrowText
import com.example.ui.components.HudPanelCard
import com.example.ui.components.SectionDivider
import com.example.ui.components.StatusPill
import com.example.ui.components.licenseStatusColor
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberPink
import com.example.ui.theme.CyberViolet
import com.example.ui.theme.HudBorder
import com.example.ui.theme.InputSurface
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun GenerateKeyCard(
    onGenerate: (
        durationAmount: String,
        unit: DurationUnit,
        maxDevices: String,
        customEnabled: Boolean,
        customPrefix: String
    ) -> Unit
) {
    var durationAmount by remember { mutableStateOf("30") }
    var durationUnit by remember { mutableStateOf(DurationUnit.DAYS) }
    var unitExpanded by remember { mutableStateOf(false) }
    var maxDevices by remember { mutableStateOf("1") }
    var customEnabled by remember { mutableStateOf(false) }
    var customPrefix by remember { mutableStateOf("") }

    HudPanelCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = CyberCyan.copy(alpha = 0.14f),
                border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.45f)),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = CyberCyan)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column {
                EyebrowText("PROVISION ACCESS")
                Text("GENERATE KEY", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            }
        }
        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth()
        ) {
            CyberTextField(
                value = durationAmount,
                onValueChange = { durationAmount = it },
                label = "Duration",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .testTag("duration_amount_input")
            )
            Column(modifier = Modifier.weight(1f)) {
                Text("UNIT", style = MaterialTheme.typography.labelMedium, color = CyberCyan)
                Spacer(Modifier.height(6.dp))
                Box {
                    Surface(
                        color = InputSurface,
                        border = BorderStroke(1.dp, Color(0x4D67E8F9)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .clickable { unitExpanded = true }
                    ) {
                        Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.padding(horizontal = 14.dp)) {
                            Text(durationUnit.label, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        }
                    }
                    DropdownMenu(expanded = unitExpanded, onDismissRequest = { unitExpanded = false }) {
                        DurationUnit.entries.forEach { unit ->
                            DropdownMenuItem(
                                text = { Text(unit.label) },
                                onClick = {
                                    durationUnit = unit
                                    unitExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            "Timer starts only when the first device validates. Months are fixed at 30 days.",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )

        Spacer(Modifier.height(14.dp))
        CyberTextField(
            value = maxDevices,
            onValueChange = { maxDevices = it },
            label = "Maximum devices",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.testTag("max_devices_input")
        )

        Spacer(Modifier.height(12.dp))
        Surface(
            color = CyberCyan.copy(alpha = 0.05f),
            border = BorderStroke(1.dp, HudBorder),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("EXACT CUSTOM KEY", style = MaterialTheme.typography.labelMedium, color = CyberCyan)
                    Switch(
                        checked = customEnabled,
                        onCheckedChange = { customEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyberCyan,
                            checkedTrackColor = CyberCyan.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier.testTag("custom_key_switch")
                    )
                }
                if (customEnabled) {
                    Spacer(Modifier.height(8.dp))
                    CyberTextField(
                        value = customPrefix,
                        onValueChange = { customPrefix = it },
                        label = "Custom key",
                        placeholder = "NEXA-VIP-01",
                        modifier = Modifier.testTag("custom_key_input")
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Type 4–64 characters. Generated key matches your input verbatim.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        CyberPrimaryButton(
            text = "GENERATE LICENSE",
            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp)) },
            enabled = durationAmount.isNotBlank() && maxDevices.isNotBlank() && (!customEnabled || customPrefix.isNotBlank()),
            onClick = { onGenerate(durationAmount, durationUnit, maxDevices, customEnabled, customPrefix) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("generate_license_button")
        )
    }
}

@Composable
fun CreateReferralCard(
    referrals: List<ReferralEntity>,
    onCreateReferral: (UserRole, String) -> Unit,
    onCopyCode: (String) -> Unit,
    onRevokeReferral: (Long) -> Unit,
    onDeleteReferral: (Long) -> Unit
) {
    var days by remember { mutableStateOf("30") }
    var selectedRole by remember { mutableStateOf(UserRole.ADMIN) }
    var roleExpanded by remember { mutableStateOf(false) }

    HudPanelCard {
        EyebrowText("OWNER CONTROL")
        Spacer(Modifier.height(4.dp))
        Text("CREATE REFERRAL", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        Spacer(Modifier.height(14.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth()
        ) {
            CyberTextField(
                value = days,
                onValueChange = { days = it },
                label = "Days",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(0.9f)
            )
            Column(modifier = Modifier.weight(1.1f)) {
                Text("ROLE", style = MaterialTheme.typography.labelMedium, color = CyberCyan)
                Spacer(Modifier.height(6.dp))
                Box {
                    Surface(
                        color = InputSurface,
                        border = BorderStroke(1.dp, Color(0x4D67E8F9)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .clickable { roleExpanded = true }
                    ) {
                        Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.padding(horizontal = 14.dp)) {
                            Text("${selectedRole.label} access", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        }
                    }
                    DropdownMenu(expanded = roleExpanded, onDismissRequest = { roleExpanded = false }) {
                        listOf(UserRole.ADMIN, UserRole.OWNER).forEach { roleOption ->
                            DropdownMenuItem(
                                text = { Text("${roleOption.label} access") },
                                onClick = {
                                    selectedRole = roleOption
                                    roleExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        CyberPrimaryButton(
            text = "CREATE REFERRAL",
            onClick = { onCreateReferral(selectedRole, days) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("create_referral_button")
        )

        if (referrals.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            SectionDivider()
            Spacer(Modifier.height(12.dp))
            referrals.take(8).forEach { ref ->
                val now = System.currentTimeMillis()
                val expired = ref.expiresAtMillis <= now
                val stale = ref.status != ReferralStatus.ACTIVE.wire || expired
                val displayStatus = if (expired && ref.status == ReferralStatus.ACTIVE.wire) "expired" else ref.status

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            ref.code,
                            color = CyberCyan,
                            fontFamily = MonoFontFamily,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            "${ref.role} · $displayStatus · exp ${LicenseValidationEngine.formatTimestamp(ref.expiresAtMillis)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { onCopyCode(ref.code) }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy referral", tint = CyberCyan, modifier = Modifier.size(18.dp))
                        }
                        if (ref.status == ReferralStatus.ACTIVE.wire && !expired) {
                            TextButton(onClick = { onRevokeReferral(ref.id) }) {
                                Text("REVOKE", color = CyberPink, fontSize = 11.sp)
                            }
                        }
                        if (stale) {
                            IconButton(onClick = { onDeleteReferral(ref.id) }) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Clear referral", tint = CyberPink, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KeyRegistryCard(
    keys: List<LicenseUiModel>,
    isOwner: Boolean,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSelectBindings: (Long) -> Unit,
    onCopyKeyJson: (String) -> Unit,
    onToggleRevoke: (LicenseUiModel) -> Unit,
    onManageKey: (LicenseUiModel) -> Unit,
    onDeleteKey: (Long) -> Unit
) {
    val filtered = remember(keys, searchQuery) {
        val q = searchQuery.trim().lowercase()
        if (q.isEmpty()) keys else keys.filter { it.keyValue.lowercase().contains(q) }
    }

    HudPanelCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                EyebrowText("YOUR KEY REGISTRY")
                Text("KEYS YOU GENERATED", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            }
            StatusPill("${filtered.size} KEYS", CyberCyan)
        }
        Spacer(Modifier.height(12.dp))
        CyberTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            label = "Search key",
            placeholder = "Filter by key prefix or token…",
            trailingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = CyberCyan)
            },
            modifier = Modifier.testTag("search_key_input")
        )
        Spacer(Modifier.height(14.dp))

        if (filtered.isEmpty()) {
            Text(
                "No keys generated by this account yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                modifier = Modifier.padding(vertical = 20.dp)
            )
        } else {
            filtered.forEach { key ->
                val shownDevices = maxOf(key.usedDevices, if (key.boundDeviceId != null) 1 else 0)
                Surface(
                    color = Color(0x0A00EEFF),
                    border = BorderStroke(1.dp, HudBorder),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = CyberCyan.copy(alpha = 0.08f),
                                border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.35f)),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier
                                    .weight(1f, fill = false)
                                    .clickable { onSelectBindings(key.id) }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                ) {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(13.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        key.keyValue,
                                        color = TextPrimary,
                                        fontFamily = MonoFontFamily,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            Spacer(Modifier.width(8.dp))
                            StatusPill(key.effectiveStatus.wire, licenseStatusColor(key.effectiveStatus))
                        }

                        Spacer(Modifier.height(10.dp))
                        if (key.activatedAtMillis != null) {
                            Text(
                                "activated ${LicenseValidationEngine.formatTimestamp(key.activatedAtMillis)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = CyberCyan
                            )
                            Text(
                                "expires ${LicenseValidationEngine.formatTimestamp(key.expiresAtMillis)} · ${LicenseValidationEngine.formatDuration(key.durationMinutes)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        } else {
                            Text("pending activation", style = MaterialTheme.typography.bodySmall, color = CyberAmber)
                            Text(
                                "${LicenseValidationEngine.formatDuration(key.durationMinutes)} from first device",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }

                        Spacer(Modifier.height(10.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(onClick = { onSelectBindings(key.id) }) {
                                Text("DEVICES $shownDevices/${key.maxDevices}", color = CyberCyan, fontSize = 11.sp)
                            }
                            IconButton(onClick = { onCopyKeyJson(key.keyValue) }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = stringResource(R.string.cd_copy_key), tint = CyberCyan, modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = { onToggleRevoke(key) }) {
                                Icon(
                                    imageVector = if (key.effectiveStatus == LicenseStatus.ACTIVE) Icons.Default.Shield else Icons.Default.VerifiedUser,
                                    contentDescription = stringResource(R.string.cd_toggle_key_status),
                                    tint = if (key.effectiveStatus == LicenseStatus.ACTIVE) CyberAmber else CyberEmerald,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            TextButton(onClick = { onManageKey(key) }) {
                                Text("MANAGE", color = CyberCyan, fontSize = 11.sp)
                            }
                            if (isOwner) {
                                IconButton(onClick = { onDeleteKey(key.id) }) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = stringResource(R.string.cd_delete_key), tint = CyberPink, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserRolesCard(
    currentUser: UserEntity,
    users: List<UserEntity>,
    onToggleAdminAccess: (UserEntity) -> Unit,
    onRemoveOwnerPrompt: (Long) -> Unit,
    onClearUser: (Long) -> Unit
) {
    val isSuperOwner = UserRole.fromWire(currentUser.role) == UserRole.SUPER_OWNER
    val now = System.currentTimeMillis()

    HudPanelCard {
        EyebrowText("IDENTITY CONTROL")
        Spacer(Modifier.height(4.dp))
        Text("USER ROLES", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        Spacer(Modifier.height(4.dp))
        Text(
            "Only the configured Super Owner can remove another Owner account.",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )
        Spacer(Modifier.height(12.dp))

        users.forEach { account ->
            val role = UserRole.fromWire(account.role)
            val expired = account.accessExpiresAtMillis != null && account.accessExpiresAtMillis <= now
            val clearable = account.id != currentUser.id && (account.accessStatus == AccessStatus.REVOKED.wire || expired)
            val displayName = account.username ?: account.name ?: account.email ?: account.openId
            val activeOk = account.accessStatus == AccessStatus.ACTIVE.wire && !expired
            val statusText = "${role.label} · ${if (expired && account.accessStatus == AccessStatus.ACTIVE.wire) "expired" else account.accessStatus}"

            SectionDivider()
            Column(modifier = Modifier.padding(vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(displayName, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        Text(
                            "last seen ${LicenseValidationEngine.formatTimestamp(account.lastSignedInMillis)} · exp ${LicenseValidationEngine.formatTimestamp(account.accessExpiresAtMillis)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                    StatusPill(statusText, if (activeOk) CyberEmerald else CyberPink)
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (role == UserRole.ADMIN && account.id != currentUser.id) {
                        CyberOutlineButton(
                            text = if (activeOk) "REVOKE" else "RESTORE",
                            leadingIcon = { Icon(Icons.Default.ManageAccounts, contentDescription = null, modifier = Modifier.size(14.dp)) },
                            onClick = { onToggleAdminAccess(account) }
                        )
                    }
                    if (isSuperOwner && role == UserRole.OWNER && account.id != currentUser.id) {
                        TextButton(onClick = { onRemoveOwnerPrompt(account.id) }) {
                            Text("REMOVE OWNER", color = CyberPink, fontSize = 11.sp)
                        }
                    }
                    if (clearable) {
                        TextButton(onClick = { onClearUser(account.id) }) {
                            Text("CLEAR", color = CyberPink, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AuditLogCard(logs: List<AuditLogEntity>) {
    HudPanelCard {
        EyebrowText("SECURITY HISTORY")
        Spacer(Modifier.height(4.dp))
        Text("AUDIT LOG", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        Spacer(Modifier.height(4.dp))
        Text(
            "Showing the most recent ${LicenseValidationEngine.RECENT_AUDIT_LOG_LIMIT} events.",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )
        Spacer(Modifier.height(12.dp))

        if (logs.isEmpty()) {
            Text("No audit events recorded yet.", style = MaterialTheme.typography.bodySmall, color = TextMuted)
        } else {
            logs.forEach { item ->
                SectionDivider()
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        "${item.action.replace('_', ' ')}${item.keyPreview?.let { " · $it" } ?: ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CyberCyan
                    )
                    Text(
                        "${item.actorName ?: "system"} · ${LicenseValidationEngine.formatTimestamp(item.createdAtMillis)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    if (!item.details.isNullOrBlank()) {
                        Text(item.details, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
fun ValidationTransportCard(
    lastResult: ValidationResponse?,
    onValidateKey: (key: String, deviceId: String) -> Unit
) {
    var testKey by remember { mutableStateOf("") }
    var testDeviceId by remember { mutableStateOf("ANDROID-EMU-001") }

    HudPanelCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                EyebrowText("API DIAGNOSTICS // LIVE ENGINE")
                Text("VALIDATION TRANSPORT", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            }
            StatusPill("READY", CyberEmerald)
        }
        Spacer(Modifier.height(10.dp))
        Surface(
            color = CyberCyan.copy(alpha = 0.05f),
            border = BorderStroke(1.dp, HudBorder),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("CANONICAL VALIDATION", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                Text("POST /api/license/validate · POST /v1/validate", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                Text("Statuses: valid · expired · invalid · not_registered · device_mismatch · device_max_reached", style = MaterialTheme.typography.bodySmall, color = TextMuted)
            }
        }
        Spacer(Modifier.height(12.dp))
        CyberTextField(
            value = testKey,
            onValueChange = { testKey = it },
            label = "Test license key",
            placeholder = "Paste a generated key",
            modifier = Modifier.testTag("test_validate_key_input")
        )
        Spacer(Modifier.height(10.dp))
        CyberTextField(
            value = testDeviceId,
            onValueChange = { testDeviceId = it },
            label = "Test device ID",
            placeholder = "ANDROID-EMU-001",
            modifier = Modifier.testTag("test_validate_device_input")
        )
        Spacer(Modifier.height(12.dp))
        CyberPrimaryButton(
            text = "RUN VALIDATION CHECK",
            leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp)) },
            enabled = testKey.isNotBlank() && testDeviceId.isNotBlank(),
            onClick = { onValidateKey(testKey, testDeviceId) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("run_validation_button")
        )
        if (lastResult != null) {
            Spacer(Modifier.height(12.dp))
            val accent = if (lastResult.success) CyberEmerald else CyberPink
            Surface(
                color = accent.copy(alpha = 0.09f),
                border = BorderStroke(1.dp, accent.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "STATUS: ${lastResult.status.wire.uppercase()} (success=${lastResult.success})",
                        color = accent,
                        fontFamily = MonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(lastResult.message, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                    if (lastResult.expiresAtMillis != null) {
                        Text(
                            "expiresAt: ${LicenseValidationEngine.formatTimestamp(lastResult.expiresAtMillis)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}
