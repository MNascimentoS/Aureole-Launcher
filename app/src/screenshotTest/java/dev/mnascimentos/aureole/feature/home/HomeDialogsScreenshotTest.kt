package dev.mnascimentos.aureole.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteItem
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteSection
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteType
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteVersion
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.EditClockBottomSheet
import dev.mnascimentos.aureole.feature.home.components.containers.EditContainerScreen
import dev.mnascimentos.aureole.feature.home.components.containers.model.EditContainerScreenParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.ContainerFolderBottomSheet
import dev.mnascimentos.aureole.feature.home.components.containers.folder.CreateFolderDialog
import dev.mnascimentos.aureole.feature.home.components.containers.folder.EditFolderDialog
import dev.mnascimentos.aureole.feature.home.components.containers.folder.FolderAppPickerDialog
import dev.mnascimentos.aureole.feature.home.components.containers.folder.FolderDialog
import dev.mnascimentos.aureole.feature.home.components.containers.folder.FolderFormDialog
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerFolderBottomSheetParams
import dev.mnascimentos.aureole.feature.home.components.favorites.FavoriteAppsDialog
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.feature.home.components.overlays.CustomizeBottomSheet
import dev.mnascimentos.aureole.feature.home.components.overlays.ReleaseNotesBottomSheet
import dev.mnascimentos.aureole.feature.home.components.overlays.StandardUpdateAlertDialog
import dev.mnascimentos.aureole.feature.home.components.overlays.UpdateAvailableDialog
import dev.mnascimentos.aureole.feature.home.components.overlays.UpdateDownloadedDialog
import dev.mnascimentos.aureole.feature.home.components.overlays.model.UpdateDialogContentParams
import dev.mnascimentos.aureole.feature.home.components.widgets.EditWidgetGroupScreen
import dev.mnascimentos.aureole.feature.home.components.widgets.WidgetSelectorScreen
import dev.mnascimentos.aureole.feature.home.components.widgets.WidgetStackBottomSheet
import dev.mnascimentos.aureole.feature.home.components.widgets.model.EditWidgetGroupActions
import dev.mnascimentos.aureole.feature.home.components.widgets.model.EditWidgetGroupParams
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetAppGroup
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetSelectorScreenParams
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetSelectorState
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetStackBottomSheetParams
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetVariant
import dev.mnascimentos.aureole.feature.home.grid.AddContainerDialog
import dev.mnascimentos.aureole.feature.home.grid.EditGridItemDialog

@PreviewTest
@AureolePreview
@Composable
private fun FavoriteAppsDialogScreenshotTest() {
    val sampleData = createSampleHomeData()
    val config = FavoriteAppsDialogConfig(
        allApps = sampleData.apps,
        favoriteAppPackages = listOf("com.example.phone", "com.example.camera")
    )
    val actions = FavoriteAppsDialogActions(
        onDismiss = {}
    )
    AureoleLauncherTheme {
        CompositionLocalProvider(LocalHomeUiState provides sampleData.uiState) {
            FavoriteAppsDialog(config = config, actions = actions)
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun EditContainerDialogScreenshotTest() {
    val sampleData = createSampleHomeData()
    val childItem = LauncherItemState(
        id = "child_1",
        type = LauncherItemType.APPS_LIST,
        col = 0,
        row = 0,
        colSpan = 2,
        rowSpan = 2
    )
    val item = LauncherItemState(
        id = "item_1",
        type = LauncherItemType.SCROLL_VIEW,
        col = 0,
        row = 0,
        colSpan = 4,
        rowSpan = 4,
        children = listOf(childItem)
    )
    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides sampleData.uiState,
            LocalHomeActions provides sampleData.actions
        ) {
            EditGridItemDialog(
                item = item,
                onDismissRequest = {},
                onDeleteConfirm = {}
            )
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun AddContainerDialogScreenshotTest() {
    AureoleLauncherTheme {
        AddContainerDialog(
            onDismissRequest = {},
            onSelectType = {}
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun CreateFolderDialogScreenshotTest() {
    AureoleLauncherTheme {
        CreateFolderDialog(
            onDismiss = {},
            onSubmit = {}
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun EditFolderDialogScreenshotTest() {
    val folder = AppFolder(id = "folder_work", name = "Work", icon = "briefcase")
    AureoleLauncherTheme {
        EditFolderDialog(
            folder = folder,
            onDismiss = {},
            onSave = { _, _ -> },
            onDelete = {}
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun FolderAppPickerDialogScreenshotTest() {
    val sampleData = createSampleHomeData()
    AureoleLauncherTheme {
        FolderAppPickerDialog(
            folder = sampleData.folders.first(),
            allApps = sampleData.apps,
            onDismiss = {},
            onSave = {}
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun UpdateAvailableDialogScreenshotTest() {
    AureoleLauncherTheme {
        UpdateAvailableDialog(
            onConfirmUpdate = {},
            onDismiss = {}
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun UpdateDownloadedDialogScreenshotTest() {
    AureoleLauncherTheme {
        UpdateDownloadedDialog(
            onConfirmRestart = {},
            onDismiss = {}
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun ReleaseNotesBottomSheetScreenshotTest() {
    val sampleReleaseNoteVersion = ReleaseNoteVersion(
        versionName = "0.6.13",
        releaseDate = "14/10/2026",
        isCurrent = true,
        sections = listOf(
            ReleaseNoteSection(
                title = "Novidades & Recursos",
                items = listOf(
                    ReleaseNoteItem(
                        title = "Nova Tela de Edição de Container",
                        description = "Interface de personalização em tela cheia com pré-visualização em tempo real e seções dedicadas para Aparência, Comportamento e Componentes.",
                        type = ReleaseNoteType.FEATURE
                    ),
                    ReleaseNoteItem(
                        title = "Modo de Pastas em Grade (Grid Folders)",
                        description = "Suporte para exibir miniaturas de ícones 2x2 no container e abrir o menu expansível de pastas em formato de grade.",
                        type = ReleaseNoteType.FEATURE
                    )
                )
            ),
            ReleaseNoteSection(
                title = "Melhorias de Interface & Experiência",
                items = listOf(
                    ReleaseNoteItem(
                        title = "Melhorias de Performance",
                        description = "Otimizações gerais no carregamento da tela inicial.",
                        type = ReleaseNoteType.IMPROVEMENT
                    )
                )
            )
        )
    )

    AureoleLauncherTheme {
        ReleaseNotesBottomSheet(
            onDismissRequest = {},
            allVersions = listOf(
                sampleReleaseNoteVersion,
                sampleReleaseNoteVersion.copy(versionName = "0.5.13", isCurrent = false),
                sampleReleaseNoteVersion.copy(versionName = "0.3.13", isCurrent = false)
            )
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun CustomizeBottomSheetScreenshotTest() {
    val sampleData = createSampleHomeData()
    AureoleLauncherTheme {
        CustomizeBottomSheet(
            actions = sampleData.actions,
            onDismissRequest = {}
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun EditClockBottomSheetScreenshotTest() {
    val sampleData = createSampleHomeData()
    AureoleLauncherTheme {
        CompositionLocalProvider(LocalHomeUiState provides sampleData.uiState) {
            EditClockBottomSheet(
                uiState = sampleData.uiState,
                actions = sampleData.actions,
                onDismissRequest = {}
            )
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun WidgetPickerBottomSheetScreenshotTest() {
    val sampleData = createSampleHomeData()
    val sampleWidgetGroup = WidgetAppGroup(
        appId = "com.example.calendar",
        appName = "Calendar",
        availableWidgets = listOf(
            WidgetVariant(
                widgetId = "calendar_1",
                title = "Month View",
                minSpanX = 2,
                minSpanY = 2
            ),
            WidgetVariant(
                widgetId = "calendar_2",
                title = "Agenda View",
                minSpanX = 4,
                minSpanY = 2
            )
        )
    )
    val sampleState = WidgetSelectorState(
        isLoading = false,
        searchQuery = "",
        widgetGroups = listOf(sampleWidgetGroup),
        expandedAppIds = setOf("com.example.calendar")
    )
    AureoleLauncherTheme {
        CompositionLocalProvider(LocalHomeUiState provides sampleData.uiState) {
            WidgetSelectorScreen(
                params = WidgetSelectorScreenParams(
                    state = sampleState,
                    onIntent = {},
                    onWidgetSelected = {},
                    onDismiss = {}
                )
            )
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun FolderDialogScreenshotTest() {
    val sampleData = createSampleHomeData()
    AureoleLauncherTheme {
        CompositionLocalProvider(LocalHomeUiState provides sampleData.uiState) {
            FolderDialog(
                folder = sampleData.folders.first(),
                allApps = sampleData.apps,
                onDismiss = {},
                onAppClick = {}
            )
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun EditContainerScreenScreenshotTest() {
    val sampleData = createSampleHomeData()
    val panel = ContainerModel(
        id = "panel_1",
        title = "Favoritos",
        orientation = "Vertical",
        isBackgroundEnabled = true,
        isGridFolderEnabled = true,
        folders = sampleData.folders,
        appPackageNames = sampleData.apps.take(3).map { it.packageName },
        items = listOf(
            ContainerItemEntity(
                id = "item_f1",
                panelId = "panel_1",
                itemType = ContainerItemType.FOLDER,
                folderId = sampleData.folders.first().id,
                orderIndex = 0
            ),
            ContainerItemEntity(
                id = "item_a1",
                panelId = "panel_1",
                itemType = ContainerItemType.APP,
                packageName = sampleData.apps.first().packageName,
                orderIndex = 1
            )
        )
    )
    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides sampleData.uiState,
            LocalHomeActions provides sampleData.actions
        ) {
            EditContainerScreen(
                params = EditContainerScreenParams(
                    panel = panel,
                    allApps = sampleData.apps,
                    onDismiss = {},
                    onSave = {}
                )
            )
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun EditContainerHorizontalScreenScreenshotTest() {
    val sampleData = createSampleHomeData()
    val panel = ContainerModel(
        id = "panel_horizontal_1",
        title = "Favoritos Horizontal",
        orientation = "Horizontal",
        isBackgroundEnabled = true,
        isGridFolderEnabled = true,
        folders = sampleData.folders,
        appPackageNames = sampleData.apps.take(3).map { it.packageName },
        items = listOf(
            ContainerItemEntity(
                id = "item_f1",
                panelId = "panel_horizontal_1",
                itemType = ContainerItemType.FOLDER,
                folderId = sampleData.folders.first().id,
                orderIndex = 0
            ),
            ContainerItemEntity(
                id = "item_a1",
                panelId = "panel_horizontal_1",
                itemType = ContainerItemType.APP,
                packageName = sampleData.apps.first().packageName,
                orderIndex = 1
            ),
            ContainerItemEntity(
                id = "item_a2",
                panelId = "panel_horizontal_1",
                itemType = ContainerItemType.APP,
                packageName = sampleData.apps.getOrNull(1)?.packageName ?: sampleData.apps.first().packageName,
                orderIndex = 2
            )
        )
    )
    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides sampleData.uiState,
            LocalHomeActions provides sampleData.actions
        ) {
            EditContainerScreen(
                params = EditContainerScreenParams(
                    panel = panel,
                    allApps = sampleData.apps,
                    onDismiss = {},
                    onSave = {}
                )
            )
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun EditWidgetGroupScreenScreenshotTest() {
    val sampleData = createSampleHomeData()
    val item = LauncherItemState(
        id = "widget_group_1",
        type = LauncherItemType.WIDGET_LIST,
        col = 0,
        row = 0,
        colSpan = 2,
        rowSpan = 2
    )
    AureoleLauncherTheme {
        CompositionLocalProvider(LocalHomeUiState provides sampleData.uiState) {
            EditWidgetGroupScreen(
                params = EditWidgetGroupParams(
                    item = item,
                    widgets = listOf(1, 2),
                    appWidgetHost = null,
                    isIndicatorEnabled = true
                ),
                actions = EditWidgetGroupActions(
                    onToggleIndicator = {},
                    onRemoveWidget = {},
                    onAddWidgetClick = {},
                    onDismissRequest = {}
                )
            )
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun ContainerFolderBottomSheetScreenshotTest() {
    val params = ContainerFolderBottomSheetParams(
        onEditShortcuts = {},
        onRename = {},
        onIcon = {},
        onRemoveFolder = {},
        onOpenContainerSettings = {}
    )
    AureoleLauncherTheme {
        ContainerFolderBottomSheet(
            params = params,
            onDismiss = {}
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun WidgetStackBottomSheetScreenshotTest() {
    val params = WidgetStackBottomSheetParams(
        stackId = "widget_stack_1",
        widgetId = 1,
        onEditStack = {},
        onToggleDots = {},
        onRemoveStack = {}
    )
    AureoleLauncherTheme {
        WidgetStackBottomSheet(
            params = params,
            onDismiss = {}
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun FolderFormDialogScreenshotTest() {
    val sampleFolder = AppFolder(
        id = "folder_1",
        name = "Work Apps",
        icon = null,
        appPackageNames = listOf("com.example.work")
    )
    AureoleLauncherTheme {
        FolderFormDialog(
            folder = sampleFolder,
            onDismiss = {},
            onSave = { _, _ -> },
            onDelete = {}
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun StandardUpdateAlertDialogScreenshotTest() {
    val params = UpdateDialogContentParams(
        title = "Nova atualização disponível",
        body = "Uma nova versão do Aureole Launcher está disponível com melhorias.",
        confirmLabel = "Atualizar",
        dismissLabel = "Cancelar",
        onConfirm = {},
        onDismiss = {}
    )
    AureoleLauncherTheme {
        StandardUpdateAlertDialog(params = params)
    }
}
