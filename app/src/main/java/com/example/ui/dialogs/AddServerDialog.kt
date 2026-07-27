package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CardBackground
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AddServerDialog(
    onDismiss: () -> Unit,
    onImportLink: (String) -> Unit,
    onImportSub: (String) -> Unit,
    onImportJson: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var inputContent by remember { mutableStateOf("") }

    val tabs = listOf("Config Link", "Subscription", "Manual JSON")

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(CardBackground)
                .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Text(
                text = "ADD NEW SERVER",
                color = NeonGreen,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tabs Header
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF141428),
                contentColor = NeonCyan,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = NeonGreen
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            inputContent = ""
                        },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) NeonGreen else TextSecondary
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Input field instructions
            Text(
                text = when (selectedTab) {
                    0 -> "Paste vmess://, vless://, trojan://, ss://, hy2://, tuic:// or wg:// link:"
                    1 -> "Paste Subscription URL to sync multiple servers:"
                    else -> "Paste custom Xray-core / sing-box JSON configuration:"
                },
                color = TextMuted,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = inputContent,
                onValueChange = { inputContent = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (selectedTab == 2) 130.dp else 90.dp)
                    .testTag("add_server_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonGreen,
                    unfocusedBorderColor = GlassBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = NeonGreen
                ),
                placeholder = {
                    Text(
                        text = when (selectedTab) {
                            0 -> "vmess://eyJhZGQiOiI..."
                            1 -> "https://sub.cybertech.io/api/v1/..."
                            else -> "{\n  \"outbounds\": [...]\n}"
                        },
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("CANCEL", color = TextSecondary)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = {
                        if (inputContent.isNotBlank()) {
                            when (selectedTab) {
                                0 -> onImportLink(inputContent)
                                1 -> onImportSub(inputContent)
                                2 -> onImportJson(inputContent)
                            }
                        }
                    },
                    enabled = inputContent.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGreen,
                        contentColor = DeepNavy
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("confirm_import_button")
                ) {
                    Text("IMPORT", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
