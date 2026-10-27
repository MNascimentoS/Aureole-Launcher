package dev.mnascimentos.aureole.composable.clock.model

import android.graphics.Typeface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import dev.chrisbanes.haze.HazeState

data class ClockHazeConfig(
    val hazeState: HazeState? = null,
    val isHazeEnabled: Boolean = false,
    val hazeOpacity: Float = 0.5f,
    val isBackgroundEnabled: Boolean = true
)

data class ClockHeaderConfig(
    val style: String? = null,
    val customGreeting: String? = null,
    val alignment: String? = null,
    val fontFamily: String? = null,
    val timeFormat: String? = null,
    val dateFormat: String? = null,
    val textColor: Int? = null,
    val backgroundColor: Int? = null
)

internal data class ClockHeaderContentParams(
    val maxHeight: Dp,
    val colorPrimary: Int,
    val composeTextColor: Color,
    val greeting: String,
    val clockStyle: String,
    val alignment: String,
    val fontFamily: String,
    val timeFormat: String,
    val dateFormat: String
)

internal data class ClockTimeDisplayParams(
    val colorPrimary: Int,
    val textSizePx: Float,
    val format12: String,
    val format24: String,
    val typeface: Typeface,
    val alignment: String
)

data class ResolvedClockConfig(
    val style: String,
    val customGreeting: String,
    val alignment: String,
    val fontFamily: String,
    val timeFormat: String,
    val dateFormat: String,
    val textColor: Int,
    val backgroundColor: Int
)
