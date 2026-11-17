package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import androidx.compose.ui.geometry.Offset
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.EdgeScrubPath
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingMotionSpec
import org.junit.Assert.assertEquals
import org.junit.Test

class EdgeScrubPointTest {

    private val right = EdgeScrubPath(edgeX = 1000f, inwardSign = -1f, pullPx = 140f, startY = 400f, endY = 900f)
    private val left = right.copy(edgeX = 0f, inwardSign = 1f)

    private fun assertOffset(expected: Offset, actual: Offset) {
        assertEquals(expected.x, actual.x, EPSILON)
        assertEquals(expected.y, actual.y, EPSILON)
    }

    @Test
    fun `starts on the edge at the start height`() {
        assertOffset(Offset(1000f, 400f), edgeScrubPoint(0f, right))
    }

    @Test
    fun `at the end of the pull the finger is pulled in at the same height`() {
        assertOffset(Offset(860f, 400f), edgeScrubPoint(OnboardingMotionSpec.HAND_EDGE_PULL_END, right))
    }

    @Test
    fun `stays still during the pause`() {
        val pauseStart = edgeScrubPoint(OnboardingMotionSpec.HAND_EDGE_PULL_END, right)
        val pauseEnd = edgeScrubPoint(OnboardingMotionSpec.HAND_EDGE_SLIDE_START, right)
        assertOffset(pauseStart, pauseEnd)
    }

    @Test
    fun `ends at the end height`() {
        assertOffset(Offset(860f, 900f), edgeScrubPoint(OnboardingMotionSpec.HAND_EDGE_SLIDE_END, right))
        assertOffset(Offset(860f, 900f), edgeScrubPoint(1f, right))
    }

    @Test
    fun `left handed mode mirrors x`() {
        assertOffset(Offset(0f, 400f), edgeScrubPoint(0f, left))
        assertOffset(Offset(140f, 900f), edgeScrubPoint(1f, left))
    }

    private companion object {
        const val EPSILON = 0.001f
    }
}
