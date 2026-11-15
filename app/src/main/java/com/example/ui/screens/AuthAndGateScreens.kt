package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.UserEntity
import com.example.ui.components.BrandMark
import com.example.ui.components.CyberOutlineButton
import com.example.ui.components.CyberPanelBackground
import com.example.ui.components.CyberPrimaryButton
import com.example.ui.components.CyberTextField
import com.example.ui.components.EyebrowText
import com.example.ui.components.HudPanelCard
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberPink
import com.example.ui.theme.HudBorder
import com.example.ui.theme.PanelSurfaceHigh
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AuthScreen(
    isBusy: Boolean,
    onLogin: (String, String) -> Unit,
    onRegister: (String, String, () -> Unit) -> Unit
) {
    var isRegistering by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    CyberPanelBackground(modifier = Modifier.fillMaxSize()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            HudPanelCard(
                modifier = Modifier.widthIn(max = 460.dp)
            ) {
                BrandMark()
                Spacer(Modifier.height(16.dp))
                EyebrowText(if (isRegistering) "ACCOUNT PROVISIONING" else "CONTROL GRID // AUTH GATE")
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(
                        if (isRegistering) R.string.auth_create_account else R.string.auth_bus_title
                    ),
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
                Spacer(Modifier.height(20.dp))

                CyberTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = "Username",
                    placeholder = "Enter username",
                    modifier = Modifier.testTag("username_input")
                )
                Spacer(Modifier.height(14.dp))
                CyberTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    placeholder = if (isRegistering) "Min. 8 characters" else "Enter password",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = CyberCyan
                            )
                        }
                    },
                    modifier = Modifier.testTag("password_input")
                )
                Spacer(Modifier.height(20.dp))

                CyberPrimaryButton(
                    text = when {
                        isBusy -> "PROCESSING…"
                        isRegistering -> "REGISTER"
                        else -> "LOGIN"
                    },
                    enabled = !isBusy && username.isNotBlank() && password.isNotBlank(),
                    onClick = {
                        if (isRegistering) {
                            onRegister(username, password) { isRegistering = false }
                        } else {
                            onLogin(username, password)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_button")
                )

                Spacer(Modifier.height(8.dp))
                TextButton(
                    onClick = { isRegistering = !isRegistering },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("toggle_register_button")
                ) {
                    Text(
                        text = if (isRegistering) "BACK TO LOGIN" else "NEED AN ACCOUNT? REGISTER",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        letterSpacing = 1.4.sp
                    )
                }

                if (!isRegistering) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "QUICK FILL SEEDED ACCOUNTS",
                        color = TextMuted,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Spacer(Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "NEXA (Super Owner)" to ("NEXA" to "NEXA@2026"),
                            "ATLAS (Owner)" to ("ATLAS" to "ATLAS@2026"),
                            "NOVA (Admin)" to ("NOVA" to "NOVA@2026"),
                            "ROOKIE (Pending)" to ("ROOKIE" to "ROOKIE@2026")
                        ).forEach { (label, creds) ->
                            AssistChip(
                                onClick = {
                                    username = creds.first
                                    password = creds.second
                                },
                                label = {
                                    Text(label, fontSize = 11.sp, color = CyberCyan)
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = PanelSurfaceHigh
                                ),
                                border = AssistChipDefaults.assistChipBorder(
                                    enabled = true,
                                    borderColor = HudBorder
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SessionLoadingScreen() {
    CyberPanelBackground(modifier = Modifier.fillMaxSize()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            HudPanelCard(modifier = Modifier.widthIn(max = 460.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BrandMark()
                    EyebrowText("AUTHENTICATION BUS // ONLINE")
                }
                Spacer(Modifier.height(20.dp))
                EyebrowText("CONTROL GRID INITIALIZATION")
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "VERIFYING SESSION",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Securely loading your account permissions, license registry, and management controls.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(Modifier.height(20.dp))
                LinearProgressIndicator(
                    color = CyberCyan,
                    trackColor = CyberCyan.copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun PendingReferralScreen(
    user: UserEntity,
    onRedeem: (String) -> Unit,
    onSwitchAccount: () -> Unit
) {
    var referralCode by remember { mutableStateOf("") }
    CyberPanelBackground(modifier = Modifier.fillMaxSize()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            HudPanelCard(modifier = Modifier.widthIn(max = 520.dp)) {
                EyebrowText("REFERRAL GATE // PENDING")
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "ACTIVATE ACCESS",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Signed in as ${user.username ?: user.email ?: "your account"}. Ask the owner for a referral, then redeem it here to activate your admin or owner panel access.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(Modifier.height(16.dp))
                CyberTextField(
                    value = referralCode,
                    onValueChange = { referralCode = it },
                    label = "Referral code",
                    placeholder = "REF-...",
                    modifier = Modifier.testTag("referral_code_input")
                )
                Spacer(Modifier.height(14.dp))
                CyberPrimaryButton(
                    text = "REDEEM REFERRAL",
                    enabled = referralCode.trim().length >= 4,
                    onClick = { onRedeem(referralCode.trim()) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("redeem_referral_button")
                )
                Spacer(Modifier.height(12.dp))
                CyberOutlineButton(
                    text = "SIGN IN WITH ANOTHER ACCOUNT",
                    onClick = onSwitchAccount,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("switch_account_button")
                )
            }
        }
    }
}

@Composable
fun SuspendedAccessScreen(onSwitchAccount: () -> Unit) {
    CyberPanelBackground(modifier = Modifier.fillMaxSize()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            HudPanelCard(modifier = Modifier.widthIn(max = 500.dp)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Access Suspended",
                        tint = CyberPink,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(Modifier.height(14.dp))
                    EyebrowText("ACCESS POLICY // BLOCKED", color = CyberPink)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "MANAGEMENT ACCESS SUSPENDED",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Your account is not active, expired, or has been revoked. License and device controls remain disabled until an authorized Owner restores access.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(20.dp))
                    CyberOutlineButton(
                        text = "SIGN IN WITH ANOTHER ACCOUNT",
                        onClick = onSwitchAccount,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
