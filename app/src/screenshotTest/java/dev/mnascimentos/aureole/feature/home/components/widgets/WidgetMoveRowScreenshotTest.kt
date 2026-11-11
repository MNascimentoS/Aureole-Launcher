package dev.mnascimentos.aureole.feature.home.components.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetMoveParams
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.ProvideHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState

@Composable
private fun WidgetMoveRowCase(isLeftHanded: Boolean) {
    ProvideHomeUiState(MainUiState(isLeftHandedMode = isLeftHanded)) {
        AureoleLauncherTheme {
            Column(modifier = Modifier.padding(AureoleDS.dimens.medium)) {
                WidgetMoveRow(WidgetMoveParams("Clock", position = 1, count = 3, onPrevious = null, onNext = {}))
                WidgetMoveRow(WidgetMoveParams("Weather", position = 2, count = 3, onPrevious = {}, onNext = {}))
                WidgetMoveRow(WidgetMoveParams("Calendar", position = 3, count = 3, onPrevious = {}, onNext = null))
            }
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun WidgetMoveRowRightHandedScreenshotTest() = WidgetMoveRowCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun WidgetMoveRowLeftHandedScreenshotTest() = WidgetMoveRowCase(isLeftHanded = true)
