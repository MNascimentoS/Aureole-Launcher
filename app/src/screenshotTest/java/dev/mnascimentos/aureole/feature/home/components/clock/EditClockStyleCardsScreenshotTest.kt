package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockStyleOption

/** Style picker of the clock editor with a card selected. */
class EditClockStyleCardsScreenshotTest {

    @Composable
    private fun StyleCards(selected: ClockStyleOption) {
        AureoleLauncherTheme {
            EditClockStyleCards(
                selectedKey = selected.key,
                onSelect = {},
                modifier = Modifier.background(AureoleDS.colors.surface).padding(AureoleDS.dimens.medium)
            )
        }
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockStyleCardsDateOnTop() {
        StyleCards(ClockStyleOption.DATE_ON_TOP)
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockStyleCardsGreetingAndDate() {
        StyleCards(ClockStyleOption.GREETING_AND_DATE)
    }
}
