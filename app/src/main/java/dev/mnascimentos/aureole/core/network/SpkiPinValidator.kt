package dev.mnascimentos.aureole.core.network

import dev.mnascimentos.aureole.core.network.model.TlsPinConfig
import java.security.MessageDigest
import java.security.cert.X509Certificate
import java.util.Base64

sealed class SpkiValidationResult {
    data object Valid : SpkiValidationResult()
    data class InsufficientPins(val count: Int) : SpkiValidationResult()
    data class Expired(val expirationTimestampMillis: Long) : SpkiValidationResult()
    data class PinMismatch(
        val expectedPins: List<String>,
        val actualSpkiHashes: List<String>
    ) : SpkiValidationResult()
}

class SpkiPinValidator {

    fun validate(
        config: TlsPinConfig,
        peerCertificates: List<X509Certificate>,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): SpkiValidationResult {
        if (config.pins.size < MINIMUM_REQUIRED_PINS) {
            return SpkiValidationResult.InsufficientPins(config.pins.size)
        }

        if (currentTimeMillis > config.expirationTimestampMillis) {
            return SpkiValidationResult.Expired(config.expirationTimestampMillis)
        }

        val actualHashes = peerCertificates.map { cert ->
            calculateSha256Spki(cert.publicKey.encoded)
        }

        val hasMatch = config.pins.any { expectedPin ->
            val cleanExpected = expectedPin.removePrefix("SHA-256/").removePrefix("sha256/")
            actualHashes.any { actualHash ->
                actualHash.equals(cleanExpected, ignoreCase = true)
            }
        }

        return if (hasMatch) {
            SpkiValidationResult.Valid
        } else {
            SpkiValidationResult.PinMismatch(
                expectedPins = config.pins,
                actualSpkiHashes = actualHashes
            )
        }
    }

    internal fun calculateSha256Spki(publicKeyEncoded: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(publicKeyEncoded)
        return Base64.getEncoder().encodeToString(digest)
    }

    companion object {
        const val MINIMUM_REQUIRED_PINS = 2
    }
}
