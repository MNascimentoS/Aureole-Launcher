package dev.mnascimentos.aureole.telas.menushome

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.folder.ContainerFolderBottomSheet
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerFolderBottomSheetParams
import dev.mnascimentos.aureole.feature.home.components.widgets.WidgetStackBottomSheet
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetStackBottomSheetParams
import dev.mnascimentos.aureole.feature.home.grid.EditGridItemDialog
import dev.mnascimentos.aureole.fixtures.HomeCase
import dev.mnascimentos.aureole.fixtures.createSampleHomeData

@Composable
private fun MenuCase(content: @Composable () -> Unit) = HomeCase(createSampleHomeData().uiState, content = content)

/** Menu de uma pasta dentro de um container: editar atalhos, nome, ícone, ajustes; Remover por último. */
@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun PadraoScreenshotTest() = MenuCase {
    ContainerFolderBottomSheet(
        params = ContainerFolderBottomSheetParams(
            onEditShortcuts = {},
            onRename = {},
            onIcon = {},
            onRemoveFolder = {},
            onOpenContainerSettings = {},
            onOpenSettings = {}
        ),
        onDismiss = {}
    )
}

/** Menu de um item da grade (relógio). */
@PreviewTest
@AureolePreview
@Composable
private fun ItemScreenshotTest() = MenuCase {
    EditGridItemDialog(
        item = LauncherItemState(id = "clock", type = LauncherItemType.CLOCK, col = 0, row = 0, colSpan = 2, rowSpan = 2),
        onDismissRequest = {},
        onDeleteConfirm = {}
    )
}

/** Menu de uma pilha de widgets. */
@PreviewTest
@AureolePreview
@Composable
private fun PilhaScreenshotTest() = MenuCase {
    WidgetStackBottomSheet(
        params = WidgetStackBottomSheetParams(
            widgetId = 1,
            stackId = "widget_stack_1",
            onEditStack = {},
            onToggleDots = {},
            onRemoveStack = {},
            appPackage = "com.example.widgets"
        ),
        onDismiss = {}
    )
}
