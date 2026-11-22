package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.composable.clock.model.ClockHeaderConfig
import dev.mnascimentos.aureole.core.data.model.ClockLayout
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayState
import dev.mnascimentos.aureole.core.designsystem.model.previewAlpha
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.model.EditClockOverlayOptions
import dev.mnascimentos.aureole.feature.home.createSampleHomeData

/**
 * The clock preview above the sheet: centered vertically in its card in three layouts (RF-D3, RF-D4), and the whole
 * card, uncut, at half opacity as the sheet expands (RF-G2, RF-G3).
 */
class EditClockPreviewScreenshotTest {

    @Composable
    private fun ClockSheetWithLayout(layout: ClockLayout) {
        val sampleData = createSampleHomeData()
        val uiState = sampleData.uiState.copy(clockStyle = layout.key)
        AureoleLauncherTheme {
            ProvideHomeUiState(uiState) {
                EditClockOverlay(
                    uiState = uiState,
                    actions = sampleData.actions,
                    onDismissRequest = {},
                    options = EditClockOverlayOptions(
                        overlayState = AureoleEditOverlayState(),
                        nowMillis = FIXED_NOW_MILLIS
                    )
                )
            }
        }
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockPreviewTimeOnly() {
        ClockSheetWithLayout(ClockLayout.TIME_ONLY)
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockPreviewDateOnTop() {
        ClockSheetWithLayout(ClockLayout.DATE_ON_TOP)
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockPreviewMsgTimeDate() {
        ClockSheetWithLayout(ClockLayout.MSG_TIME_DATE)
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockPreviewHalfExpanded() {
        val sampleData = createSampleHomeData()
        AureoleLauncherTheme {
            ProvideHomeUiState(sampleData.uiState) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    HomeClockHeader(
                        modifier = Modifier
                            .padding(AureoleDS.dimens.medium)
                            .alpha(previewAlpha(HALF_EXPANDED)),
                        config = ClockHeaderConfig()
                    )
                }
            }
        }
    }
}

private const val HALF_EXPANDED = 0.5f

/** 2026-11-11 17:29 UTC, so the format tiles render the same on every run. */
private const val FIXED_NOW_MILLIS = 1_794_418_140_000L
