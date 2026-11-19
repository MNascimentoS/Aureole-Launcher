package dev.mnascimentos.aureole.core.designsystem.model

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LogoRingTest {

    @Test
    fun `when progress is set, the sweep should be the clamped progress`() {
        assertEquals(0f, logoRingSweepFraction(-1f), 0f)
        assertEquals(0.5f, logoRingSweepFraction(0.5f), 0f)
        assertEquals(1f, logoRingSweepFraction(3f), 0f)
    }

    @Test
    fun `when progress is null, the sweep should be the fixed indeterminate arc`() {
        assertEquals(LOGO_RING_INDETERMINATE_SWEEP, logoRingSweepFraction(null), 0f)
    }

    @Test
    fun `range info should be indeterminate without progress and clamped with it`() {
        assertEquals(ProgressBarRangeInfo.Indeterminate, logoRingRangeInfo(null))
        assertEquals(ProgressBarRangeInfo(1f, 0f..1f), logoRingRangeInfo(2f))
        assertEquals(ProgressBarRangeInfo(0.25f, 0f..1f), logoRingRangeInfo(0.25f))
    }

    @Test
    fun `ring should spin only without progress and with animations on`() {
        assertTrue(isLogoRingSpinning(progress = null, animationsEnabled = true))
        assertFalse(isLogoRingSpinning(progress = null, animationsEnabled = false))
        assertFalse(isLogoRingSpinning(progress = 0.4f, animationsEnabled = true))
    }
}
