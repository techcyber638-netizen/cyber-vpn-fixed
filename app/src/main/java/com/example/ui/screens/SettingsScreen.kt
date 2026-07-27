package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.theme.CardBackground
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.VpnViewModel

@Composable
fun SettingsScreen(
    viewModel: VpnViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()

    var showPingUrlDialog by remember { mutableStateOf(false) }
    var pingUrlInput by remember { mutableStateOf(settings.pingUrl) }

    var showNotifTitleDialog by remember { mutableStateOf(false) }
    var notifTitleInput by remember { mutableStateOf(settings.notificationTitle) }

    var showAboutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepNavy)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("settings_back")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SETTINGS",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION 1: CORE ENGINE SWITCHER
        SectionHeader("CORE ENGINE")

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                EngineOptionRow(
                    name = "Xray-core",
                    version = "v${settings.xrayVersion}",
                    description = "Default engine. Supports VMess, VLESS, Trojan, Shadowsocks & HY2.",
                    isSelected = settings.engine == "Xray-core",
                    onSelect = { viewModel.setEngine("Xray-core") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                EngineOptionRow(
                    name = "sing-box",
                    version = "v${settings.singBoxVersion}",
                    description = "Next-gen universal engine for TUIC, WireGuard & SSH tunnels.",
                    isSelected = settings.engine == "sing-box",
                    onSelect = { viewModel.setEngine("sing-box") }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SECTION 2: PROXY & NETWORK
        SectionHeader("PROXY & NETWORK")

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Per-app Proxy Switch
                SettingToggleRow(
                    title = "Per-App Proxy",
                    subtitle = "Filter which Android apps bypass or use VPN tunnel",
                    icon = Icons.Default.Apps,
                    checked = settings.perAppProxyEnabled,
                    onCheckedChange = { viewModel.togglePerAppProxy(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Custom Ping Test URL
                SettingActionRow(
                    title = "Ping Test URL",
                    subtitle = settings.pingUrl,
                    icon = Icons.Default.Public,
                    onClick = {
                        pingUrlInput = settings.pingUrl
                        showPingUrlDialog = true
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SECTION 3: NOTIFICATIONS & SYSTEM
        SectionHeader("NOTIFICATIONS & DISPLAY")

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Show Notification
                SettingToggleRow(
                    title = "Connection Notification",
                    subtitle = "Show sticky status notification in system bar",
                    icon = Icons.Default.Notifications,
                    checked = settings.showNotification,
                    onCheckedChange = { viewModel.toggleNotification(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Notification Title Customization
                SettingActionRow(
                    title = "Notification Title",
                    subtitle = settings.notificationTitle,
                    icon = Icons.Default.Code,
                    onClick = {
                        notifTitleInput = settings.notificationTitle
                        showNotifTitleDialog = true
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Dark / Light Theme Toggle
                SettingToggleRow(
                    title = "Cyber Dark Theme",
                    subtitle = "Neon cyan & green dark aesthetic",
                    icon = Icons.Default.Palette,
                    checked = settings.isDarkTheme,
                    onCheckedChange = { /* Default dark theme enforced */ }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SECTION 4: ABOUT
        SectionHeader("ABOUT & CREDITS")

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = { showAboutDialog = true }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "About",
                    tint = NeonGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "Cyber Tech VPN v2.4.0",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Powered by Xray-core & sing-box",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

    // Ping URL Dialog
    if (showPingUrlDialog) {
        AlertDialog(
            onDismissRequest = { showPingUrlDialog = false },
            containerColor = CardBackground,
            title = { Text("Custom Ping URL", color = NeonGreen) },
            text = {
                OutlinedTextField(
                    value = pingUrlInput,
                    onValueChange = { pingUrlInput = it },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonGreen,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.setPingUrl(pingUrlInput)
                        showPingUrlDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = DeepNavy)
                ) {
                    Text("SAVE")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPingUrlDialog = false }) {
                    Text("CANCEL", color = TextSecondary)
                }
            }
        )
    }

    // Notification Title Dialog
    if (showNotifTitleDialog) {
        AlertDialog(
            onDismissRequest = { showNotifTitleDialog = false },
            containerColor = CardBackground,
            title = { Text("Notification Title", color = NeonCyan) },
            text = {
                OutlinedTextField(
                    value = notifTitleInput,
                    onValueChange = { notifTitleInput = it },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.setNotificationTitle(notifTitleInput)
                        showNotifTitleDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = DeepNavy)
                ) {
                    Text("SAVE")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotifTitleDialog = false }) {
                    Text("CANCEL", color = TextSecondary)
                }
            }
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            containerColor = CardBackground,
            title = { Text("Cyber Tech VPN", color = NeonGreen, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Cyber Tech VPN is a modern high-performance VPN client built with Kotlin Jetpack Compose.\n\n" +
                            "• Core Engine 1: Xray-core v1.8.8\n" +
                            "• Core Engine 2: sing-box v1.8.0\n" +
                            "• Protocols: VMess, VLESS, Trojan, Shadowsocks, Hysteria2, TUIC, WireGuard, SSH\n" +
                            "• License: MIT Open Source",
                    color = TextPrimary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showAboutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = DeepNavy)
                ) {
                    Text("CLOSE")
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = NeonGreen,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    )
}

@Composable
private fun EngineOptionRow(
    name: String,
    version: String,
    description: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0x3300FF88) else Color(0xFF141428))
            .clickable { onSelect() }
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = name,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF003344))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = version,
                            color = NeonCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected Engine",
                    tint = NeonGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(text = subtitle, color = TextMuted, fontSize = 11.sp)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = DeepNavy,
                checkedTrackColor = NeonGreen,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = Color(0xFF252542)
            )
        )
    }
}

@Composable
private fun SettingActionRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, color = TextMuted, fontSize = 11.sp)
        }
    }
}
