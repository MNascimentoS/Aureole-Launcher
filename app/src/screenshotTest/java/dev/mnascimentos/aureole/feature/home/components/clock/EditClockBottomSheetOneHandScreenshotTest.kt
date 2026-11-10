package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.model.EditClockOverlayOptions
import dev.mnascimentos.aureole.feature.home.createSampleHomeData
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState

/** Clock editor on the one-hand sheet: rest (60%), expanded (90%) and left-handed footer. */
class EditClockBottomSheetOneHandScreenshotTest {

    @Composable
    private fun EditClockSheet(isExpanded: Boolean, isLeftHanded: Boolean) {
        val sampleData = createSampleHomeData()
        val uiState = sampleData.uiState.copy(isLeftHandedMode = isLeftHanded)
        AureoleLauncherTheme {
            CompositionLocalProvider(LocalHomeUiState provides uiState) {
                EditClockOverlay(
                    uiState = uiState,
                    actions = sampleData.actions,
                    onDismissRequest = {},
                    options = EditClockOverlayOptions(
                        overlayState = AureoleEditOverlayState(initialExpanded = isExpanded)
                    )
                )
            }
        }
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockBottomSheetRest() {
        EditClockSheet(isExpanded = false, isLeftHanded = false)
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockBottomSheetExpanded() {
        EditClockSheet(isExpanded = true, isLeftHanded = false)
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockBottomSheetLeftHanded() {
        EditClockSheet(isExpanded = false, isLeftHanded = true)
    }
}
