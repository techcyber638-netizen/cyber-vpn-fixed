package com.example.utils

import android.util.Base64
import com.example.data.model.VpnServer
import org.json.JSONObject
import java.net.URI
import java.net.URLDecoder

object ConfigParser {

    fun parseConfigLink(link: String): VpnServer? {
        val trimmed = link.trim()
        return when {
            trimmed.startsWith("vmess://", ignoreCase = true) -> parseVMess(trimmed)
            trimmed.startsWith("vless://", ignoreCase = true) -> parseVLess(trimmed)
            trimmed.startsWith("trojan://", ignoreCase = true) -> parseTrojan(trimmed)
            trimmed.startsWith("ss://", ignoreCase = true) -> parseShadowsocks(trimmed)
            trimmed.startsWith("hysteria2://", ignoreCase = true) || trimmed.startsWith("hy2://", ignoreCase = true) -> parseHysteria2(trimmed)
            trimmed.startsWith("tuic://", ignoreCase = true) -> parseTuic(trimmed)
            trimmed.startsWith("wireguard://", ignoreCase = true) || trimmed.startsWith("wg://", ignoreCase = true) -> parseWireGuard(trimmed)
            trimmed.startsWith("ssh://", ignoreCase = true) -> parseSSH(trimmed)
            trimmed.startsWith("{") -> parseJsonConfig(trimmed)
            else -> null
        }
    }

    private fun parseVMess(link: String): VpnServer? {
        return try {
            val base64Part = link.substring(8)
            val jsonString = String(Base64.decode(base64Part, Base64.DEFAULT), Charsets.UTF_8)
            val json = JSONObject(jsonString)
            val ps = json.optString("ps", "VMess Node")
            val add = json.optString("add", "127.0.0.1")
            val port = json.optInt("port", 443)
            val countryCode = detectCountryCode(ps)
            VpnServer(
                name = ps,
                countryCode = countryCode,
                flagEmoji = getFlagEmoji(countryCode),
                protocol = "VMess",
                serverAddress = add,
                port = port,
                configUrl = link,
                jsonConfig = jsonString
            )
        } catch (e: Exception) {
            val uri = URI(link)
            val name = decodeUrlComponent(uri.fragment ?: "VMess Node")
            val countryCode = detectCountryCode(name)
            VpnServer(
                name = name,
                countryCode = countryCode,
                flagEmoji = getFlagEmoji(countryCode),
                protocol = "VMess",
                serverAddress = uri.host ?: "127.0.0.1",
                port = if (uri.port != -1) uri.port else 443,
                configUrl = link
            )
        }
    }

    private fun parseVLess(link: String): VpnServer? {
        return try {
            val uri = URI(link)
            val name = decodeUrlComponent(uri.fragment ?: "VLESS Cyber Node")
            val host = uri.host ?: "127.0.0.1"
            val port = if (uri.port != -1) uri.port else 443
            val countryCode = detectCountryCode(name)
            VpnServer(
                name = name,
                countryCode = countryCode,
                flagEmoji = getFlagEmoji(countryCode),
                protocol = "VLESS",
                serverAddress = host,
                port = port,
                configUrl = link
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun parseTrojan(link: String): VpnServer? {
        return try {
            val uri = URI(link)
            val name = decodeUrlComponent(uri.fragment ?: "Trojan Secure")
            val host = uri.host ?: "127.0.0.1"
            val port = if (uri.port != -1) uri.port else 443
            val countryCode = detectCountryCode(name)
            VpnServer(
                name = name,
                countryCode = countryCode,
                flagEmoji = getFlagEmoji(countryCode),
                protocol = "Trojan",
                serverAddress = host,
                port = port,
                configUrl = link
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun parseShadowsocks(link: String): VpnServer? {
        return try {
            val uri = URI(link)
            val name = decodeUrlComponent(uri.fragment ?: "Shadowsocks Node")
            val host = uri.host ?: "127.0.0.1"
            val port = if (uri.port != -1) uri.port else 8388
            val countryCode = detectCountryCode(name)
            VpnServer(
                name = name,
                countryCode = countryCode,
                flagEmoji = getFlagEmoji(countryCode),
                protocol = "Shadowsocks",
                serverAddress = host,
                port = port,
                configUrl = link
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun parseHysteria2(link: String): VpnServer? {
        return try {
            val uri = URI(link)
            val name = decodeUrlComponent(uri.fragment ?: "Hysteria2 High Speed")
            val host = uri.host ?: "127.0.0.1"
            val port = if (uri.port != -1) uri.port else 8443
            val countryCode = detectCountryCode(name)
            VpnServer(
                name = name,
                countryCode = countryCode,
                flagEmoji = getFlagEmoji(countryCode),
                protocol = "Hysteria2",
                serverAddress = host,
                port = port,
                configUrl = link
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun parseTuic(link: String): VpnServer? {
        return try {
            val uri = URI(link)
            val name = decodeUrlComponent(uri.fragment ?: "TUIC Node")
            val host = uri.host ?: "127.0.0.1"
            val port = if (uri.port != -1) uri.port else 8443
            val countryCode = detectCountryCode(name)
            VpnServer(
                name = name,
                countryCode = countryCode,
                flagEmoji = getFlagEmoji(countryCode),
                protocol = "TUIC",
                serverAddress = host,
                port = port,
                configUrl = link
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun parseWireGuard(link: String): VpnServer? {
        return try {
            val uri = URI(link)
            val name = decodeUrlComponent(uri.fragment ?: "WireGuard Tunnel")
            val host = uri.host ?: "127.0.0.1"
            val port = if (uri.port != -1) uri.port else 51820
            val countryCode = detectCountryCode(name)
            VpnServer(
                name = name,
                countryCode = countryCode,
                flagEmoji = getFlagEmoji(countryCode),
                protocol = "WireGuard",
                serverAddress = host,
                port = port,
                configUrl = link
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun parseSSH(link: String): VpnServer? {
        return try {
            val uri = URI(link)
            val name = decodeUrlComponent(uri.fragment ?: "SSH Tunnel Node")
            val host = uri.host ?: "127.0.0.1"
            val port = if (uri.port != -1) uri.port else 22
            val countryCode = detectCountryCode(name)
            VpnServer(
                name = name,
                countryCode = countryCode,
                flagEmoji = getFlagEmoji(countryCode),
                protocol = "SSH",
                serverAddress = host,
                port = port,
                configUrl = link
            )
        } catch (e: Exception) {
            null
        }
    }

    fun parseJsonConfig(jsonStr: String): VpnServer? {
        return try {
            val json = JSONObject(jsonStr)
            val name = json.optString("name", "Custom JSON Server")
            val server = json.optString("server", json.optString("address", "127.0.0.1"))
            val port = json.optInt("port", 443)
            val protocol = json.optString("protocol", "Xray")
            val countryCode = detectCountryCode(name)
            VpnServer(
                name = name,
                countryCode = countryCode,
                flagEmoji = getFlagEmoji(countryCode),
                protocol = protocol,
                serverAddress = server,
                port = port,
                jsonConfig = jsonStr
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun decodeUrlComponent(s: String): String {
        return try {
            URLDecoder.decode(s, "UTF-8")
        } catch (e: Exception) {
            s
        }
    }

    fun detectCountryCode(name: String): String {
        val lower = name.lowercase()
        return when {
            lower.contains("us") || lower.contains("united states") || lower.contains("america") || lower.contains("los angeles") || lower.contains("new york") -> "US"
            lower.contains("de") || lower.contains("germany") || lower.contains("frankfurt") -> "DE"
            lower.contains("jp") || lower.contains("japan") || lower.contains("tokyo") -> "JP"
            lower.contains("sg") || lower.contains("singapore") -> "SG"
            lower.contains("uk") || lower.contains("london") || lower.contains("britain") -> "UK"
            lower.contains("ca") || lower.contains("canada") || lower.contains("toronto") -> "CA"
            lower.contains("fi") || lower.contains("finland") || lower.contains("helsinki") -> "FI"
            lower.contains("nl") || lower.contains("netherlands") || lower.contains("amsterdam") -> "NL"
            lower.contains("hk") || lower.contains("hong kong") -> "HK"
            lower.contains("kr") || lower.contains("korea") || lower.contains("seoul") -> "KR"
            lower.contains("fr") || lower.contains("france") || lower.contains("paris") -> "FR"
            lower.contains("au") || lower.contains("australia") || lower.contains("sydney") -> "AU"
            else -> "US"
        }
    }

    fun getFlagEmoji(countryCode: String): String {
        return when (countryCode.uppercase()) {
            "US" -> "🇺🇸"
            "DE" -> "🇩🇪"
            "JP" -> "🇯🇵"
            "SG" -> "🇸🇬"
            "UK" -> "🇬🇧"
            "CA" -> "🇨🇦"
            "FI" -> "🇫🇮"
            "NL" -> "🇳🇱"
            "HK" -> "🇭🇰"
            "KR" -> "🇰🇷"
            "FR" -> "🇫🇷"
            "AU" -> "🇦🇺"
            else -> "🌐"
        }
    }

    fun getInitialDummyServers(): List<VpnServer> {
        return listOf(
            VpnServer(
                id = 1,
                name = "⚡ Cyber Ultra - Frankfurt #01",
                countryCode = "DE",
                flagEmoji = "🇩🇪",
                protocol = "VLESS",
                serverAddress = "de-cyber.tech-vpn.net",
                port = 443,
                pingMs = 28,
                ipAddress = "185.220.101.5",
                isFavorite = true
            ),
            VpnServer(
                id = 2,
                name = "🚀 Cyber Turbo - Tokyo #03",
                countryCode = "JP",
                flagEmoji = "🇯🇵",
                protocol = "Hysteria2",
                serverAddress = "jp-cyber.tech-vpn.net",
                port = 8443,
                pingMs = 45,
                ipAddress = "133.242.180.12",
                isFavorite = true
            ),
            VpnServer(
                id = 3,
                name = "🔒 Cyber Shield - Los Angeles #02",
                countryCode = "US",
                flagEmoji = "🇺🇸",
                protocol = "VMess",
                serverAddress = "us-cyber.tech-vpn.net",
                port = 443,
                pingMs = 92,
                ipAddress = "104.28.201.88",
                isFavorite = false
            ),
            VpnServer(
                id = 4,
                name = "⚡ Cyber Express - Singapore #01",
                countryCode = "SG",
                flagEmoji = "🇸🇬",
                protocol = "Trojan",
                serverAddress = "sg-cyber.tech-vpn.net",
                port = 443,
                pingMs = 38,
                ipAddress = "128.199.200.15",
                isFavorite = false
            ),
            VpnServer(
                id = 5,
                name = "🛡️ Cyber Stealth - London #04",
                countryCode = "UK",
                flagEmoji = "🇬🇧",
                protocol = "WireGuard",
                serverAddress = "uk-cyber.tech-vpn.net",
                port = 51820,
                pingMs = 65,
                ipAddress = "178.62.199.11",
                isFavorite = false
            ),
            VpnServer(
                id = 6,
                name = "⚡ Cyber HY2 - Helsinki #01",
                countryCode = "FI",
                flagEmoji = "🇫🇮",
                protocol = "Hysteria2",
                serverAddress = "fi-cyber.tech-vpn.net",
                port = 8443,
                pingMs = 32,
                ipAddress = "95.216.155.3",
                isFavorite = false
            ),
            VpnServer(
                id = 7,
                name = "🌐 Cyber Mesh - Toronto #01",
                countryCode = "CA",
                flagEmoji = "🇨🇦",
                protocol = "TUIC",
                serverAddress = "ca-cyber.tech-vpn.net",
                port = 8443,
                pingMs = 110,
                ipAddress = "159.203.44.12",
                isFavorite = false
            ),
            VpnServer(
                id = 8,
                name = "🔑 Cyber Tunnel - Amsterdam SSH",
                countryCode = "NL",
                flagEmoji = "🇳🇱",
                protocol = "SSH",
                serverAddress = "nl-cyber.tech-vpn.net",
                port = 22,
                pingMs = 41,
                ipAddress = "188.166.30.22",
                isFavorite = false
            )
        )
    }
}
