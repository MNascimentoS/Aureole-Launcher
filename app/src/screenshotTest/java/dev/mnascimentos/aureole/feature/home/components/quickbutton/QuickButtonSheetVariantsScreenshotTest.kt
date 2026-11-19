package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.core.data.model.QuickButtonAppearance
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRole
import dev.mnascimentos.aureole.core.designsystem.palette.getPaletteByName
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleThemeConfig
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions

/** The quick button sheet in other states: name hidden, another theme and landscape (botao-rapido RF-01, RF-04). */
@Composable
private fun QuickButtonVariantCase(model: QuickButtonModel, paletteName: String? = null) {
    val uiState = MainUiState(activeQuickButtonModel = model)
    val config = paletteName?.let {
        AureoleThemeConfig(darkTheme = true, aureoleColors = getPaletteByName(it, isDarkTheme = true).colors)
    }
    ProvideHomeUiState(uiState) {
        val sheet = @Composable {
            QuickButtonAppearanceBottomSheet(
                uiState = uiState,
                actions = createDefaultHomeScreenActions(),
                onDismissRequest = {}
            )
        }
        if (config == null) AureoleLauncherTheme { sheet() } else AureoleLauncherTheme(config) { sheet() }
    }
}

private val ThemedModel = QuickButtonModel(
    appearance = QuickButtonAppearance(
        label = "Mail",
        iconColor = ThemeColorRef.Role(ThemeColorRole.ACCENT)
    )
)

/** Name hidden: the preview shows only the button, no label under it. */
@PreviewTest
@AureolePreview
@Composable
private fun QuickButtonAppearanceNameHiddenScreenshotTest() =
    QuickButtonVariantCase(QuickButtonModel(appearance = QuickButtonAppearance(label = "Mail", showLabel = false)))

/** The theme color of the icon follows the palette: Frostbite (default) and Neon Dusk. */
@PreviewTest
@AureolePreview
@Composable
private fun QuickButtonAppearanceThemeColorDefaultScreenshotTest() = QuickButtonVariantCase(ThemedModel)

@PreviewTest
@AureolePreview
@Composable
private fun QuickButtonAppearanceThemeColorOtherScreenshotTest() = QuickButtonVariantCase(ThemedModel, "Neon Dusk")

@PreviewTest
@AureoleLandscapePreview
@Composable
private fun QuickButtonAppearanceLandscapeScreenshotTest() =
    QuickButtonVariantCase(QuickButtonModel(appearance = QuickButtonAppearance(label = "Mail", showLabel = true)))
