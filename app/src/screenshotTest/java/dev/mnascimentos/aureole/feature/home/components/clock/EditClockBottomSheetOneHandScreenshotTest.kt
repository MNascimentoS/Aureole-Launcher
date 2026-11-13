package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockTab
import dev.mnascimentos.aureole.feature.home.components.clock.model.EditClockOverlayOptions
import dev.mnascimentos.aureole.feature.home.createSampleHomeData
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.ProvideHomeUiState

/** Clock editor on the one-hand sheet: rest (60%), expanded (90%) and left-handed footer. */
class EditClockBottomSheetOneHandScreenshotTest {

    @Composable
    private fun EditClockSheet(
        isExpanded: Boolean,
        isLeftHanded: Boolean,
        tab: ClockTab = ClockTab.STYLE,
        hasBackgroundCard: Boolean = false
    ) {
        val sampleData = createSampleHomeData()
        val uiState = sampleData.uiState.copy(
            isLeftHandedMode = isLeftHanded,
            isClockBackgroundEnabled = hasBackgroundCard
        )
        AureoleLauncherTheme {
            ProvideHomeUiState(uiState) {
                EditClockOverlay(
                    uiState = uiState,
                    actions = sampleData.actions,
                    onDismissRequest = {},
                    options = EditClockOverlayOptions(
                        overlayState = AureoleEditOverlayState(initialExpanded = isExpanded),
                        initialTab = tab,
                        nowMillis = FIXED_NOW_MILLIS
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

    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockBottomSheetTabStyle() {
        EditClockSheet(isExpanded = false, isLeftHanded = false, tab = ClockTab.STYLE)
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockBottomSheetTabFormat() {
        EditClockSheet(isExpanded = false, isLeftHanded = false, tab = ClockTab.FORMAT)
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockBottomSheetTabColors() {
        EditClockSheet(isExpanded = false, isLeftHanded = false, tab = ClockTab.COLORS)
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockBottomSheetTabFormatLeftHanded() {
        EditClockSheet(isExpanded = false, isLeftHanded = true, tab = ClockTab.FORMAT)
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockBottomSheetExpandedFormat() {
        EditClockSheet(isExpanded = true, isLeftHanded = false, tab = ClockTab.FORMAT)
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockBottomSheetRestWithBackgroundCard() {
        EditClockSheet(isExpanded = false, isLeftHanded = false, hasBackgroundCard = true)
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockBottomSheetRestWithoutBackgroundCard() {
        EditClockSheet(isExpanded = false, isLeftHanded = false, hasBackgroundCard = false)
    }
}

/** 2026-11-11 17:29 UTC, so the format tiles render the same on every run. */
private const val FIXED_NOW_MILLIS = 1_794_418_140_000L
