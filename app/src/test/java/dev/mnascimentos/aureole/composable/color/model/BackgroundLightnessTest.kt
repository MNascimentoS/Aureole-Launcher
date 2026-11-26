package dev.mnascimentos.aureole.composable.color.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BackgroundLightnessTest {

    @Test
    fun `white is a light background and the Frostbite background is not`() {
        assertTrue(isLightBackground(0xFFFFFFFF.toInt()))
        assertFalse(isLightBackground(0xFF12181D.toInt()))
    }
}
