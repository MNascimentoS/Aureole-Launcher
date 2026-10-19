@file:Suppress("EmptyFunctionBlock")

package dev.mnascimentos.aureole.core.network

import dev.mnascimentos.aureole.core.network.model.TlsPinConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigInteger
import java.security.Principal
import java.security.PublicKey
import java.security.cert.X509Certificate
import java.util.Date

class SpkiPinValidatorTest {

    private lateinit var validator: SpkiPinValidator

    @Before
    fun setUp() {
        validator = SpkiPinValidator()
    }

    @Test
    fun `when pin config has fewer than 2 pins, should return InsufficientPins`() {
        // Given
        val config = TlsPinConfig(
            domain = "api.aureolelauncher.dev",
            pins = listOf("SHA-256/pin1"),
            expirationTimestampMillis = System.currentTimeMillis() + 100000
        )
        val cert = createFakeCertificate(byteArrayOf(1, 2, 3))

        // When
        val result = validator.validate(config, listOf(cert))

        // Then
        assertTrue(result is SpkiValidationResult.InsufficientPins)
        assertEquals(1, (result as SpkiValidationResult.InsufficientPins).count)
    }

    @Test
    fun `when pin config is expired, should return Expired`() {
        // Given
        val currentTime = System.currentTimeMillis()
        val expiredTime = currentTime - 50000
        val config = TlsPinConfig(
            domain = "api.aureolelauncher.dev",
            pins = listOf("SHA-256/pin1", "SHA-256/pin2"),
            expirationTimestampMillis = expiredTime
        )
        val cert = createFakeCertificate(byteArrayOf(1, 2, 3))

        // When
        val result = validator.validate(config, listOf(cert), currentTimeMillis = currentTime)

        // Then
        assertTrue(result is SpkiValidationResult.Expired)
        assertEquals(expiredTime, (result as SpkiValidationResult.Expired).expirationTimestampMillis)
    }

    @Test
    fun `when certificate SPKI matches pinned key, should return Valid`() {
        // Given
        val keyBytes = byteArrayOf(10, 20, 30, 40)
        val cert = createFakeCertificate(keyBytes)
        val expectedHash = validator.calculateSha256Spki(keyBytes)

        val config = TlsPinConfig(
            domain = "api.aureolelauncher.dev",
            pins = listOf("SHA-256/$expectedHash", "SHA-256/backupPin"),
            expirationTimestampMillis = System.currentTimeMillis() + 100000
        )

        // When
        val result = validator.validate(config, listOf(cert))

        // Then
        assertEquals(SpkiValidationResult.Valid, result)
    }

    @Test
    fun `when certificate SPKI does not match pinned key, should return PinMismatch`() {
        // Given
        val certKeyBytes = byteArrayOf(1, 2, 3, 4)
        val cert = createFakeCertificate(certKeyBytes)

        val config = TlsPinConfig(
            domain = "api.aureolelauncher.dev",
            pins = listOf("SHA-256/pinOne", "SHA-256/pinTwo"),
            expirationTimestampMillis = System.currentTimeMillis() + 100000
        )

        // When
        val result = validator.validate(config, listOf(cert))

        // Then
        assertTrue(result is SpkiValidationResult.PinMismatch)
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
