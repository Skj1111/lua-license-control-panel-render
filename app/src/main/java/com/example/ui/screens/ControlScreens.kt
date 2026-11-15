package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.local.UserEntity
import com.example.domain.AnnouncementDisplayMode
import com.example.domain.AnnouncementTextSize
import com.example.domain.LicenseValidationEngine
import com.example.domain.UserRole
import com.example.ui.components.CyberOutlineButton
import com.example.ui.components.CyberPrimaryButton
import com.example.ui.components.CyberTextField
import com.example.ui.components.EyebrowText
import com.example.ui.components.HudPanelCard
import com.example.ui.components.StatusPill
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.HudBorder
import com.example.ui.theme.InputSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.LicenseXViewModel

@Composable
fun SystemControlScreen(
    user: UserEntity,
    viewModel: LicenseXViewModel,
    onNavigateBack: () -> Unit
) {
    if (UserRole.fromWire(user.role) != UserRole.SUPER_OWNER) {
        RestrictedSuperOwnerCard(onNavigateBack)
        return
    }

    val maintenance by viewModel.maintenance.collectAsStateWithLifecycle()
    var enabled by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("SERVER MAINTENANCE") }
    var message by remember { mutableStateOf("") }
    var startAt by remember { mutableStateOf("") }
    var endAt by remember { mutableStateOf("") }

    LaunchedEffect(maintenance) {
        maintenance?.let { current ->
            enabled = current.enabled
            title = current.title
            message = current.message
            startAt = LicenseValidationEngine.formatInputDateTime(current.startAtMillis)
            endAt = LicenseValidationEngine.formatInputDateTime(current.endAtMillis)
        }
    }

    val isActiveWindow = LicenseValidationEngine.isWindowActive(
        enabled = maintenance?.enabled == true,
        message = maintenance?.message ?: "",
        startAtMillis = maintenance?.startAtMillis,
        endAtMillis = maintenance?.endAtMillis
    )

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Box(modifier = Modifier.widthIn(max = 960.dp).fillMaxWidth()) {
                HudPanelCard {
                    EyebrowText("SUPER OWNER // OPERATIONAL HOLD", color = CyberAmber)
                    Spacer(Modifier.height(4.dp))
                    Text(stringResource(R.string.system_control_title), style = MaterialTheme.typography.displayLarge, color = TextPrimary)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Configure the separate Server Maintenance state. Preserves valid license status while holding Main Lua during an active window.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(14.dp))
                    CyberOutlineButton(
                        text = "RETURN TO DASHBOARD",
                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        onClick = onNavigateBack
                    )
                }
            }
        }

        item {
            Box(modifier = Modifier.widthIn(max = 960.dp).fillMaxWidth()) {
                HudPanelCard(borderColor = CyberAmber.copy(alpha = 0.35f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            EyebrowText("OPERATIONAL HOLD", color = CyberAmber)
                            Spacer(Modifier.height(4.dp))
                            Text("SERVER MAINTENANCE", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                            Spacer(Modifier.height(4.dp))
                            StatusPill(
                                if (isActiveWindow) "HOLD ACTIVE" else "STANDBY",
                                if (isActiveWindow) CyberAmber else CyberEmerald
                            )
                        }
                        Switch(
                            checked = enabled,
                            onCheckedChange = { enabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CyberAmber,
                                checkedTrackColor = CyberAmber.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier.testTag("maintenance_toggle_switch")
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                    CyberTextField(
                        value = title,
                        onValueChange = { if (it.length <= 80) title = it },
                        label = "Popup title (${title.length}/80)",
                        placeholder = "SERVER MAINTENANCE"
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        CyberTextField(
                            value = startAt,
                            onValueChange = { startAt = it },
                            label = "Start UTC (optional)",
                            placeholder = "yyyy-MM-dd HH:mm",
                            modifier = Modifier.weight(1f)
                        )
                        CyberTextField(
                            value = endAt,
                            onValueChange = { endAt = it },
                            label = "End UTC (optional)",
                            placeholder = "yyyy-MM-dd HH:mm",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    CyberTextField(
                        value = message,
                        onValueChange = { if (it.length <= 300) message = it },
                        label = "Popup message (${message.length}/300)",
                        placeholder = "Server Under Maintenance. Please wait...",
                        singleLine = false,
                        minLines = 4
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Unicode and emoji are supported. Blank schedule fields create an immediate open-ended hold.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    Spacer(Modifier.height(14.dp))
                    CyberPrimaryButton(
                        text = "SAVE MAINTENANCE",
                        enabled = !enabled || message.isNotBlank(),
                        onClick = { viewModel.saveMaintenance(enabled, title, message, startAt, endAt) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_maintenance_button")
                    )
                }
            }
        }
    }
}

@Composable
fun AnnouncementControlScreen(
    user: UserEntity,
    viewModel: LicenseXViewModel,
    onNavigateBack: () -> Unit
) {
    val role = UserRole.fromWire(user.role)
    if (role != UserRole.SUPER_OWNER) {
        RestrictedSuperOwnerCard(onNavigateBack)
        return
    }

    val announcement by viewModel.announcement.collectAsStateWithLifecycle()
    val telegramSettings by viewModel.telegramSettings.collectAsStateWithLifecycle()
    val myTelegramChannel by viewModel.myTelegramChannel.collectAsStateWithLifecycle()
    val telegramSetupState by viewModel.telegramSetupState.collectAsStateWithLifecycle()
    val managedChannels by viewModel.managedTelegramChannels.collectAsStateWithLifecycle()
    val scopeCandidates by viewModel.telegramScopeCandidates.collectAsStateWithLifecycle()

    var enabled by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("SYSTEM ANNOUNCEMENT") }
    var message by remember { mutableStateOf("") }
    var startAt by remember { mutableStateOf("") }
    var endAt by remember { mutableStateOf("") }
    var displayMode by remember { mutableStateOf(AnnouncementDisplayMode.EVERY_LOGIN) }
    var textSize by remember { mutableStateOf(AnnouncementTextSize.NORMAL) }
    var modeExpanded by remember { mutableStateOf(false) }
    var sizeExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(announcement) {
        announcement?.let { current ->
            enabled = current.enabled
            title = current.title
            message = current.message
            startAt = LicenseValidationEngine.formatInputDateTime(current.startAtMillis)
            endAt = LicenseValidationEngine.formatInputDateTime(current.endAtMillis)
            displayMode = AnnouncementDisplayMode.fromWire(current.displayMode)
            textSize = AnnouncementTextSize.fromWire(current.textSize)
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Box(modifier = Modifier.widthIn(max = 960.dp).fillMaxWidth()) {
                HudPanelCard {
                    EyebrowText("SUPER OWNER // PRESENTATION CONTROL")
                    Spacer(Modifier.height(4.dp))
                    Text(stringResource(R.string.announcement_control_title), style = MaterialTheme.typography.displayLarge, color = TextPrimary)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Manage the System Announcement and isolated Telegram extension without altering license validation behavior.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(14.dp))
                    CyberOutlineButton(
                        text = "RETURN TO DASHBOARD",
                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        onClick = onNavigateBack
                    )
                }
            }
        }

        item {
            Box(modifier = Modifier.widthIn(max = 960.dp).fillMaxWidth()) {
                HudPanelCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            EyebrowText("SUPER OWNER CONTROL")
                            Spacer(Modifier.height(4.dp))
                            Text("SYSTEM ANNOUNCEMENT", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                        }
                        Switch(
                            checked = enabled,
                            onCheckedChange = { enabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CyberCyan,
                                checkedTrackColor = CyberCyan.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier.testTag("announcement_toggle_switch")
                        )
                    }
                    Spacer(Modifier.height(14.dp))
                    CyberTextField(
                        value = title,
                        onValueChange = { if (it.length <= 80) title = it },
                        label = "Title (${title.length}/80)"
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("TEXT SIZE", style = MaterialTheme.typography.labelMedium, color = CyberCyan)
                            Spacer(Modifier.height(6.dp))
                            Box {
                                Surface(
                                    color = InputSurface,
                                    border = BorderStroke(1.dp, HudBorder),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp)
                                        .clickable { sizeExpanded = true }
                                ) {
                                    Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.padding(horizontal = 12.dp)) {
                                        Text(textSize.label, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                                    }
                                }
                                DropdownMenu(expanded = sizeExpanded, onDismissRequest = { sizeExpanded = false }) {
                                    AnnouncementTextSize.entries.forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(option.label) },
                                            onClick = {
                                                textSize = option
                                                sizeExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("DISPLAY MODE", style = MaterialTheme.typography.labelMedium, color = CyberCyan)
                            Spacer(Modifier.height(6.dp))
                            Box {
                                Surface(
                                    color = InputSurface,
                                    border = BorderStroke(1.dp, HudBorder),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp)
                                        .clickable { modeExpanded = true }
                                ) {
                                    Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.padding(horizontal = 12.dp)) {
                                        Text(displayMode.label, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                                    }
                                }
                                DropdownMenu(expanded = modeExpanded, onDismissRequest = { modeExpanded = false }) {
                                    AnnouncementDisplayMode.entries.forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(option.label) },
                                            onClick = {
                                                displayMode = option
                                                modeExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    CyberTextField(
                        value = message,
                        onValueChange = { if (it.length <= 300) message = it },
                        label = "Message (${message.length}/300)",
                        placeholder = "Happy Independence Day to all users!",
                        singleLine = false,
                        minLines = 3
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        CyberTextField(
                            value = startAt,
                            onValueChange = { startAt = it },
                            label = "Start UTC",
                            placeholder = "yyyy-MM-dd HH:mm",
                            modifier = Modifier.weight(1f)
                        )
                        CyberTextField(
                            value = endAt,
                            onValueChange = { endAt = it },
                            label = "End UTC",
                            placeholder = "yyyy-MM-dd HH:mm",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(14.dp))
                    CyberPrimaryButton(
                        text = "SAVE ANNOUNCEMENT",
                        enabled = message.isNotBlank(),
                        onClick = {
                            viewModel.saveAnnouncement(
                                enabled = enabled,
                                title = title,
                                message = message,
                                startRaw = startAt,
                                endRaw = endAt,
                                displayMode = displayMode,
                                textSize = textSize
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_announcement_button")
                    )
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
                    onSaveChannel = { url, targetId, chEnabled, onDone ->
                        viewModel.saveTelegramChannel(url, targetId, chEnabled, onDone)
                    },
                    onConfirmScope = { accId, ownerId, refId ->
                        viewModel.confirmTelegramScope(accId, ownerId, refId)
                    }
                )
            }
        }
    }
}
