package dev.mnascimentos.aureole.telas.ajustesfundo

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.core.data.model.WallpaperAdjustments
import dev.mnascimentos.aureole.core.designsystem.model.GlassLevel
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.settings.components.CardsGlassBottomSheet
import dev.mnascimentos.aureole.feature.settings.components.wallpaper.WallpaperEditorOverlay
import dev.mnascimentos.aureole.feature.settings.model.BlurOpacity
import dev.mnascimentos.aureole.feature.settings.model.CardsGlassDraft
import dev.mnascimentos.aureole.feature.settings.model.CardsGlassParams
import dev.mnascimentos.aureole.feature.settings.model.SettingsArea
import dev.mnascimentos.aureole.feature.settings.model.SettingsInnerKind
import dev.mnascimentos.aureole.feature.settings.model.SettingsSheet
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.feature.settings.model.WallpaperEditorParams
import dev.mnascimentos.aureole.feature.settings.model.WallpaperEditorTab
import dev.mnascimentos.aureole.fixtures.OrientedCase
import dev.mnascimentos.aureole.fixtures.SettingsSheetCase

private const val CARD_RADIUS_DP = 16

/** O seletor de fundo dentro da sheet Aparência. */
private val BackgroundSheet = SettingsSheet.Area(SettingsArea.APPEARANCE, inner = SettingsInnerKind.BACKGROUND)

/** Valores ajustados, para a miniatura da foto não sair nos padrões. */
private val EDITED = WallpaperAdjustments(zoom = 2f, dim = 0.45f, blurDp = 8f)

private val MediumGlass = CardsGlassDraft(CARD_RADIUS_DP, GlassLevel.MEDIUM, BlurOpacity.MEDIUM.value)

/** Seletor de fundo: cartões de tipo, amostras de cor e a mini-home ancorada em cima. */
@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun PadraoScreenshotTest() = SettingsSheetCase(sheet = BackgroundSheet)

/** Sheet "Vidro dos cartões" com raio e vidro médios. */
@PreviewTest
@AureolePreview
@Composable
private fun VidroScreenshotTest() {
    AureoleLauncherTheme {
        ProvideHomeUiState(MainUiState()) {
            CardsGlassBottomSheet(
                params = CardsGlassParams(
                    initial = MediumGlass,
                    onChange = {},
                    onCommit = {},
                    onDismissRequest = {},
                    isLeftHanded = false
                )
            )
        }
    }
}

/** Imagem própria: cartão com a miniatura e os botões Editar foto / Trocar foto, sem amostras. */
@PreviewTest
@AureolePreview
@Composable
private fun FotoScreenshotTest() = SettingsSheetCase(
    sheet = BackgroundSheet,
    uiState = SettingsUiState(
        isCustomWallpaperSet = true,
        customWallpaperPath = "",
        wallpaperAdjustments = EDITED
    )
)

@Composable
private fun EditorCase(tab: WallpaperEditorTab, isLeftHanded: Boolean) = OrientedCase {
    AureoleLauncherTheme {
        WallpaperEditorOverlay(
            params = WallpaperEditorParams(
                photoPath = null,
                initial = EDITED,
                onFinish = { _, _ -> },
                onDismissRequest = {},
                isLeftHanded = isLeftHanded,
                initialTab = tab
            )
        )
    }
}

/** Editor de papel de parede: home fantasma, moldura da tela, etiqueta de zoom e a sheet na aba Enquadrar. */
@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun EnquadrarScreenshotTest() = EditorCase(WallpaperEditorTab.FRAME, isLeftHanded = false)

/** Slider de escurecer com o valor (desfoque tem o mesmo layout). */
@PreviewTest
@AureolePreview
@Composable
private fun AjusteScreenshotTest() = EditorCase(WallpaperEditorTab.DIM, isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun CanhotoScreenshotTest() = EditorCase(WallpaperEditorTab.FRAME, isLeftHanded = true)
