package dev.mnascimentos.aureole.composable.clock.model

import android.graphics.Typeface
import androidx.compose.ui.graphics.Color
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.data.model.ClockConfig
import dev.mnascimentos.aureole.core.data.model.ClockLayout

data class ClockHazeConfig(
    val hazeState: HazeState? = null,
    val isHazeEnabled: Boolean = false,
    val hazeOpacity: Float = 0.5f,
    val isBackgroundEnabled: Boolean = true
)

data class ClockHeaderConfig(
    val style: String? = null,
    val customGreeting: String? = null,
    val message: String? = null,
    val alignment: String? = null,
    val fontFamily: String? = null,
    val timeFormat: String? = null,
    val dateFormat: String? = null,
    val textColor: Int? = null,
    val backgroundColor: Int? = null,
    val textColorRef: String? = null,
    val backgroundColorRef: String? = null,
    val isBackgroundEnabled: Boolean? = null,
    val isFontBold: Boolean? = null
)

internal data class ClockHeaderContentParams(
    /** Sizes of the card, from its room (`resolveClockSizing`); the lines it draws follow [layout]. */
    val sizing: ClockSizing,
    val colorPrimary: Int,
    val composeTextColor: Color,
    val greeting: String,
    val layout: ClockLayout,
    val alignment: String,
    val fontFamily: String,
    val timeFormat: String,
    val dateFormat: String,
    val isFontBold: Boolean = ClockConfig.DEFAULT_FONT_BOLD
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
    val message: String = ClockConfig.DEFAULT_MESSAGE,
    val alignment: String,
    val fontFamily: String,
    val timeFormat: String,
    val dateFormat: String,
    val textColor: Int,
    val backgroundColor: Int,
    val isFontBold: Boolean = ClockConfig.DEFAULT_FONT_BOLD
)
