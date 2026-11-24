package dev.mnascimentos.aureole.telas.pastaformulario

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.dialogs.FolderFormSheet
import dev.mnascimentos.aureole.composable.dialogs.model.FolderFormParams
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRole
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.fixtures.HomeCase

private val SAMPLE_FOLDER = AppFolder(
    id = "1",
    name = "Bank",
    icon = "account_balance",
    color = ThemeColorRef.Role(ThemeColorRole.OUTLINE)
)

@Composable
private fun FolderFormCase(folder: AppFolder?) = HomeCase(MainUiState()) {
    FolderFormSheet(
        params = FolderFormParams(folder = folder, onDelete = if (folder != null) ({}) else null),
        onDismiss = {},
        onSave = {}
    )
}

/** Pasta nova: nome vazio sob o tile, segmento Ícone com as sugestões, "Criar e escolher apps". */
@PreviewTest
@AureolePreview
@Composable
private fun PadraoScreenshotTest() = FolderFormCase(folder = null)

/** Editar pasta: cor própria, ícone salvo marcado, "Excluir pasta" no fim, sem botões. */
@PreviewTest
@AureolePreview
@Composable
private fun EditarScreenshotTest() = FolderFormCase(folder = SAMPLE_FOLDER)
