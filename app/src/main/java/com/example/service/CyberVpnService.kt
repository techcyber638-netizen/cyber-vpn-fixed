package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import com.example.data.model.VpnState
import com.example.data.model.VpnStats
import com.example.data.model.VpnServer
import com.example.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class CyberVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var statsJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    inner class CyberVpnBinder : Binder() {
        fun getService(): CyberVpnService = this@CyberVpnService
    }

    private val binder = CyberVpnBinder()

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        when (action) {
            ACTION_CONNECT -> {
                val serverName = intent.getStringExtra(EXTRA_SERVER_NAME) ?: "Cyber Tech VPN"
                val engine = intent.getStringExtra(EXTRA_ENGINE) ?: "Xray-core"
                startVpnSession(serverName, engine)
            }
            ACTION_DISCONNECT -> {
                stopVpnSession()
            }
        }
        return START_STICKY
    }

    private fun startVpnSession(serverName: String, engine: String) {
        _vpnStateFlow.value = VpnState.CONNECTING
        
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification(serverName, "Connecting via $engine..."))

        serviceScope.launch {
            try {
                // Simulate handshake / core initialization
                delay(800)

                val builder = Builder()
                    .setSession(serverName)
                    .addAddress("10.8.0.2", 24)
                    .addDnsServer("1.1.1.1")
                    .addDnsServer("8.8.8.8")
                    .addRoute("0.0.0.0", 0)

                vpnInterface = builder.establish()

                _vpnStateFlow.value = VpnState.CONNECTED
                updateNotification(serverName, "Connected via $engine")

                startStatsSimulation()
            } catch (e: Exception) {
                _vpnStateFlow.value = VpnState.ERROR
                stopVpnSession()
            }
        }
    }

    private fun startStatsSimulation() {
        statsJob?.cancel()
        var currentDuration = 0L
        var totalDown = _vpnStatsFlow.value.totalDownBytes
        var totalUp = _vpnStatsFlow.value.totalUpBytes

        statsJob = serviceScope.launch {
            while (_vpnStateFlow.value == VpnState.CONNECTED) {
                delay(1000)
                currentDuration++

                // Realistic active internet speed fluctuations (Mbps / KBps converted to Bps)
                val isDownloadingBurst = Random.nextInt(100) > 40
                val downBps = if (isDownloadingBurst) Random.nextLong(2_500_000L, 18_000_000L) else Random.nextLong(150_000L, 800_000L)
                val upBps = Random.nextLong(80_000L, 2_200_000L)

                totalDown += downBps
                totalUp += upBps

                _vpnStatsFlow.value = VpnStats(
                    downSpeedBps = downBps,
                    upSpeedBps = upBps,
                    totalDownBytes = totalDown,
                    totalUpBytes = totalUp,
                    sessionDurationSeconds = currentDuration
                )
            }
        }
    }

    private fun stopVpnSession() {
        _vpnStateFlow.value = VpnState.DISCONNECTING
        statsJob?.cancel()
        statsJob = null

        try {
            vpnInterface?.close()
            vpnInterface = null
        } catch (e: Exception) {
            // ignore
        }

        _vpnStateFlow.value = VpnState.DISCONNECTED
        _vpnStatsFlow.value = _vpnStatsFlow.value.copy(
            downSpeedBps = 0L,
            upSpeedBps = 0L
        )

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        stopVpnSession()
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Cyber Tech VPN Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "VPN Connection Status"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(serverName: String, statusText: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Cyber Tech VPN")
            .setContentText("$serverName - $statusText")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(serverName: String, statusText: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, createNotification(serverName, statusText))
    }

    companion object {
        const val ACTION_CONNECT = "com.example.cybertech.CONNECT"
        const val ACTION_DISCONNECT = "com.example.cybertech.DISCONNECT"
        const val EXTRA_SERVER_NAME = "extra_server_name"
        const val EXTRA_ENGINE = "extra_engine"

        private const val CHANNEL_ID = "cyber_vpn_channel"
        private const val NOTIFICATION_ID = 9091

        private val _vpnStateFlow = MutableStateFlow(VpnState.DISCONNECTED)
        val vpnStateFlow: StateFlow<VpnState> = _vpnStateFlow.asStateFlow()

        private val _vpnStatsFlow = MutableStateFlow(VpnStats())
        val vpnStatsFlow: StateFlow<VpnStats> = _vpnStatsFlow.asStateFlow()
    }
}
