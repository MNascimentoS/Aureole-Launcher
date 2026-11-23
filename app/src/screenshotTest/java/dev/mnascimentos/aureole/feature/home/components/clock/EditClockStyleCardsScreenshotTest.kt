package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockLayoutOption

/** Style picker of the clock editor with a card selected. */
class EditClockStyleCardsScreenshotTest {

    @Composable
    private fun StyleCards(selected: ClockLayoutOption, alignment: String = "CENTER") {
        AureoleLauncherTheme {
            EditClockStyleCards(
                selectedKey = selected.key,
                onSelect = {},
                alignment = alignment,
                modifier = Modifier.background(AureoleDS.colors.surface).padding(AureoleDS.dimens.medium)
            )
        }
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockStyleCardsDateOnTop() {
        StyleCards(ClockLayoutOption.DATE_ON_TOP)
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockStyleCardsGreetingAndDate() {
        StyleCards(ClockLayoutOption.MSG_TIME_DATE)
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockStyleCardsAlignedStart() {
        StyleCards(ClockLayoutOption.MSG_TIME_DATE, alignment = "START")
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockStyleCardsAlignedCenter() {
        StyleCards(ClockLayoutOption.MSG_TIME_DATE, alignment = "CENTER")
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockStyleCardsAlignedEnd() {
        StyleCards(ClockLayoutOption.MSG_TIME_DATE, alignment = "END")
    }
}
