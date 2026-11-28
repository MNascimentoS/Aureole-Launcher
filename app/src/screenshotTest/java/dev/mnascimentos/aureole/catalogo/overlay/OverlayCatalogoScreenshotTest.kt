/**
 * Edit overlay.
 *
 * @en Edit overlay
 */
package dev.mnascimentos.aureole.catalogo.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.OverlaySheetBody
import dev.mnascimentos.aureole.core.designsystem.components.OverlaySheetGestureZone
import dev.mnascimentos.aureole.core.designsystem.components.OverlaySheetHeader
import dev.mnascimentos.aureole.core.designsystem.components.SheetSurface
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.model.GestureZoneState
import dev.mnascimentos.aureole.core.designsystem.model.OverlayHeaderTitle
import dev.mnascimentos.aureole.core.designsystem.model.OverlaySheetDecorations
import dev.mnascimentos.aureole.core.designsystem.model.OverlaySheetMotion
import dev.mnascimentos.aureole.core.designsystem.model.OverlaySheetStateData
import dev.mnascimentos.aureole.core.designsystem.model.SheetBounds
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleCatalogPreview
import dev.mnascimentos.aureole.fixtures.CatalogoColumn
import dev.mnascimentos.aureole.fixtures.CatalogoFrame
import dev.mnascimentos.aureole.fixtures.CatalogoLabel

private val OVERLAY_FRAME = 480.dp

/** Meio da linha do título: padding do corpo 4 dp + topo do cabeçalho 4 dp + metade do slot de 48 dp (RF-05). */
private val TITLE_LINE_CENTER = 32.dp

private const val TALL_SHEET_ROWS = 40
private const val PREVIEW_ROWS = 16
private const val REST_MAX_PX = 700f
private const val EXPANDED_PX = 1200f

/** Corpo da sheet com título, configurações, barra de baixo e conteúdo. */
@Composable
private fun SheetBodyCase() {
    OverlaySheetBody(
        title = "Preview Overlay Sheet",
        onSettingsClick = {},
        bottomBarContent = { AureoleText(text = "Bottom Bar Content", color = AureoleDS.colors.onSurfaceMedium) },
        sheetContent = { AureoleText(text = "Sheet Body Content", color = AureoleDS.colors.onSurfaceHigh) }
    )
}

/** RF-05: texto e logo sobre a régua vermelha no meio da linha do título, destro e canhoto. */
@Composable
private fun HeaderWithRuler(isLeftHanded: Boolean) {
    val rulerColor = AureoleDS.colors.error
    Box(
        modifier = Modifier.drawWithContent {
            drawContent()
            val y = TITLE_LINE_CENTER.toPx()
            drawLine(rulerColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.dp.toPx())
        }
    ) {
        OverlaySheetBody(
            title = "Container settings for a long title",
            onSettingsClick = {},
            bottomBarContent = null,
            sheetContent = null,
            decorations = OverlaySheetDecorations(isLeftHanded = isLeftHanded)
        )
    }
}

/** RF-19: título maior que a linha corta com reticências entre os slots de 48 dp. */
@Composable
private fun LongTitleCase(isLeftHanded: Boolean) {
    OverlaySheetBody(
        title = "Settings of the container on the left side of the home screen",
        onSettingsClick = {},
        bottomBarContent = null,
        sheetContent = null,
        decorations = OverlaySheetDecorations(isLeftHanded = isLeftHanded)
    )
}

/** RF-A1/A2/A4: a prévia cabe acima de uma sheet curta e só ocupa o que sobra acima de uma alta. */
@Composable
private fun PreviewFitCase(rows: Int) {
    AureoleEditOverlay(
        onDismissRequest = {},
        params = AureoleEditOverlayParams(
            config = AureoleEditOverlayConfig(title = "Clock style", animated = false),
            previewContent = {
                Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                    repeat(PREVIEW_ROWS) {
                        AureoleText(text = "Preview line ${it + 1}", color = AureoleDS.colors.onSurfaceHigh)
                    }
                }
            },
            sheetContent = {
                Column {
                    repeat(rows) { AureoleText(text = "Option ${it + 1}", color = AureoleDS.colors.onSurfaceHigh) }
                }
            }
        )
    )
}

/** A superfície da sheet expandida até o teto. */
@Composable
private fun SheetSurfaceExpandedCase() {
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = true, modifier = Modifier.align(Alignment.BottomCenter)) {
            SheetSurface(
                stateData = OverlaySheetStateData(
                    isExpanded = true,
                    onExpandedChanged = {},
                    triggerDismiss = {},
                    effectiveAnimated = false,
                    animScope = this
                ),
                params = AureoleEditOverlayParams(
                    config = AureoleEditOverlayConfig(title = "Preview Title", onSettingsClick = {}),
                    previewContent = { AureoleText("Preview Content") },
                    bottomBarContent = null,
                    sheetContent = { AureoleText("Sheet Content") }
                ),
                bounds = SheetBounds(restMaxPx = REST_MAX_PX, expandedPx = EXPANDED_PX),
                motion = remember { OverlaySheetMotion() }
            )
        }
    }
}

/**
 * Edit overlay.
 *
 * Grupo overlay: corpo e cabeçalho da sheet (régua, título longo, subtítulo; destro e canhoto), zona de gesto,
 * `AureoleEditOverlay` com sheet curta e alta e a superfície expandida, cada tela cheia numa moldura.
 *
 * @en Edit overlay
 */
@PreviewTest
@AureoleCatalogPreview
@Composable
private fun OverlayScreenshotTest() = CatalogoColumn {
    CatalogoLabel("OverlaySheetBody")
    SheetBodyCase()
    CatalogoLabel("OverlaySheetHeader régua: destro / canhoto")
    HeaderWithRuler(isLeftHanded = false)
    HeaderWithRuler(isLeftHanded = true)
    CatalogoLabel("OverlaySheetHeader título longo: destro / canhoto")
    LongTitleCase(isLeftHanded = false)
    LongTitleCase(isLeftHanded = true)
    CatalogoLabel("OverlaySheetHeader subtítulo")
    Box(modifier = Modifier.background(AureoleDS.colors.surface)) {
        OverlaySheetHeader(title = OverlayHeaderTitle(text = "Clock font", subtitle = "Only for this clock"))
    }
    CatalogoLabel("OverlaySheetGestureZone")
    OverlaySheetGestureZone(
        state = GestureZoneState(isExpanded = false, isLeftHanded = false),
        onToggleExpanded = {},
        onSettingsClick = {}
    )
    CatalogoLabel("AureoleEditOverlay: sheet curta")
    CatalogoFrame(OVERLAY_FRAME) { PreviewFitCase(rows = 1) }
    CatalogoLabel("AureoleEditOverlay: sheet alta")
    CatalogoFrame(OVERLAY_FRAME) { PreviewFitCase(rows = TALL_SHEET_ROWS) }
    CatalogoLabel("SheetSurface: expandida")
    CatalogoFrame(OVERLAY_FRAME) { SheetSurfaceExpandedCase() }
}
