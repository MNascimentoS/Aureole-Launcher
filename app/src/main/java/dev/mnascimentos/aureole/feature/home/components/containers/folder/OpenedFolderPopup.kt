package dev.mnascimentos.aureole.feature.home.components.containers.folder

import android.content.ComponentName
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupBox
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderPopupActions
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderPopupMetrics
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.GridFolderPopupParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.OpenedFolderPopupConfig
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/**
 * The opened folder. Grid mode ([OpenedFolderPopupConfig.isGridFolderEnabled] or the folder's own flag) is a
 * centered popup with only the apps; list mode is anchored near the folder by the caller. Adding apps and
 * editing the folder live in the folder menu (long press on the folder), not here.
 */
@Composable
fun OpenedFolderPopup(
    config: OpenedFolderPopupConfig,
    modifier: Modifier = Modifier
) {
    val folder = config.folder
    val isGridMode = config.isGridFolderEnabled || folder.displayAsGrid

    val appsInFolder = remember(folder, config.allApps) {
        folder.appPackageNames.mapNotNull { pkgName ->
            config.allApps.find { it.packageName == pkgName }
        }
    }

    if (isGridMode) {
        GridFolderPopup(
            params = GridFolderPopupParams(
                folder = folder,
                appsInFolder = appsInFolder,
                actions = config.actions,
                hazeState = config.hazeState,
                isHazeEnabled = true
            ),
            modifier = modifier
        )
    } else {
        ListFolderPopupBox(config = config, appsInFolder = appsInFolder, modifier = modifier)
    }
}

@Composable
private fun ListFolderPopupBox(
    config: OpenedFolderPopupConfig,
    appsInFolder: List<AppInfo>,
    modifier: Modifier = Modifier
) {
    val uiState = LocalHomeUiState.current
    val homeActions = LocalHomeActions.current

    AureolePopupBox(
        hazeState = null,
        modifier = modifier
            .widthIn(min = FolderPopupMetrics.WidthMin, max = FolderPopupMetrics.WidthMax)
            .heightIn(max = FolderPopupMetrics.HeightMax)
    ) {
        if (config.folder.name.isNotBlank()) {
            Text(
                text = config.folder.name,
                style = AureoleDS.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = AureoleDS.colors.onSurfaceHigh,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AureoleDS.dimens.small, bottom = AureoleDS.dimens.xSmall)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AureoleDS.dimens.small, vertical = AureoleDS.dimens.xSmall)
        ) {
            OpenedFolderAppList(
                appsInFolder = appsInFolder,
                isLeftHandedMode = uiState.isLeftHandedMode,
                onAppClick = config.actions.onAppClick,
                onAppLongClick = { app -> homeActions.onOpenFolderAppBottomSheet(app, config.folder) }
            )
        }
    }
}

@AureolePreview
@Composable
private fun OpenedFolderPopupPreview() {
    val mockApp = AppInfo(
        label = "Chat",
        packageName = "com.example.chat",
        componentName = ComponentName("com.example.chat", "MainActivity"),
        icon = 0xFF1B65C0.toInt().toDrawable()
    )
    val mockFolder = AppFolder(
        id = "1",
        name = "Social",
        appPackageNames = listOf(mockApp.packageName)
    )
    val actions = FolderPopupActions(onDismiss = {}, onAppClick = {})

    AureoleLauncherTheme {
        CompositionLocalProvider(LocalHomeUiState provides MainUiState()) {
            OpenedFolderPopup(
                config = OpenedFolderPopupConfig(
                    folder = mockFolder,
                    allApps = listOf(mockApp),
                    actions = actions
                )
            )
        }
    }
}
