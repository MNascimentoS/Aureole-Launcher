package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.CLOCK_CARD_THEME_REF
import dev.mnascimentos.aureole.core.data.model.ClockLayout
import dev.mnascimentos.aureole.core.data.model.ClockMessage
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions

private const val NOW_MILLIS = 1_700_000_000_000L

/** Style and Format pages of the clock editor: the message section, and the Date format with no date in the layout. */
class EditClockSectionsScreenshotTest {

    @Composable
    private fun Page(content: @Composable () -> Unit) {
        AureoleLauncherTheme {
            Column(modifier = Modifier.background(AureoleDS.colors.surface).padding(AureoleDS.dimens.medium)) {
                content()
            }
        }
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockStyleCustomText() = Page {
        EditClockStyleSection(
            uiState = MainUiState(clockStyle = ClockLayout.MSG_ON_TOP.key, clockMessage = ClockMessage.CUSTOM.key),
            actions = createDefaultHomeScreenActions(),
            tempCustomGreeting = "Bem-vindo",
            onGreetingChange = {}
        )
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockStyleTimeOnlyHidesMessage() = Page {
        EditClockStyleSection(
            uiState = MainUiState(clockStyle = ClockLayout.TIME_ONLY.key),
            actions = createDefaultHomeScreenActions(),
            tempCustomGreeting = "",
            onGreetingChange = {}
        )
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockFormatDateDisabled() = Page {
        EditClockFormatSection(
            uiState = MainUiState(clockStyle = ClockLayout.TIME_ONLY.key),
            actions = createDefaultHomeScreenActions(),
            nowMillis = NOW_MILLIS
        )
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockFormatDateEnabled() = Page {
        EditClockFormatSection(
            uiState = MainUiState(clockStyle = ClockLayout.DATE_ON_TOP.key),
            actions = createDefaultHomeScreenActions(),
            nowMillis = NOW_MILLIS
        )
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockColorsTwoRows() = Page {
        EditClockColorsSection(
            uiState = MainUiState(
                clockBackgroundColorRef = CLOCK_CARD_THEME_REF.toJson(),
                clockTextColor = 0xFF90CAF9.toInt()
            )
        )
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockColorsNoCard() = Page {
        EditClockColorsSection(uiState = MainUiState())
    }
}
