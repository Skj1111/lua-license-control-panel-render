package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.local.TelegramChannelEntity
import com.example.domain.LicenseValidationEngine
import com.example.domain.TelegramChannelWithUser
import com.example.domain.TelegramScopeCandidate
import com.example.domain.TelegramSetupState
import com.example.domain.UserRole
import com.example.ui.components.CyberOutlineButton
import com.example.ui.components.CyberPrimaryButton
import com.example.ui.components.CyberTextField
import com.example.ui.components.EyebrowText
import com.example.ui.components.HudPanelCard
import com.example.ui.components.SectionDivider
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberPink
import com.example.ui.theme.DialogSurface
import com.example.ui.theme.HudBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TelegramExtensionCard(
    role: UserRole,
    extensionEnabled: Boolean,
    myChannel: TelegramChannelEntity?,
    setupState: TelegramSetupState,
    managedChannels: List<TelegramChannelWithUser>,
    scopeCandidates: List<TelegramScopeCandidate>,
    onToggleExtension: (Boolean) -> Unit,
    onSaveChannel: (url: String, targetUserId: Long?, enabled: Boolean, onDone: () -> Unit) -> Unit,
    onConfirmScope: (accountUserId: Long, ownerUserId: Long, sourceReferralId: Long?) -> Unit
) {
    val context = LocalContext.current
    val isSuperOwner = role == UserRole.SUPER_OWNER
    val isOwner = role.isOwnerLevel
    val isAdmin = role == UserRole.ADMIN

    var urlInput by remember { mutableStateOf(myChannel?.channelUrl ?: "") }
    var urlError by remember { mutableStateOf<String?>(null) }
    var setupDialogOpen by remember { mutableStateOf(false) }
    var editingUserId by remember { mutableStateOf<Long?>(null) }
    var editingUrl by remember { mutableStateOf("") }

    LaunchedEffect(myChannel?.channelUrl) {
        urlInput = myChannel?.channelUrl ?: ""
    }

    val canEditOwnChannel = isSuperOwner || role == UserRole.OWNER || (isAdmin && myChannel == null)

    if (setupDialogOpen) {
        AlertDialog(
            onDismissRequest = { setupDialogOpen = false },
            containerColor = DialogSurface,
            title = {
                Text("ADD TELEGRAM LINK", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Add a Public Channel Link or Private Invite Link. Accepted: https://t.me/channel, https://t.me/+invite_hash, or https://t.me/joinchat/invite_hash.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    CyberTextField(
                        value = urlInput,
                        onValueChange = {
                            urlInput = it
                            urlError = null
                        },
                        label = "Telegram link",
                        placeholder = "https://t.me/channel"
                    )
                    urlError?.let {
                        Text(it, color = CyberPink, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                CyberPrimaryButton(
                    text = "SAVE LINK",
                    enabled = urlInput.isNotBlank(),
                    onClick = {
                        val err = LicenseValidationEngine.validateTelegramHttpsUrl(urlInput)
                        if (err != null) {
                            urlError = err
                        } else {
                            onSaveChannel(urlInput, null, true) {
                                setupDialogOpen = false
                            }
                        }
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { setupDialogOpen = false }) {
                    Text("CANCEL", color = TextSecondary)
                }
            }
        )
    }

    HudPanelCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                EyebrowText("OPTIONAL EXTENSION // TELEGRAM")
                Spacer(Modifier.height(4.dp))
                Text("CHANNEL REDIRECT", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Independent from license validation and System Announcement. Only a confirmed announcement OK action opens the mapped channel.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            if (isSuperOwner) {
                Switch(
                    checked = extensionEnabled,
                    onCheckedChange = onToggleExtension,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CyberCyan,
                        checkedTrackColor = CyberCyan.copy(alpha = 0.25f)
                    ),
                    modifier = Modifier.testTag("telegram_extension_switch")
                )
            }
        }

        if (extensionEnabled && setupState.needsSetup) {
            Spacer(Modifier.height(14.dp))
            Surface(
                color = CyberAmber.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, CyberAmber.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(6.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "Telegram setup is required for this account. Save a valid HTTPS Telegram link to dismiss this prompt.",
                        style = MaterialTheme.typography.bodySmall,
                        color = CyberAmber
                    )
                    Spacer(Modifier.height(8.dp))
                    CyberOutlineButton(
                        text = "SETUP TELEGRAM LINK",
                        onClick = { setupDialogOpen = true }
                    )
                }
            }
        }

        if (extensionEnabled && canEditOwnChannel) {
            Spacer(Modifier.height(14.dp))
            CyberTextField(
                value = urlInput,
                onValueChange = {
                    urlInput = it
                    urlError = null
                },
                label = "Your Telegram channel URL",
                placeholder = "https://t.me/channel or https://t.me/+invite_hash",
                trailingIcon = if (myChannel?.channelUrl?.isNotBlank() == true) {
                    {
                        IconButton(onClick = {
                            runCatching {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(myChannel.channelUrl)))
                            }
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = "Open Telegram link",
                                tint = CyberCyan
                            )
                        }
                    }
                } else null,
                modifier = Modifier.testTag("telegram_channel_input")
            )
            urlError?.let {
                Spacer(Modifier.height(6.dp))
                Text(it, color = CyberPink, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(10.dp))
            CyberPrimaryButton(
                text = "SAVE CHANNEL",
                enabled = urlInput.isNotBlank(),
                onClick = {
                    val err = LicenseValidationEngine.validateTelegramHttpsUrl(urlInput)
                    if (err != null) {
                        urlError = err
                    } else {
                        onSaveChannel(urlInput, null, true) {}
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("save_telegram_channel_button")
            )
        } else if (extensionEnabled && isAdmin && myChannel != null) {
            Spacer(Modifier.height(14.dp))
            Text(
                "Saved channel: ${myChannel.channelUrl}. Only the Owner who referred your Admin account, or the Super Owner, can edit it.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        if (extensionEnabled && isOwner) {
            Spacer(Modifier.height(16.dp))
            SectionDivider()
            Spacer(Modifier.height(12.dp))
            Text(
                text = if (isSuperOwner) "MAPPED ACCOUNTS" else "ADMINS YOU REFERRED",
                style = MaterialTheme.typography.labelMedium,
                color = CyberCyan
            )
            Spacer(Modifier.height(8.dp))
            if (managedChannels.isEmpty()) {
                Text(
                    text = if (isSuperOwner) "No Telegram channel mappings yet." else "No referred Admin Telegram links yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            } else {
                managedChannels.take(12).forEach { item ->
                    Surface(
                        color = Color(0x0A54E7FF),
                        border = BorderStroke(1.dp, HudBorder),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "${item.username ?: "Account ${item.userId}"} (${item.role.label})",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary
                                )
                                Text(
                                    if (item.enabled) "enabled" else "disabled",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                            if (editingUserId == item.userId) {
                                Spacer(Modifier.height(8.dp))
                                CyberTextField(
                                    value = editingUrl,
                                    onValueChange = { editingUrl = it },
                                    label = "New Telegram URL",
                                    placeholder = "https://t.me/channel"
                                )
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    CyberPrimaryButton(
                                        text = "SAVE",
                                        onClick = {
                                            onSaveChannel(editingUrl, item.userId, item.enabled) {
                                                editingUserId = null
                                                editingUrl = ""
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                    CyberOutlineButton(
                                        text = "CANCEL",
                                        onClick = {
                                            editingUserId = null
                                            editingUrl = ""
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            } else {
                                Spacer(Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        item.channelUrl,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    TextButton(onClick = {
                                        editingUserId = item.userId
                                        editingUrl = item.channelUrl
                                    }) {
                                        Text("EDIT LINK", color = CyberCyan)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (extensionEnabled && isSuperOwner) {
            Spacer(Modifier.height(16.dp))
            SectionDivider()
            Spacer(Modifier.height(12.dp))
            Text("SCOPE REVIEW", style = MaterialTheme.typography.labelMedium, color = CyberCyan)
            Spacer(Modifier.height(4.dp))
            Text(
                "Only confirmed referral provenance can create an Owner -> Admin Telegram scope.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
            Spacer(Modifier.height(8.dp))
            if (scopeCandidates.isEmpty()) {
                Text("No referral-based Admin scopes need review.", style = MaterialTheme.typography.bodySmall, color = TextMuted)
            } else {
                scopeCandidates.take(10).forEach { candidate ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                candidate.accountUsername ?: "Account ${candidate.accountUserId ?: "pending"}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary
                            )
                            Text(
                                "${candidate.status.wire}${candidate.accountUserId?.let { " · creator ${candidate.referralCreatorId}" } ?: " · not redeemed"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                        if (candidate.accountUserId != null && candidate.confirmedScopeStatus == null) {
                            CyberOutlineButton(
                                text = "CONFIRM",
                                onClick = {
                                    onConfirmScope(candidate.accountUserId, candidate.referralCreatorId, candidate.referralId)
                                }
                            )
                        } else if (candidate.confirmedScopeStatus != null) {
                            Text("CONFIRMED", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        }
                    }
                }
            }
        }
    }
}
