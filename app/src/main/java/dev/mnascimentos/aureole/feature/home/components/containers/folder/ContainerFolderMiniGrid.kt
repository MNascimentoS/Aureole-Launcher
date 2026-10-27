package dev.mnascimentos.aureole.feature.home.components.containers.folder

import android.content.ComponentName
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.composable.dialogs.FolderIconRegistry
import dev.mnascimentos.aureole.composable.dialogs.model.FolderButtonMetrics
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val MAX_PREVIEW_APPS = 9
private const val GRID_ROWS = 3
private const val GRID_COLS = 3

@Composable
fun FolderMiniGridPreview(
    folder: AppFolder,
    allApps: List<AppInfo>,
    modifier: Modifier = Modifier
) {
    val folderApps = remember(folder.appPackageNames, allApps) {
        folder.appPackageNames.take(MAX_PREVIEW_APPS).mapNotNull { pkg ->
            allApps.find { it.packageName == pkg }
        }
    }

    Box(
        modifier = modifier
            .size(FolderButtonMetrics.SmallSize)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.85f))
            .padding(AureoleDS.dimens.borderMax),
        contentAlignment = Alignment.Center
    ) {
        if (folderApps.isEmpty()) {
            EmptyFolderMiniGridPreview(folder = folder)
        } else {
            PopulatedFolderMiniGrid(folderApps = folderApps)
        }
    }
}

@Composable
private fun EmptyFolderMiniGridPreview(folder: AppFolder) {
    if (folder.icon != null) {
        FolderIconRegistry.RenderIcon(
            name = folder.icon,
            modifier = Modifier.size(AureoleDS.dimens.iconLarge),
            tint = AureoleDS.colors.onSurfaceMedium
        )
    } else {
        AureoleText(
            text = folder.name.take(1).uppercase(),
            color = AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(style = AureoleDS.typography.titleMedium, fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
private fun PopulatedFolderMiniGrid(folderApps: List<AppInfo>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.minimal),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (row in 0 until GRID_ROWS) {
            FolderMiniGridRow(row = row, folderApps = folderApps)
        }
    }
}

@Composable
private fun FolderMiniGridRow(row: Int, folderApps: List<AppInfo>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.minimal),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (col in 0 until GRID_COLS) {
            val index = row * GRID_COLS + col
            FolderMiniGridCell(app = folderApps.getOrNull(index))
        }
    }
}

@Composable
private fun FolderMiniGridCell(app: AppInfo?) {
    if (app == null) {
        Spacer(modifier = Modifier.size(AureoleDS.dimens.xSmall))
        return
    }

    val bitmap = remember(app.packageName) { app.getIconBitmap() }
    Image(
        bitmap = bitmap,
        contentDescription = app.label,
        modifier = Modifier
            .size(AureoleDS.dimens.xSmall)
            .clip(CircleShape)
    )
}

@AureolePreview
@Composable
private fun FolderMiniGridPreviewTest() {
    val sampleApps = listOf(
        AppInfo(
            label = "App 1",
            packageName = "com.example.app1",
            componentName = ComponentName("com.example.app1", "MainActivity"),
            icon = 0xFF1B65C0.toInt().toDrawable()
        ),
        AppInfo(
            label = "App 2",
            packageName = "com.example.app2",
            componentName = ComponentName("com.example.app2", "MainActivity"),
            icon = 0xFF2E7D32.toInt().toDrawable()
        )
    )
    val populatedFolder = AppFolder(
        id = "folder_1",
        name = "Social",
        appPackageNames = sampleApps.map { it.packageName }
    )
    val emptyFolder = AppFolder(
        id = "folder_2",
        name = "Work",
        appPackageNames = emptyList()
    )

    AureoleLauncherTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.medium)
        ) {
            FolderMiniGridPreview(
                folder = populatedFolder,
                allApps = sampleApps
            )
            FolderMiniGridPreview(
                folder = emptyFolder,
                allApps = sampleApps
            )
        }
    }
}
