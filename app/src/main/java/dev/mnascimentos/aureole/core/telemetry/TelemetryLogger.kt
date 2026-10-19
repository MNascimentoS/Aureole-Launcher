package dev.mnascimentos.aureole.core.telemetry

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics

interface TelemetryLogger {
    fun logTlsPinningFailure(
        domain: String,
        reason: String,
        expectedPins: List<String>,
        details: Map<String, String> = emptyMap()
    )

    fun logSecurityEvent(
        eventName: String,
        params: Map<String, String> = emptyMap()
    )
}

class DefaultTelemetryLogger : TelemetryLogger {

    override fun logTlsPinningFailure(
        domain: String,
        reason: String,
        expectedPins: List<String>,
        details: Map<String, String>
    ) {
        val message = "TLS Pinning Failure [domain=$domain, reason=$reason, pinsCount=${expectedPins.size}]"
        Log.e(TAG, message)

        runCatching {
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.log(message)
            crashlytics.setCustomKey("tls_failure_domain", domain)
            crashlytics.setCustomKey("tls_failure_reason", reason)
            details.forEach { (key, value) ->
                crashlytics.setCustomKey("tls_details_$key", value)
            }
            crashlytics.recordException(SecurityException(message))
        }.onFailure { e ->
            Log.w(TAG, "Failed to record TLS failure event to Crashlytics", e)
        }
    }

    override fun logSecurityEvent(
        eventName: String,
        params: Map<String, String>
    ) {
        val message = "Security Event: $eventName $params"
        Log.i(TAG, message)

        runCatching {
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.log(message)
            params.forEach { (key, value) ->
                crashlytics.setCustomKey(key, value)
            }
        }.onFailure { e ->
            Log.w(TAG, "Failed to record security event to Crashlytics", e)
        }
    }

    private companion object {
        private const val TAG = "TelemetryLogger"
    }
}
