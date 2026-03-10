package com.macaron.blocktel

import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log

/**
 * Core call screening service.
 * Intercepts incoming calls and rejects those matching blocked prefixes.
 * Runs as a system service — no UI, no activity needed.
 */
class CallBlockerService : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        val app = application as BlockTelApp
        val repo = app.prefixRepository
        val log = app.blockedCallLog

        if (!repo.isBlockingActive()) {
            respondAllow(callDetails)
            return
        }

        val handle = callDetails.handle
        val number = handle?.schemeSpecificPart

        if (number == null) {
            respondAllow(callDetails)
            return
        }

        val enabledPrefixes = repo.getEnabledPrefixes()
        val matchedPrefix = NumberNormalizer.matchesAnyPrefix(number, enabledPrefixes)

        if (matchedPrefix != null) {
            val normalized = NumberNormalizer.normalize(number) ?: number
            Log.i(TAG, "Blocked: $normalized (prefix $matchedPrefix)")
            log.log(normalized, matchedPrefix)
            respondReject(callDetails)
        } else {
            respondAllow(callDetails)
        }
    }

    private fun respondAllow(callDetails: Call.Details) {
        respondToCall(
            callDetails,
            CallResponse.Builder()
                .setDisallowCall(false)
                .setRejectCall(false)
                .setSkipCallLog(false)
                .setSkipNotification(false)
                .build()
        )
    }

    private fun respondReject(callDetails: Call.Details) {
        respondToCall(
            callDetails,
            CallResponse.Builder()
                .setDisallowCall(true)
                .setRejectCall(true)
                .setSkipCallLog(false)
                .setSkipNotification(true)
                .build()
        )
    }

    companion object {
        private const val TAG = "BLOCkTEL"
    }
}
