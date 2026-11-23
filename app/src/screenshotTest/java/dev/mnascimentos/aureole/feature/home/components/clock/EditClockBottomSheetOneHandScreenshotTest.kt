package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.core.data.model.CLOCK_CARD_THEME_REF
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockTab
import dev.mnascimentos.aureole.feature.home.components.clock.model.EditClockOverlayOptions
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.createSampleHomeData

/** Clock editor on the one-hand sheet: rest (60%), expanded (90%) and left-handed footer. */
class EditClockBottomSheetOneHandScreenshotTest {

    @Composable
    private fun EditClockSheet(
        isExpanded: Boolean,
        isLeftHanded: Boolean,
        tab: ClockTab = ClockTab.STYLE,
        hasBackgroundCard: Boolean = false,
        isOnboarding: Boolean = false,
        colorRole: ComponentColorRole? = null
    ) {
        val sampleData = createSampleHomeData()
        val uiState = sampleData.uiState.copy(
            isLeftHandedMode = isLeftHanded,
            clockBackgroundColorRef = CLOCK_CARD_THEME_REF.toJson().takeIf { hasBackgroundCard },
            onboarding = OnboardingUiState(
                stage = if (isOnboarding) OnboardingStage.CHECKLIST else OnboardingStage.COMPLETED
            )
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
                        nowMillis = FIXED_NOW_MILLIS,
                        initialColorRole = colorRole
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

    /** RF-D2: during onboarding the "everything is saved" card sits above the tabs. */
    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockBottomSheetOnboardingSaveHint() {
        EditClockSheet(isExpanded = false, isLeftHanded = false, isOnboarding = true)
    }

    /** RF-E4: the text color page inside the sheet, with back arrow and no tab bar. */
    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockBottomSheetTextColorPage() {
        EditClockSheet(isExpanded = false, isLeftHanded = false, colorRole = ComponentColorRole.TEXT)
    }

    /** RF-E4: the card color page inside the sheet, "None" first. */
    @PreviewTest
    @AureolePreview
    @Composable
    private fun EditClockBottomSheetCardColorPage() {
        EditClockSheet(isExpanded = false, isLeftHanded = false, colorRole = ComponentColorRole.CARD)
    }
}


/** 2026-11-11 17:29 UTC, so the format tiles render the same on every run. */
private const val FIXED_NOW_MILLIS = 1_794_418_140_000L
