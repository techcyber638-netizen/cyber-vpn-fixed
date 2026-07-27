package com.example.ui.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppSettings
import com.example.data.model.VpnServer
import com.example.data.model.VpnState
import com.example.data.model.VpnStats
import com.example.data.repository.VpnRepository
import com.example.service.CyberVpnService
import com.example.utils.PermissionsHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VpnViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VpnRepository(application)

    val vpnState: StateFlow<VpnState> = CyberVpnService.vpnStateFlow
    val vpnStats: StateFlow<VpnStats> = CyberVpnService.vpnStatsFlow
    val settings: StateFlow<AppSettings> = repository.appSettings

    val servers: StateFlow<List<VpnServer>> = repository.allServers
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedServer = MutableStateFlow<VpnServer?>(null)
    val selectedServer: StateFlow<VpnServer?> = combine(servers, settings) { serverList, currentSettings ->
        serverList.find { it.id == currentSettings.selectedServerId } ?: serverList.firstOrNull()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _isPingingAll = MutableStateFlow(false)
    val isPingingAll: StateFlow<Boolean> = _isPingingAll.asStateFlow()

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeDefaultServersIfEmpty()
        }
    }

    fun clearUiMessage() {
        _uiMessage.value = null
    }

    fun toggleVpnConnection() {
        when (vpnState.value) {
            VpnState.DISCONNECTED, VpnState.ERROR -> startVpn()
            VpnState.CONNECTED -> stopVpn()
            else -> { /* connecting or disconnecting */ }
        }
    }

    fun startVpn() {
        val context = getApplication<Application>()
        if (!PermissionsHelper.isVpnPermissionGranted(context)) {
            _uiMessage.value = "VPN Permission required"
            return
        }

        val server = selectedServer.value
        if (server == null) {
            _uiMessage.value = "Please select a server first"
            return
        }

        val intent = Intent(context, CyberVpnService::class.java).apply {
            action = CyberVpnService.ACTION_CONNECT
            putExtra(CyberVpnService.EXTRA_SERVER_NAME, server.name)
            putExtra(CyberVpnService.EXTRA_ENGINE, settings.value.engine)
        }
        context.startService(intent)
    }

    fun stopVpn() {
        val context = getApplication<Application>()
        val intent = Intent(context, CyberVpnService::class.java).apply {
            action = CyberVpnService.ACTION_DISCONNECT
        }
        context.startService(intent)
    }

    fun selectServer(server: VpnServer) {
        repository.setSelectedServerId(server.id)
        if (vpnState.value == VpnState.CONNECTED) {
            // Reconnect to new server if currently connected
            stopVpn()
            viewModelScope.launch {
                kotlinx.coroutines.delay(600)
                startVpn()
            }
        }
    }

    fun addServerFromLink(link: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val server = repository.addServerFromLink(link)
            if (server != null) {
                repository.setSelectedServerId(server.id)
                _uiMessage.value = "Imported ${server.protocol} server successfully!"
                onComplete(true)
            } else {
                _uiMessage.value = "Failed to parse config link"
                onComplete(false)
            }
        }
    }

    fun addServerFromJson(jsonStr: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val server = repository.addServerFromJson(jsonStr)
            if (server != null) {
                repository.setSelectedServerId(server.id)
                _uiMessage.value = "Imported JSON config successfully!"
                onComplete(true)
            } else {
                _uiMessage.value = "Invalid JSON config structure"
                onComplete(false)
            }
        }
    }

    fun fetchSubscription(subUrl: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val count = repository.fetchSubscriptionServers(subUrl)
            if (count > 0) {
                _uiMessage.value = "Successfully imported $count servers from subscription"
                onComplete(true)
            } else {
                _uiMessage.value = "Failed to fetch subscription URL"
                onComplete(false)
            }
        }
    }

    fun pingAllServers() {
        if (_isPingingAll.value) return
        viewModelScope.launch {
            _isPingingAll.value = true
            repository.pingAllServers(servers.value)
            _isPingingAll.value = false
            _uiMessage.value = "Batch ping testing completed!"
        }
    }

    fun pingSingleServer(server: VpnServer) {
        viewModelScope.launch {
            val ping = repository.pingSingleServer(server)
            _uiMessage.value = "Ping for ${server.name}: $ping ms"
        }
    }

    fun toggleFavorite(server: VpnServer) {
        viewModelScope.launch {
            repository.updateServer(server.copy(isFavorite = !server.isFavorite))
        }
    }

    fun deleteServer(server: VpnServer) {
        viewModelScope.launch {
            repository.deleteServer(server)
            _uiMessage.value = "Server deleted"
        }
    }

    fun setEngine(engineName: String) {
        repository.setEngine(engineName)
        _uiMessage.value = "Core Engine set to $engineName"
    }

    fun setPingUrl(url: String) {
        repository.setPingUrl(url)
    }

    fun togglePerAppProxy(enabled: Boolean) {
        repository.togglePerAppProxy(enabled)
    }

    fun setNotificationTitle(title: String) {
        repository.setNotificationTitle(title)
    }

    fun toggleNotification(show: Boolean) {
        repository.toggleNotification(show)
    }
}
