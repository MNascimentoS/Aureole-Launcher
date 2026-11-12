package dev.mnascimentos.aureole.composable.menu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.composable.menu.model.RadioOption
import dev.mnascimentos.aureole.composable.menu.model.RadioSheetParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.MainUiState

@Composable
private fun RadioSheetCase(isLeftHanded: Boolean) {
    ProvideHomeUiState(MainUiState(isLeftHandedMode = isLeftHanded)) {
        AureoleLauncherTheme {
            AureoleRadioSheet(
                params = RadioSheetParams(
                    title = "Sidebar Alignment",
                    options = listOf(
                        RadioOption("Top", "Top"),
                        RadioOption("Center", "Center"),
                        RadioOption("Bottom", "Bottom"),
                        RadioOption("Space Evenly", "Space Evenly"),
                        RadioOption("Space Between", "Space Between")
                    ),
                    selectedValue = "Center"
                ),
                onSelect = {},
                onDismiss = {}
            )
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun RadioSheetRightHandedScreenshotTest() = RadioSheetCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun RadioSheetLeftHandedScreenshotTest() = RadioSheetCase(isLeftHanded = true)
