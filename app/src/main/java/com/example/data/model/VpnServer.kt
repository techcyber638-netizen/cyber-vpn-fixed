package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vpn_servers")
data class VpnServer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val countryCode: String, // e.g. "US", "DE", "JP", "SG", "UK", "CA"
    val flagEmoji: String,   // e.g. "🇺🇸", "🇩🇪", "🇯🇵"
    val protocol: String,    // VMess, VLESS, Trojan, Shadowsocks, Hysteria2, TUIC, WireGuard, SSH
    val serverAddress: String,
    val port: Int,
    val pingMs: Int = -1,    // -1: untested, -2: timeout/error, >0: milliseconds
    val configUrl: String = "",
    val jsonConfig: String? = null,
    val isCustom: Boolean = true,
    val subscriptionUrl: String? = null,
    val isFavorite: Boolean = false,
    val ipAddress: String = "104.28.16.1"
)

enum class VpnState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING,
    ERROR
}

data class VpnStats(
    val downSpeedBps: Long = 0L,
    val upSpeedBps: Long = 0L,
    val totalDownBytes: Long = 0L,
    val totalUpBytes: Long = 0L,
    val sessionDurationSeconds: Long = 0L
)

data class AppSettings(
    val engine: String = "Xray-core", // "Xray-core" or "sing-box"
    val xrayVersion: String = "1.8.8",
    val singBoxVersion: String = "1.8.0",
    val pingUrl: String = "http://cp.cloudflare.com/generate_204",
    val perAppProxyEnabled: Boolean = false,
    val notificationTitle: String = "Cyber Tech VPN Secured",
    val showNotification: Boolean = true,
    val isDarkTheme: Boolean = true,
    val selectedServerId: Long = 1L
)
