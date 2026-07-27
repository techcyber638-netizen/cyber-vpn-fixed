package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VpnState
import com.example.ui.theme.CardBackground
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ConnectButton(
    vpnState: VpnState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val isConnected = vpnState == VpnState.CONNECTED
    val isConnecting = vpnState == VpnState.CONNECTING || vpnState == VpnState.DISCONNECTING

    val primaryGradient = Brush.linearGradient(
        colors = when (vpnState) {
            VpnState.CONNECTED -> listOf(NeonCyan, NeonGreen)
            VpnState.CONNECTING, VpnState.DISCONNECTING -> listOf(Color(0xFFFFB800), NeonCyan)
            VpnState.ERROR -> listOf(NeonPink, Color(0xFFFF3366))
            VpnState.DISCONNECTED -> listOf(Color(0xFF2A2A4A), Color(0xFF1E1E38))
        }
    )

    val auraColor = when (vpnState) {
        VpnState.CONNECTED -> NeonGreen.copy(alpha = 0.35f)
        VpnState.CONNECTING, VpnState.DISCONNECTING -> NeonCyan.copy(alpha = 0.35f)
        VpnState.ERROR -> NeonPink.copy(alpha = 0.35f)
        VpnState.DISCONNECTED -> Color.Transparent
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(230.dp)
        ) {
            // Pulsing Outer Aura Ring
            if (isConnected || isConnecting) {
                Box(
                    modifier = Modifier
                        .size(210.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(auraColor)
                )
            }

            // Outer Neon Border Ring
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .clip(CircleShape)
                    .background(primaryGradient)
                    .padding(6.dp)
                    .clip(CircleShape)
                    .background(DeepNavy),
                contentAlignment = Alignment.Center
            ) {
                // Inner Interactive Button Surface
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(primaryGradient)
                        .testTag("connect_button")
                        .clickable { onClick() }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (isConnected) Icons.Default.Shield else Icons.Default.PowerSettingsNew,
                            contentDescription = "VPN Power Action",
                            tint = if (vpnState == VpnState.DISCONNECTED) TextSecondary else DeepNavy,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (vpnState) {
                                VpnState.CONNECTED -> "SECURED"
                                VpnState.CONNECTING -> "LINKING..."
                                VpnState.DISCONNECTING -> "STOPPING"
                                VpnState.ERROR -> "RETRY"
                                VpnState.DISCONNECTED -> "CONNECT"
                            },
                            color = if (vpnState == VpnState.DISCONNECTED) TextPrimary else DeepNavy,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = when (vpnState) {
                VpnState.CONNECTED -> "Cyber Tunnel Active • Protected"
                VpnState.CONNECTING -> "Establishing Secure Xray Core Pipeline..."
                VpnState.DISCONNECTING -> "Closing VPN Tunnel..."
                VpnState.ERROR -> "Connection Interrupted - Tap to retry"
                VpnState.DISCONNECTED -> "Tap to Connect Cyber VPN"
            },
            color = when (vpnState) {
                VpnState.CONNECTED -> NeonGreen
                VpnState.CONNECTING, VpnState.DISCONNECTING -> NeonCyan
                VpnState.ERROR -> NeonPink
                VpnState.DISCONNECTED -> TextSecondary
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
