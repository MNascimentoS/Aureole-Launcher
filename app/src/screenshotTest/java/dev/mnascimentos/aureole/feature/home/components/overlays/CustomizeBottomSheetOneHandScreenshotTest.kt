package dev.mnascimentos.aureole.feature.home.components.overlays

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.createSampleHomeData

/** Customize sheet with the clock and all apps already on the home: right-handed and left-handed. */
class CustomizeBottomSheetOneHandScreenshotTest {

    @Composable
    private fun CustomizeSheet(isLeftHanded: Boolean, withItemsOnHome: Boolean) {
        val sampleData = createSampleHomeData()
        val onHome = if (withItemsOnHome) {
            listOf(
                LauncherItemState(id = "clock", type = LauncherItemType.CLOCK),
                LauncherItemState(id = "all", type = LauncherItemType.ALL_APPS_LIST)
            )
        } else {
            emptyList()
        }
        val uiState = sampleData.uiState.copy(isLeftHandedMode = isLeftHanded, gridItems = onHome)
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
        CustomizeSheet(isLeftHanded = false, withItemsOnHome = true)
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun CustomizeSheetLeftHanded() {
        CustomizeSheet(isLeftHanded = true, withItemsOnHome = true)
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun CustomizeSheetEmptyHome() {
        CustomizeSheet(isLeftHanded = false, withItemsOnHome = false)
    }
}
