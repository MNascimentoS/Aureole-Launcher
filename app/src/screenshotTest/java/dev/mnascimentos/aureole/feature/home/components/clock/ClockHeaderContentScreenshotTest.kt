package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.clock.ClockHeaderContent
import dev.mnascimentos.aureole.composable.clock.model.ClockHeaderContentParams
import dev.mnascimentos.aureole.composable.clock.model.resolveClockSizing
import dev.mnascimentos.aureole.core.data.model.ClockLayout
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleThemeConfig
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@PreviewTest
@AureolePreview
@Composable
private fun ClockHeaderStandardCenterScreenshotTest() {
    AureoleLauncherTheme {
        ClockHeaderContent(
            params = ClockHeaderContentParams(
                sizing = resolveClockSizing(
                    ClockLayout.MSG_TIME_DATE.lines,
                    maxTextWidthDp = 300f, maxHeightDp = 152f
                ),
                colorPrimary = android.graphics.Color.WHITE,
                composeTextColor = Color.White,
                greeting = "Boa tarde",
                layout = ClockLayout.MSG_TIME_DATE,
                alignment = "CENTER",
                fontFamily = "DEFAULT",
                timeFormat = "12H",
                dateFormat = "DEFAULT"
            )
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun ClockHeaderDateOnTopStartScreenshotTest() {
    AureoleLauncherTheme {
        ClockHeaderContent(
            params = ClockHeaderContentParams(
                sizing = resolveClockSizing(
                    ClockLayout.DATE_ON_TOP.lines,
                    maxTextWidthDp = 300f, maxHeightDp = 152f
                ),
                colorPrimary = android.graphics.Color.WHITE,
                composeTextColor = Color.White,
                greeting = "Bom dia",
                layout = ClockLayout.DATE_ON_TOP,
                alignment = "START",
                fontFamily = "DEFAULT",
                timeFormat = "24H",
                dateFormat = "DEFAULT"
            )
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun ClockHeaderTimeOnlyEndScreenshotTest() {
    AureoleLauncherTheme {
        ClockHeaderContent(
            params = ClockHeaderContentParams(
                sizing = resolveClockSizing(
                    ClockLayout.TIME_ONLY.lines,
                    maxTextWidthDp = 300f, maxHeightDp = 152f
                ),
                colorPrimary = android.graphics.Color.WHITE,
                composeTextColor = Color.White,
                greeting = "",
                layout = ClockLayout.TIME_ONLY,
                alignment = "END",
                fontFamily = "DEFAULT",
                timeFormat = "24H",
                dateFormat = "DEFAULT"
            )
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun ClockHeaderThemeFontScreenshotTest() {
    AureoleLauncherTheme(config = AureoleThemeConfig(darkTheme = true, aureoleFontName = "Space Mono")) {
        ClockHeaderContent(
            params = ClockHeaderContentParams(
                sizing = resolveClockSizing(
                    ClockLayout.MSG_TIME_DATE.lines,
                    maxTextWidthDp = 300f, maxHeightDp = 152f
                ),
                colorPrimary = android.graphics.Color.WHITE,
                composeTextColor = Color.White,
                greeting = "Boa tarde",
                layout = ClockLayout.MSG_TIME_DATE,
                alignment = "CENTER",
                fontFamily = "SANS_SERIF",
                timeFormat = "24H",
                dateFormat = "DEFAULT"
            )
        )
    }
}
