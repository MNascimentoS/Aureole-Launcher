package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockHeaderContentParams

@PreviewTest
@AureolePreview
@Composable
private fun ClockHeaderContentScreenshotTest() {
    AureoleLauncherTheme {
        Column {
            ClockHeaderContent(
                params = ClockHeaderContentParams(
                    maxHeight = 120.dp,
                    colorPrimary = android.graphics.Color.WHITE,
                    composeTextColor = Color.White,
                    greeting = "Boa tarde",
                    clockStyle = "STANDARD",
                    alignment = "CENTER",
                    fontFamily = "DEFAULT",
                    timeFormat = "12H",
                    dateFormat = "DEFAULT"
                )
            )
            ClockHeaderContent(
                params = ClockHeaderContentParams(
                    maxHeight = 120.dp,
                    colorPrimary = android.graphics.Color.WHITE,
                    composeTextColor = Color.White,
                    greeting = "Bom dia",
                    clockStyle = "DATE_ON_TOP",
                    alignment = "START",
                    fontFamily = "DEFAULT",
                    timeFormat = "24H",
                    dateFormat = "DEFAULT"
                )
            )
        }
    }
}
