package dev.mnascimentos.aureole.core.network

import dev.mnascimentos.aureole.core.network.model.TlsPinConfig
import dev.mnascimentos.aureole.core.telemetry.TelemetryLogger
import java.security.cert.X509Certificate

class TlsPinningException(
    val domain: String,
    val reason: String
) : Exception("TLS Pinning Validation Failed for domain '$domain': $reason")

class ApiClient(
    private val validator: SpkiPinValidator,
    private val telemetryLogger: TelemetryLogger
) {

    fun executeSecureRequest(
        config: TlsPinConfig,
        peerCertificates: List<X509Certificate>,
        requestAction: () -> String
    ): Result<String> {
        return when (val validationResult = validator.validate(config, peerCertificates)) {
            is SpkiValidationResult.Valid -> {
                runCatching { requestAction() }
            }

            is SpkiValidationResult.InsufficientPins -> {
                val minPins = SpkiPinValidator.MINIMUM_REQUIRED_PINS
                val reason = "Insufficient pins configured (minimum is $minPins, found ${validationResult.count})"
                telemetryLogger.logTlsPinningFailure(
                    domain = config.domain,
                    reason = reason,
                    expectedPins = config.pins
                )
                Result.failure(TlsPinningException(config.domain, reason))
            }

            is SpkiValidationResult.Expired -> {
                val expiredAt = validationResult.expirationTimestampMillis
                val reason = "TLS Pinning configuration expired at timestamp $expiredAt"
                telemetryLogger.logTlsPinningFailure(
                    domain = config.domain,
                    reason = reason,
                    expectedPins = config.pins
                )
                Result.failure(TlsPinningException(config.domain, reason))
            }

            is SpkiValidationResult.PinMismatch -> {
                val reason = "SPKI Hash Mismatch between server certificate and pinned keys"
                val details = mapOf(
                    "expectedPins" to config.pins.joinToString(","),
                    "actualHashes" to validationResult.actualSpkiHashes.joinToString(",")
                )
                telemetryLogger.logTlsPinningFailure(
                    domain = config.domain,
                    reason = reason,
                    expectedPins = config.pins,
                    details = details
                )
                Result.failure(TlsPinningException(config.domain, reason))
            }
        }
    }
}
