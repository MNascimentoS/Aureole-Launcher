package dev.mnascimentos.aureole.catalogo.dialogos

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.components.AureoleDialog
import dev.mnascimentos.aureole.core.designsystem.components.AureoleDialogHeader
import dev.mnascimentos.aureole.core.designsystem.components.AureoleOverlayBackdrop
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupBox
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupHeader
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Smile
import dev.mnascimentos.aureole.core.designsystem.model.AureoleDialogConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureolePopupHeaderParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleCatalogPreview
import dev.mnascimentos.aureole.fixtures.CatalogoColumn
import dev.mnascimentos.aureole.fixtures.CatalogoFrame
import dev.mnascimentos.aureole.fixtures.CatalogoLabel

private val DIALOG_FRAME = 320.dp
private val BACKDROP_FRAME = 160.dp
private val POPUP_WIDTH = 260.dp

@Composable
private fun DialogDefaultCase() {
    AureoleDialog(onDismissRequest = {}, title = "Sample Dialog Title") {
        AureoleText(
            text = "This is sample content inside AureoleDialog.",
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium),
        )
    }
}

@Composable
private fun DialogWithIconCase() {
    AureoleDialog(
        onDismissRequest = {},
        config = AureoleDialogConfig(title = "Dialog With Icon", onLogoClick = {}),
        icon = { m -> AureoleDS.icons.Smile(m) },
    ) {
        AureoleText(
            text = "Dialog content with leading icon and logo callback.",
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium),
        )
    }
}

/**
 * Grupo dialogos: `AureoleDialog` (padrão e com ícone e logo), o cabeçalho sozinho, `AureolePopup` com cabeçalho de
 * sessão e `AureoleOverlayBackdrop` sem blur. O dialog no preview fica centrado numa moldura (sem janela).
 */
@PreviewTest
@AureoleCatalogPreview
@Composable
private fun DialogosScreenshotTest() = CatalogoColumn {
    CatalogoLabel("AureoleDialog: padrão")
    CatalogoFrame(DIALOG_FRAME) { DialogDefaultCase() }
    CatalogoLabel("AureoleDialog: ícone e logo")
    CatalogoFrame(DIALOG_FRAME) { DialogWithIconCase() }
    CatalogoLabel("AureoleDialogHeader")
    AureoleDialogHeader(
        title = "Header Composition Title",
        icon = { m -> AureoleDS.icons.Smile(m) },
        onLogoClick = {},
    )
    CatalogoLabel("AureolePopup")
    AureolePopupBox(modifier = Modifier.padding(horizontal = AureoleDS.dimens.medium).width(POPUP_WIDTH)) {
        AureolePopupHeader(
            title = "Folder Name",
            params = AureolePopupHeaderParams(sessionTitle = "Session Subtitle")
        )
    }
    CatalogoLabel("AureoleOverlayBackdrop: sem blur")
    CatalogoFrame(BACKDROP_FRAME) {
        AureoleOverlayBackdrop(hazeState = null, modifier = Modifier.fillMaxSize())
    }
}
