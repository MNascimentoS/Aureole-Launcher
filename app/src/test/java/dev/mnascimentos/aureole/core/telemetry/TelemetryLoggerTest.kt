package dev.mnascimentos.aureole.core.telemetry

import android.util.Log
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import timber.log.Timber

@RunWith(RobolectricTestRunner::class)
class TelemetryLoggerTest {

    private val tree = RecordingTree()

    @Before
    fun setUp() {
        Timber.plant(tree)
    }

    @After
    fun tearDown() {
        Timber.uproot(tree)
    }

    @Test
    fun `when a TLS pinning failure is logged, should log an error with domain, reason and pin count`() {
        DefaultTelemetryLogger().logTlsPinningFailure(
            domain = "api.example.com",
            reason = "pin mismatch",
            expectedPins = listOf("pinA", "pinB"),
            details = mapOf("cert" to "leaf")
        )

        val error = tree.logs.first { it.priority == Log.ERROR }
        assertEquals("TelemetryLogger", error.tag)
        assertEquals(
            "TLS Pinning Failure [domain=api.example.com, reason=pin mismatch, pinsCount=2]",
            error.message
        )
    }

    @Test
    fun `when a security event is logged, should log it at info level with its params`() {
        DefaultTelemetryLogger().logSecurityEvent("root_detected", mapOf("source" to "boot"))

        val info = tree.logs.first { it.priority == Log.INFO }
        assertEquals("Security Event: root_detected {source=boot}", info.message)
    }

    @Test
    fun `when Crashlytics is unavailable, should not crash and should warn instead`() {
        val logger = DefaultTelemetryLogger()

        logger.logTlsPinningFailure("api.example.com", "expired", emptyList())
        logger.logSecurityEvent("tamper")

        assertTrue(tree.logs.any { it.priority == Log.WARN })
    }

    private data class Entry(val priority: Int, val tag: String?, val message: String)

    private class RecordingTree : Timber.Tree() {
        val logs = mutableListOf<Entry>()

        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            logs += Entry(priority, tag, message)
        }
    }
}
