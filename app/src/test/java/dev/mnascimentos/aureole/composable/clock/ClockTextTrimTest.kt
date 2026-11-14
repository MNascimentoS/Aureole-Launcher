package dev.mnascimentos.aureole.composable.clock

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.math.roundToInt

@RunWith(RobolectricTestRunner::class)
class ClockTextTrimTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `when trimming the bands, should shorten the layout by the ratios and keep the width`() {
        var size = IntSize.Zero
        var fullPx = 0
        rule.setContent {
            fullPx = with(LocalDensity.current) { BOX_DP.dp.roundToPx() }
            Box(
                Modifier
                    .trimVerticalBands(topRatio = 0.25f, bottomRatio = 0.25f)
                    .onSizeChanged { size = it }
            ) {
                Box(Modifier.size(BOX_DP.dp))
            }
        }
        rule.waitForIdle()

        assertEquals(fullPx, size.width)
        assertEquals(fullPx - 2 * (fullPx * 0.25f).roundToInt(), size.height)
    }

    private companion object {
        const val BOX_DP = 40
    }
}
