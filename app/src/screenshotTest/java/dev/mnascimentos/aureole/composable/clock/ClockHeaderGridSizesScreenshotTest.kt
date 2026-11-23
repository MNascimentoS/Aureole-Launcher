package dev.mnascimentos.aureole.composable.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.clock.model.ClockHazeConfig
import dev.mnascimentos.aureole.composable.clock.model.ResolvedClockConfig
import dev.mnascimentos.aureole.core.data.model.ClockLayout
import dev.mnascimentos.aureole.core.data.model.ClockMessage
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/** One grid cell of a 411 dp wide home with four columns. */
private val CELL_WIDTH = 92.dp
private val CELL_HEIGHT = 88.dp

/** A cell shorter than usual: the date must still not touch the time (RF-B4). */
private val LOW_CELL_HEIGHT = 64.dp

private const val CARD_COLOR = 0xFF2B2F3A.toInt()

/**
 * The clock of the home at the grid sizes it can take (RF-B1 to RF-B3): every size follows the time, so the card
 * keeps the same organization from 4×2 down to a low 2×1.
 */
@Composable
private fun ClockCell(columns: Int, width: Dp = CELL_WIDTH * columns, height: Dp) {
    AureoleLauncherTheme {
        Box(modifier = Modifier.background(AureoleDS.colors.surface).padding(AureoleDS.dimens.xSmall)) {
            ClockHeader(
                resolvedConfig = ResolvedClockConfig(
                    style = ClockLayout.MSG_TIME_DATE.key,
                    // A custom message, so the greeting does not change with the hour of the run.
                    customGreeting = "Boa tarde",
                    message = ClockMessage.CUSTOM.key,
                    alignment = "CENTER",
                    fontFamily = "DEFAULT",
                    timeFormat = "24H",
                    dateFormat = "DEFAULT",
                    textColor = 0,
                    backgroundColor = CARD_COLOR
                ),
                modifier = Modifier.size(width, height),
                hazeConfig = ClockHazeConfig(isBackgroundEnabled = true)
            )
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun ClockGrid4x2ScreenshotTest() = ClockCell(columns = 4, height = CELL_HEIGHT * 2)

@PreviewTest
@AureolePreview
@Composable
private fun ClockGrid4x1ScreenshotTest() = ClockCell(columns = 4, height = CELL_HEIGHT)

@PreviewTest
@AureolePreview
@Composable
private fun ClockGrid2x1ScreenshotTest() = ClockCell(columns = 2, height = CELL_HEIGHT)

@PreviewTest
@AureolePreview
@Composable
private fun ClockGrid2x1LowScreenshotTest() = ClockCell(columns = 2, height = LOW_CELL_HEIGHT)
