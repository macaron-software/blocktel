package com.macaron.blocktel

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Ensures the service is available after device reboot.
 * CallScreeningService is registered via manifest, so this just logs readiness.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.i("BLOCkTEL", "Boot completed — call screening service ready")
        }
    }
}
