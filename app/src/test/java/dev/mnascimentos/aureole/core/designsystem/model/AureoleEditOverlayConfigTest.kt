package dev.mnascimentos.aureole.core.designsystem.model

import org.junit.Assert.assertEquals
import org.junit.Test

class AureoleEditOverlayConfigTest {

    private val log = mutableListOf<String>()

    @Test
    fun `when onDismiss is set, drag and tap outside should run it and not back`() {
        val config = AureoleEditOverlayConfig(onBackPress = { log += "back" }, onDismiss = { log += "dismiss" })

        config.dismissHandler { log += "fallback" }()

        assertEquals(listOf("dismiss"), log)
    }

    @Test
    fun `when only onBackPress is set, dismiss should fall back to it`() {
        val config = AureoleEditOverlayConfig(onBackPress = { log += "back" })

        config.dismissHandler { log += "fallback" }()

        assertEquals(listOf("back"), log)
    }

    @Test
    fun `when neither is set, dismiss should run the dismiss request`() {
        AureoleEditOverlayConfig().dismissHandler { log += "fallback" }()

        assertEquals(listOf("fallback"), log)
    }

    @Test
    fun `when onBackPress is set, the back key should run it and not hide the overlay`() {
        val config = AureoleEditOverlayConfig(onBackPress = { log += "back" }, onDismiss = { log += "dismiss" })

        config.backHandler { log += "hide" }()

        assertEquals(listOf("back"), log)
    }

    @Test
    fun `when onBackPress is not set, the back key should dismiss`() {
        AureoleEditOverlayConfig().backHandler { log += "hide" }()

        assertEquals(listOf("hide"), log)
    }
}
