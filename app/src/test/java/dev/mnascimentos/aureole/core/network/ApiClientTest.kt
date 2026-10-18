@file:Suppress("EmptyFunctionBlock")

package dev.mnascimentos.aureole.core.network

import dev.mnascimentos.aureole.core.network.model.TlsPinConfig
import dev.mnascimentos.aureole.core.telemetry.TelemetryLogger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigInteger
import java.security.Principal
import java.security.PublicKey
import java.security.cert.X509Certificate
import java.util.Date

class ApiClientTest {

    private lateinit var validator: SpkiPinValidator
    private lateinit var fakeTelemetryLogger: FakeTelemetryLogger
    private lateinit var apiClient: ApiClient

    @Before
    fun setUp() {
        validator = SpkiPinValidator()
        fakeTelemetryLogger = FakeTelemetryLogger()
        apiClient = ApiClient(validator, fakeTelemetryLogger)
    }

    @Test
    fun `when pin validation succeeds, should execute request action and return success`() {
        // Given
        val keyBytes = byteArrayOf(1, 2, 3, 4, 5)
        val cert = createFakeCertificate(keyBytes)
        val hash = validator.calculateSha256Spki(keyBytes)

        val config = TlsPinConfig(
            domain = "api.aureolelauncher.dev",
            pins = listOf("SHA-256/$hash", "SHA-256/backupPin"),
            expirationTimestampMillis = System.currentTimeMillis() + 100000
        )

        var actionExecuted = false

        // When
        val result = apiClient.executeSecureRequest(config, listOf(cert)) {
            actionExecuted = true
            "OK Response"
        }

        // Then
        assertTrue(result.isSuccess)
        assertEquals("OK Response", result.getOrNull())
        assertTrue(actionExecuted)
        assertEquals(0, fakeTelemetryLogger.loggedTlsFailuresCount)
    }

    @Test
    fun `when pin validation fails due to insufficient pins, should abort request and log telemetry`() {
        // Given
        val config = TlsPinConfig(
            domain = "api.aureolelauncher.dev",
            pins = listOf("SHA-256/onlyOnePin"),
            expirationTimestampMillis = System.currentTimeMillis() + 100000
        )
        val cert = createFakeCertificate(byteArrayOf(1, 2, 3))
        var actionExecuted = false

        // When
        val result = apiClient.executeSecureRequest(config, listOf(cert)) {
            actionExecuted = true
            "Should Not Execute"
        }

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is TlsPinningException)
        assertEquals(false, actionExecuted)
        assertEquals(1, fakeTelemetryLogger.loggedTlsFailuresCount)
        assertEquals("api.aureolelauncher.dev", fakeTelemetryLogger.lastFailedDomain)
    }

    @Test
    fun `when pin validation fails due to pin mismatch, should abort request and log telemetry`() {
        // Given
        val config = TlsPinConfig(
            domain = "api.aureolelauncher.dev",
            pins = listOf("SHA-256/invalidPin1", "SHA-256/invalidPin2"),
            expirationTimestampMillis = System.currentTimeMillis() + 100000
        )
        val cert = createFakeCertificate(byteArrayOf(9, 8, 7))
        var actionExecuted = false

        // When
        val result = apiClient.executeSecureRequest(config, listOf(cert)) {
            actionExecuted = true
            "Should Not Execute"
        }

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is TlsPinningException)
        assertEquals(false, actionExecuted)
        assertEquals(1, fakeTelemetryLogger.loggedTlsFailuresCount)
    }

    private class FakeTelemetryLogger : TelemetryLogger {
        var loggedTlsFailuresCount = 0
        var lastFailedDomain: String? = null
        var lastFailedReason: String? = null

        override fun logTlsPinningFailure(
            domain: String,
            reason: String,
            expectedPins: List<String>,
            details: Map<String, String>
        ) {
            loggedTlsFailuresCount++
            lastFailedDomain = domain
            lastFailedReason = reason
        }

        override fun logSecurityEvent(eventName: String, params: Map<String, String>) {}
    }

    private fun createFakeCertificate(publicKeyBytes: ByteArray): X509Certificate {
        val key = object : PublicKey {
            override fun getAlgorithm(): String = "RSA"
            override fun getFormat(): String = "X.509"
            override fun getEncoded(): ByteArray = publicKeyBytes
        }
        return object : X509Certificate() {
            override fun getPublicKey(): PublicKey = key
            override fun checkValidity() {}
            override fun checkValidity(date: Date?) {}
            override fun getVersion(): Int = 3
            override fun getSerialNumber(): BigInteger = BigInteger.ONE
            override fun getIssuerDN(): Principal? = null
            override fun getSubjectDN(): Principal? = null
            override fun getNotBefore(): Date? = null
            override fun getNotAfter(): Date? = null
            override fun getTBSCertificate(): ByteArray = byteArrayOf()
            override fun getSignature(): ByteArray = byteArrayOf()
            override fun getSigAlgName(): String = "SHA256withRSA"
            override fun getSigAlgOID(): String = "1.2.840.113549.1.1.11"
            override fun getSigAlgParams(): ByteArray? = null
            override fun getIssuerUniqueID(): BooleanArray? = null
            override fun getSubjectUniqueID(): BooleanArray? = null
            override fun getKeyUsage(): BooleanArray? = null
            override fun getBasicConstraints(): Int = -1
            override fun toString(): String = "FakeCert"
            override fun verify(key: PublicKey?) {}
            override fun verify(key: PublicKey?, sigProvider: String?) {}
            override fun getEncoded(): ByteArray = byteArrayOf()
            override fun getCriticalExtensionOIDs(): MutableSet<String>? = null
            override fun getExtensionValue(oid: String?): ByteArray? = null
            override fun getNonCriticalExtensionOIDs(): MutableSet<String>? = null
            override fun hasUnsupportedCriticalExtension(): Boolean = false
        }
    }
}
