package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.DeviceBindingEntity
import com.example.domain.LicenseStatus
import com.example.domain.LicenseUiModel
import com.example.domain.LicenseValidationEngine
import com.example.ui.components.CyberDangerButton
import com.example.ui.components.CyberOutlineButton
import com.example.ui.components.CyberPrimaryButton
import com.example.ui.components.CyberTextField
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberPink
import com.example.ui.theme.DialogSurface
import com.example.ui.theme.HudBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun BoundDevicesDialog(
    bindings: List<DeviceBindingEntity>,
    onUnbind: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DialogSurface,
        title = {
            Text("BOUND DEVICES", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (bindings.isEmpty()) {
                    Text(
                        "No devices bound to this key.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    bindings.forEachIndexed { index, binding ->
                        Surface(
                            color = Color(0x0A00EEFF),
                            border = BorderStroke(1.dp, HudBorder),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "DEVICE // ${(index + 1).toString().padStart(2, '0')}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = CyberCyan
                                    )
                                    Text(
                                        "${binding.deviceHash.take(20)}…",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMuted
                                    )
                                }
                                IconButton(onClick = { onUnbind(binding.id) }) {
                                    Icon(
                                        imageVector = Icons.Default.LinkOff,
                                        contentDescription = stringResource(R.string.cd_unbind_device),
                                        tint = CyberPink
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("CLOSE", color = CyberCyan)
            }
        }
    )
}

@Composable
fun ManageLicenseDialog(
    license: LicenseUiModel,
    onSave: (status: LicenseStatus, requiresExpiry: Boolean, expiryRaw: String, maxDevicesRaw: String) -> Unit,
    onDismiss: () -> Unit
) {
    var status by remember(license.id) { mutableStateOf(license.storedStatus) }
    var statusExpanded by remember { mutableStateOf(false) }
    var maxDevices by remember(license.id) { mutableStateOf(license.maxDevices.toString()) }
    var expiry by remember(license.id) {
        mutableStateOf(LicenseValidationEngine.formatInputDateTime(license.expiresAtMillis))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DialogSurface,
        title = {
            Text("MANAGE LICENSE", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column {
                    Text("STATUS", style = MaterialTheme.typography.labelMedium, color = CyberCyan)
                    Spacer(Modifier.height(6.dp))
                    Surface(
                        color = Color(0xFF090B12),
                        border = BorderStroke(1.dp, HudBorder),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { statusExpanded = true }
                    ) {
                        Text(
                            text = status.wire.uppercase(),
                            color = TextPrimary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        LicenseStatus.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.wire.uppercase()) },
                                onClick = {
                                    status = option
                                    statusExpanded = false
                                }
                            )
                        }
                    }
                }

                if (license.durationMinutes != null) {
                    Surface(
                        color = CyberCyan.copy(alpha = 0.06f),
                        border = BorderStroke(1.dp, HudBorder),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("ACTIVATION-BASED EXPIRY", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "${LicenseValidationEngine.formatDuration(license.durationMinutes)} from first device activation",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                if (license.activatedAtMillis != null) {
                                    "Activated ${LicenseValidationEngine.formatTimestamp(license.activatedAtMillis)} · expires ${LicenseValidationEngine.formatTimestamp(license.expiresAtMillis)}"
                                } else {
                                    "Pending activation — timer starts on first successful device validation."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }
                } else {
                    CyberTextField(
                        value = expiry,
                        onValueChange = { expiry = it },
                        label = "Expiry date/time (UTC)",
                        placeholder = "yyyy-MM-dd HH:mm"
                    )
                }

                CyberTextField(
                    value = maxDevices,
                    onValueChange = { maxDevices = it },
                    label = "Maximum devices",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            CyberPrimaryButton(
                text = "SAVE LICENSE",
                onClick = {
                    onSave(status, license.durationMinutes == null, expiry, maxDevices)
                }
            )
        },
        dismissButton = {
            CyberOutlineButton(text = "CANCEL", onClick = onDismiss)
        }
    )
}

@Composable
fun ConfirmDangerDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DialogSurface,
        title = {
            Text(title, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        },
        text = {
            Text(message, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        },
        confirmButton = {
            CyberDangerButton(text = confirmText, onClick = onConfirm)
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = TextSecondary)
            }
        }
    )
}

@Composable
fun UsernameDialog(
    isInitialSetup: Boolean,
    currentEmailOrId: String,
    initialValue: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var username by remember(initialValue) { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = { if (!isInitialSetup) onDismiss() },
        containerColor = DialogSurface,
        title = {
            Text(
                if (isInitialSetup) "SET YOUR USERNAME" else "EDIT USERNAME",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    if (isInitialSetup) {
                        "This name is shown in the panel, User Roles, and audit history so the owner can recognize the account."
                    } else {
                        "Your sign-in remains unchanged. A successful username change starts a 72-hour cooldown."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                CyberTextField(
                    value = currentEmailOrId,
                    onValueChange = {},
                    label = "Signed-in ID",
                    readOnly = true
                )
                CyberTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = if (isInitialSetup) "Username" else "New username",
                    placeholder = "e.g. Pooja"
                )
                Text(
                    "3–32 characters. Letters, numbers, spaces, dots, hyphens, and underscores are allowed.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        },
        confirmButton = {
            CyberPrimaryButton(
                text = "SAVE USERNAME",
                enabled = username.trim().length >= 3,
                onClick = { onSave(username) }
            )
        },
        dismissButton = if (!isInitialSetup) {
            {
                TextButton(onClick = onDismiss) {
                    Text("CANCEL", color = TextSecondary)
                }
            }
        } else null
    )
}
