package dev.mnascimentos.aureole.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.core.designsystem.model.GlassLevel
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.settings.components.CardsGlassBottomSheet
import dev.mnascimentos.aureole.feature.settings.model.BlurOpacity
import dev.mnascimentos.aureole.feature.settings.model.CardsGlassDraft
import dev.mnascimentos.aureole.feature.settings.model.CardsGlassParams

private const val CARD_RADIUS_DP = 16

@Composable
private fun CardsGlassCase(draft: CardsGlassDraft, isLeftHanded: Boolean) {
    AureoleLauncherTheme {
        ProvideHomeUiState(MainUiState(isLeftHandedMode = isLeftHanded)) {
            CardsGlassBottomSheet(
                params = CardsGlassParams(
                    initial = draft,
                    onChange = {},
                    onDismissRequest = {},
                    isLeftHanded = isLeftHanded
                )
            )
        }
    }
}

private val MediumGlass = CardsGlassDraft(CARD_RADIUS_DP, GlassLevel.MEDIUM, BlurOpacity.MEDIUM.value)
private val FlatNoGlass = CardsGlassDraft(0, GlassLevel.NONE, BlurOpacity.SOLID.value)

@PreviewTest
@AureolePreview
@Composable
private fun CardsGlassSheetRightHandedScreenshotTest() = CardsGlassCase(MediumGlass, isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun CardsGlassSheetLeftHandedScreenshotTest() = CardsGlassCase(MediumGlass, isLeftHanded = true)

@PreviewTest
@AureolePreview
@Composable
private fun CardsGlassSheetFlatNoGlassScreenshotTest() = CardsGlassCase(FlatNoGlass, isLeftHanded = false)
