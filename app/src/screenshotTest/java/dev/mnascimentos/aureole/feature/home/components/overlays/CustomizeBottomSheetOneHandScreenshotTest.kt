package dev.mnascimentos.aureole.feature.home.components.overlays

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.createSampleHomeData

/** Customize sheet (eight tiles, no header logo): right-handed and left-handed. */
class CustomizeBottomSheetOneHandScreenshotTest {

    @Composable
    private fun CustomizeSheet(isLeftHanded: Boolean, isShowcase: Boolean = false) {
        val sampleData = createSampleHomeData()
        val uiState = sampleData.uiState.copy(isLeftHandedMode = isLeftHanded)
        AureoleLauncherTheme {
            ProvideHomeUiState(uiState) {
                CustomizeBottomSheet(
                    uiState = uiState,
                    actions = sampleData.actions,
                    onDismissRequest = {},
                    isShowcase = isShowcase
                )
            }
        }
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun CustomizeSheetRightHanded() {
        CustomizeSheet(isLeftHanded = false)
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun CustomizeSheetLeftHanded() {
        CustomizeSheet(isLeftHanded = true)
    }

    /** Showcase of the onboarding (ajustes-onboarding RF-01/RF-02): the logo of the title inside its ring. */
    @PreviewTest
    @AureolePreview
    @Composable
    private fun CustomizeSheetShowcase() {
        CustomizeSheet(isLeftHanded = false, isShowcase = true)
    }
}
