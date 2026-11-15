package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.local.UserEntity
import com.example.domain.LicenseValidationEngine
import com.example.domain.UserRole
import com.example.ui.components.CyberDangerButton
import com.example.ui.components.CyberOutlineButton
import com.example.ui.components.CyberPrimaryButton
import com.example.ui.components.EyebrowText
import com.example.ui.components.HudPanelCard
import com.example.ui.components.StatusPill
import com.example.ui.components.licenseStatusColor
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberPink
import com.example.ui.theme.CyberViolet
import com.example.ui.theme.DisplayFontFamily
import com.example.ui.theme.HudBorder
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.LicenseXViewModel

@Composable
fun SuperOwnerOverviewScreen(
    user: UserEntity,
    viewModel: LicenseXViewModel,
    onNavigateBack: () -> Unit,
    onNavigateAllKeys: () -> Unit
) {
    if (UserRole.fromWire(user.role) != UserRole.SUPER_OWNER) {
        RestrictedSuperOwnerCard(onNavigateBack)
        return
    }

    val allKeys by viewModel.allLicenses.collectAsStateWithLifecycle()
    val allReferrals by viewModel.allReferrals.collectAsStateWithLifecycle()
    val referralOverview by viewModel.referralOverview.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf("referrals") }
    var confirmType by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Box(modifier = Modifier.widthIn(max = 960.dp).fillMaxWidth()) {
                HudPanelCard {
                    EyebrowText("SUPER OWNER // SEASON CONTROL")
                    Spacer(Modifier.height(4.dp))
                    Text(
                        stringResource(R.string.super_owner_overview_title),
                        style = MaterialTheme.typography.displayLarge,
                        color = TextPrimary
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Review every referral and every generated key across all Owners and Admins. Global deletion records an audit event.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CyberOutlineButton(
                            text = "RETURN TO DASHBOARD",
                            leadingIcon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            onClick = onNavigateBack
                        )
                        CyberOutlineButton(
                            text = "ALL PANEL KEYS",
                            leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            onClick = onNavigateAllKeys
                        )
                    }
                }
            }
        }

        item {
            Box(modifier = Modifier.widthIn(max = 960.dp).fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    HudPanelCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("ALL PANEL KEYS", style = MaterialTheme.typography.labelMedium, color = CyberCyan)
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    allKeys.size.toString().padStart(2, '0'),
                                    fontFamily = DisplayFontFamily,
                                    fontSize = 36.sp,
                                    color = TextPrimary
                                )
                            }
                            Icon(Icons.Default.Key, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(28.dp))
                        }
                        Spacer(Modifier.height(14.dp))
                        CyberDangerButton(
                            text = "DELETE ALL KEYS",
                            leadingIcon = { Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            enabled = allKeys.isNotEmpty(),
                            onClick = { confirmType = "keys" },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("delete_all_keys_button")
                        )
                    }

                    HudPanelCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("ALL REFERRALS", style = MaterialTheme.typography.labelMedium, color = CyberViolet)
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    allReferrals.size.toString().padStart(2, '0'),
                                    fontFamily = DisplayFontFamily,
                                    fontSize = 36.sp,
                                    color = TextPrimary
                                )
                            }
                            Icon(Icons.Default.Group, contentDescription = null, tint = CyberViolet, modifier = Modifier.size(28.dp))
                        }
                        Spacer(Modifier.height(14.dp))
                        CyberDangerButton(
                            text = "DELETE ALL REFERRALS",
                            leadingIcon = { Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            enabled = allReferrals.isNotEmpty(),
                            onClick = { confirmType = "referrals" },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("delete_all_referrals_button")
                        )
                    }
                }
            }
        }

        item {
            Box(modifier = Modifier.widthIn(max = 960.dp).fillMaxWidth()) {
                HudPanelCard {
                    EyebrowText("GLOBAL RECORDS")
                    Spacer(Modifier.height(4.dp))
                    Text("SEASON VISIBILITY", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        if (selectedTab == "referrals") {
                            CyberPrimaryButton(
                                text = "REFERRALS",
                                leadingIcon = { Icon(Icons.Default.BarChart, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                onClick = { selectedTab = "referrals" },
                                modifier = Modifier.weight(1f)
                            )
                            CyberOutlineButton(
                                text = "ALL KEYS",
                                leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                onClick = { selectedTab = "keys" },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            CyberOutlineButton(
                                text = "REFERRALS",
                                leadingIcon = { Icon(Icons.Default.BarChart, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                onClick = { selectedTab = "referrals" },
                                modifier = Modifier.weight(1f)
                            )
                            CyberPrimaryButton(
                                text = "ALL KEYS",
                                leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                onClick = { selectedTab = "keys" },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        if (selectedTab == "referrals") {
            if (referralOverview.isEmpty()) {
                item {
                    EmptyPanelCard("No owner or admin accounts are available for referral reporting.")
                }
            } else {
                items(referralOverview, key = { it.userId }) { row ->
                    Box(modifier = Modifier.widthIn(max = 960.dp).fillMaxWidth()) {
                        HudPanelCard(contentPadding = PaddingValues(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(row.creatorName, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                                StatusPill(
                                    row.role.label,
                                    if (row.role == UserRole.SUPER_OWNER) CyberEmerald else CyberCyan
                                )
                            }
                            Spacer(Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total: ${row.total}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text("Active: ${row.active}", style = MaterialTheme.typography.bodySmall, color = CyberEmerald)
                                Text("Redeemed: ${row.redeemed}", style = MaterialTheme.typography.bodySmall, color = CyberCyan)
                                Text("Revoked: ${row.revoked}", style = MaterialTheme.typography.bodySmall, color = CyberPink)
                            }
                        }
                    }
                }
            }
        } else {
            if (allKeys.isEmpty()) {
                item {
                    EmptyPanelCard("The panel currently has zero keys.")
                }
            } else {
                items(allKeys, key = { it.id }) { item ->
                    val shownDevices = maxOf(item.usedDevices, if (item.boundDeviceId != null) 1 else 0)
                    Box(modifier = Modifier.widthIn(max = 960.dp).fillMaxWidth()) {
                        HudPanelCard(contentPadding = PaddingValues(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(item.creatorDisplayName, style = MaterialTheme.typography.bodyMedium, color = CyberCyan)
                                StatusPill(item.effectiveStatus.wire, licenseStatusColor(item.effectiveStatus))
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(item.keyValue, fontFamily = MonoFontFamily, fontSize = 12.sp, color = TextPrimary)
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "Role: ${item.creatorRole?.label ?: "legacy"} · Devices: $shownDevices/${item.maxDevices}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                            Text(
                                if (item.activatedAtMillis != null) {
                                    "Activated ${LicenseValidationEngine.formatTimestamp(item.activatedAtMillis)} · Expires ${LicenseValidationEngine.formatTimestamp(item.expiresAtMillis)}"
                                } else {
                                    "Pending activation"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = if (item.activatedAtMillis != null) TextSecondary else CyberAmber
                            )
                        }
                    }
                }
            }
        }
    }

    confirmType?.let { target ->
        val count = if (target == "keys") allKeys.size else allReferrals.size
        ConfirmDangerDialog(
            title = "CONFIRM GLOBAL DELETE",
            message = if (target == "keys") {
                "Delete $count keys permanently? Every license key and its device bindings will be removed."
            } else {
                "Delete $count referrals permanently? Every current referral code will be removed."
            },
            confirmText = "YES, DELETE $count",
            onConfirm = {
                if (target == "keys") viewModel.deleteAllKeys { confirmType = null }
                else viewModel.deleteAllReferrals { confirmType = null }
            },
            onDismiss = { confirmType = null }
        )
    }
}

@Composable
fun AllPanelKeysScreen(
    user: UserEntity,
    viewModel: LicenseXViewModel,
    onNavigateBack: () -> Unit
) {
    if (UserRole.fromWire(user.role) != UserRole.SUPER_OWNER) {
        RestrictedSuperOwnerCard(onNavigateBack)
        return
    }
    val keys by viewModel.allLicenses.collectAsStateWithLifecycle()
    var confirmOpen by remember { mutableStateOf(false) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Box(modifier = Modifier.widthIn(max = 960.dp).fillMaxWidth()) {
                HudPanelCard {
                    EyebrowText("SUPER OWNER // COMPLETE REGISTRY")
                    Spacer(Modifier.height(4.dp))
                    Text(stringResource(R.string.all_panel_keys_title), style = MaterialTheme.typography.displayLarge, color = TextPrimary)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Every license key created across the panel, including Owner and Admin records.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        CyberOutlineButton(
                            text = "BACK",
                            leadingIcon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            onClick = onNavigateBack,
                            modifier = Modifier.weight(1f)
                        )
                        CyberDangerButton(
                            text = "DELETE ALL (${keys.size})",
                            enabled = keys.isNotEmpty(),
                            onClick = { confirmOpen = true },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        if (keys.isEmpty()) {
            item { EmptyPanelCard("No panel keys are available.") }
        } else {
            items(keys, key = { it.id }) { item ->
                val shownDevices = maxOf(item.usedDevices, if (item.boundDeviceId != null) 1 else 0)
                Box(modifier = Modifier.widthIn(max = 960.dp).fillMaxWidth()) {
                    Surface(
                        color = Color(0xFF101725),
                        border = BorderStroke(1.dp, HudBorder),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(item.creatorDisplayName, style = MaterialTheme.typography.bodyMedium, color = CyberCyan)
                                StatusPill(item.effectiveStatus.wire, licenseStatusColor(item.effectiveStatus))
                            }
                            Text(item.keyValue, fontFamily = MonoFontFamily, fontSize = 12.sp, color = TextPrimary)
                            Text(
                                "Role: ${item.creatorRole?.label ?: "legacy"} · Devices: $shownDevices/${item.maxDevices}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }
    }

    if (confirmOpen) {
        ConfirmDangerDialog(
            title = "DELETE ALL PANEL KEYS?",
            message = "This will permanently delete all ${keys.size} keys and all associated device bindings.",
            confirmText = "YES, DELETE ALL KEYS",
            onConfirm = { viewModel.deleteAllKeys { confirmOpen = false } },
            onDismiss = { confirmOpen = false }
        )
    }
}

@Composable
fun RestrictedSuperOwnerCard(onNavigateBack: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        HudPanelCard(modifier = Modifier.widthIn(max = 500.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = CyberPink, modifier = Modifier.size(40.dp))
                Spacer(Modifier.height(12.dp))
                EyebrowText("RESTRICTED NODE", color = CyberPink)
                Spacer(Modifier.height(6.dp))
                Text("SUPER OWNER ONLY", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                Spacer(Modifier.height(8.dp))
                Text(
                    "This section is available only to the active configured Super Owner account.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(Modifier.height(16.dp))
                CyberOutlineButton(text = "RETURN TO DASHBOARD", onClick = onNavigateBack)
            }
        }
    }
}

@Composable
private fun EmptyPanelCard(text: String) {
    Box(modifier = Modifier.widthIn(max = 960.dp).fillMaxWidth()) {
        HudPanelCard {
            Text(text, style = MaterialTheme.typography.bodyMedium, color = TextMuted)
        }
    }
}
