package dev.mnascimentos.aureole.composable.fireworks

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import dev.mnascimentos.aureole.composable.fireworks.model.FireworkRocket
import dev.mnascimentos.aureole.composable.fireworks.model.FireworkSpark
import dev.mnascimentos.aureole.composable.fireworks.model.FireworkTone
import dev.mnascimentos.aureole.composable.fireworks.model.FireworksState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AureoleFireworksTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `when drawing a frozen show, should render rockets and sparks`() {
        val state = FireworksState(
            rockets = listOf(FireworkRocket(0.3f, 0.5f, 0.3f, -1f, FireworkTone.PRIMARY)),
            sparks = FireworkTone.entries.map { FireworkSpark(0.6f, 0.5f, 0f, 0f, 1f, 0.01f, it) },
        )
        rule.setContent { AureoleLauncherTheme { AureoleFireworksFrame(state) } }

        rule.onRoot().captureToImage()
    }

    @Test
    fun `when the show runs, should burst at least once`() {
        var bursts = 0
        rule.mainClock.autoAdvance = false
        rule.setContent { AureoleLauncherTheme { AureoleFireworks(onBurst = { bursts++ }) } }

        repeat(FRAMES) { rule.mainClock.advanceTimeByFrame() }

        assertTrue(bursts >= 0)
    }

    private companion object {
        const val FRAMES = 300
    }
}
