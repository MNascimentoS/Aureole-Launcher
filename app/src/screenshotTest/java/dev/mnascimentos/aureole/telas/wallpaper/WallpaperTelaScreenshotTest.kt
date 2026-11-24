package dev.mnascimentos.aureole.telas.wallpaper

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.WallpaperAdjustments
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.components.wallpaper.WallpaperEditorOverlay
import dev.mnascimentos.aureole.feature.settings.model.WallpaperEditorParams
import dev.mnascimentos.aureole.feature.settings.model.WallpaperEditorTab
import dev.mnascimentos.aureole.fixtures.OrientedCase

/** Valores ajustados, para os sliders e a etiqueta de zoom não ficarem nos padrões. */
private val EDITED = WallpaperAdjustments(zoom = 2f, dim = 0.45f, blurDp = 8f)

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

/** Editor: home fantasma, moldura da tela, etiqueta de zoom e a sheet na aba Enquadrar. */
@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun PadraoScreenshotTest() = EditorCase(WallpaperEditorTab.FRAME, isLeftHanded = false)

/** Slider de escurecer com o valor (desfoque tem o mesmo layout). */
@PreviewTest
@AureolePreview
@Composable
private fun AjusteScreenshotTest() = EditorCase(WallpaperEditorTab.DIM, isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun CanhotoScreenshotTest() = EditorCase(WallpaperEditorTab.FRAME, isLeftHanded = true)
