package com.example.utils

import android.content.Context
import android.content.Intent
import android.net.VpnService

object PermissionsHelper {

    fun isVpnPermissionGranted(context: Context): Boolean {
        val prepareIntent = VpnService.prepare(context)
        return prepareIntent == null
    }

    fun getVpnPrepareIntent(context: Context): Intent? {
        return VpnService.prepare(context)
    }
}
