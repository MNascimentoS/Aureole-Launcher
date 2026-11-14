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
    private fun CustomizeSheet(isLeftHanded: Boolean) {
        val sampleData = createSampleHomeData()
        val uiState = sampleData.uiState.copy(isLeftHandedMode = isLeftHanded)
        AureoleLauncherTheme {
            ProvideHomeUiState(uiState) {
                CustomizeBottomSheet(uiState = uiState, actions = sampleData.actions, onDismissRequest = {})
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
}
