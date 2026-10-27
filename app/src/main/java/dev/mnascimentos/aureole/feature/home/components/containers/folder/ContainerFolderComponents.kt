package dev.mnascimentos.aureole.feature.home.components.containers.folder

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import dev.mnascimentos.aureole.composable.dialogs.FolderIconRegistry
import dev.mnascimentos.aureole.composable.dialogs.model.FolderButtonMetrics
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Add
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerAppItemParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerFolderButtonParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerFolderConfig
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerFolderItemParams
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState

private const val FOLDER_BG_INACTIVE_ALPHA = 0.85f

@Composable
fun ContainerAddFolderButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isBackgroundEnabled: Boolean = true
) {
    val buttonSize = if (isBackgroundEnabled) {
        FolderButtonMetrics.SmallSize
    } else {
        FolderButtonMetrics.MediumSize
    }
    val iconSize = if (isBackgroundEnabled) AureoleDS.dimens.large else AureoleDS.dimens.iconXLarge
    val cornerRadius = if (isBackgroundEnabled) AureoleDS.dimens.radiusMedium else AureoleDS.dimens.iconMedium

    Box(
        modifier = modifier
            .padding(vertical = AureoleDS.dimens.xxSmall)
            .size(buttonSize)
            .clip(RoundedCornerShape(cornerRadius))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = FOLDER_BG_INACTIVE_ALPHA))
            .clickable(
                onClick = onClick,
                role = Role.Button
            )
            .semantics(mergeDescendants = true) {},
        contentAlignment = Alignment.Center
    ) {
        AureoleDS.icons.Add(
            tint = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
fun ContainerFolderItem(
    params: ContainerFolderItemParams,
    modifier: Modifier = Modifier,
) {
    val folder = params.folder
    val isOpened = params.isOpened
    val config = params.config
    val onFolderClick = params.onFolderClick
    val onLongClick = params.onLongClick

    val showFolderLabels = config.showFolderLabels
    val isGridFolderEnabled = config.isGridFolderEnabled
    val isBackgroundEnabled = config.isBackgroundEnabled
    var itemYInWindow by remember { mutableFloatStateOf(0f) }

    val containerColor by animateColorAsState(
        targetValue = if (isOpened) {
            AureoleDS.colors.onSurfaceHigh
        } else {
            AureoleDS.colors.surfaceVariant.copy(alpha = FOLDER_BG_INACTIVE_ALPHA)
        },
        label = "folder_bg"
    )
    val textColor = if (isOpened) {
        AureoleDS.colors.surface
    } else {
        AureoleDS.colors.onSurfaceMedium
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(vertical = AureoleDS.dimens.xxSmall)
            .widthIn(max = FolderButtonMetrics.ContainerMaxWidth),
    ) {
        ContainerFolderButton(
            params = ContainerFolderButtonParams(
                folder = folder,
                isGridFolderEnabled = isGridFolderEnabled,
                containerColor = containerColor,
                textColor = textColor,
                isBackgroundEnabled = isBackgroundEnabled,
                onPositionedY = { y -> itemYInWindow = y },
                onClick = { onFolderClick(folder, itemYInWindow) },
                onLongClick = onLongClick
            )
        )

        if (showFolderLabels) {
            ContainerFolderLabel(name = folder.name)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContainerFolderButton(
    params: ContainerFolderButtonParams,
    modifier: Modifier = Modifier,
) {
    val uiState = LocalHomeUiState.current
    val showGridPreview = params.isGridFolderEnabled || params.folder.displayAsGrid
    val isBackgroundEnabled = params.isBackgroundEnabled
    val onPositionedY = params.onPositionedY
    val onClick = params.onClick
    val onLongClick = params.onLongClick

    val buttonSize = if (isBackgroundEnabled) {
        FolderButtonMetrics.SmallSize
    } else {
        FolderButtonMetrics.MediumSize
    }
    val iconSize = if (isBackgroundEnabled) AureoleDS.dimens.large else AureoleDS.dimens.iconXLarge
    val cornerRadius = if (isBackgroundEnabled) AureoleDS.dimens.radiusMedium else AureoleDS.dimens.iconMedium

    val clickModifier = if (onLongClick != null) {
        Modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick,
            role = Role.Button
        ).semantics(mergeDescendants = true) {}
    } else {
        Modifier.clickable(
            onClick = onClick,
            role = Role.Button
        ).semantics(mergeDescendants = true) {}
    }

    Box(
        modifier = modifier
            .size(buttonSize)
            .clip(RoundedCornerShape(cornerRadius))
            .background(params.containerColor)
            .onGloballyPositioned { coordinates ->
                onPositionedY(coordinates.positionInWindow().y)
            }
            .then(clickModifier),
        contentAlignment = Alignment.Center
    ) {
        if (showGridPreview) {
            FolderMiniGridPreview(
                folder = params.folder,
                allApps = uiState.apps,
                modifier = Modifier.size(buttonSize)
            )
        } else {
            FolderIconOrTextContent(
                folder = params.folder,
                iconSize = iconSize,
                textColor = params.textColor
            )
        }
    }
}

@Composable
private fun FolderIconOrTextContent(
    folder: AppFolder,
    iconSize: Dp,
    textColor: Color
) {
    if (folder.icon != null) {
        FolderIconRegistry.RenderIcon(
            name = folder.icon,
            modifier = Modifier.size(iconSize),
            tint = textColor
        )
    } else {
        AureoleText(
            text = folder.name.take(1).uppercase(),
            color = textColor,
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun ContainerFolderLabel(
    name: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(top = AureoleDS.dimens.minimal)
    ) {
        AureoleText(
            text = name,
            color = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.widthIn(max = FolderButtonMetrics.LabelMaxWidth),
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContainerAppItem(
    params: ContainerAppItemParams,
    modifier: Modifier = Modifier,
) {
    val app = params.app
    val showLabels = params.showLabels
    val onAppClick = params.onAppClick
    val isBackgroundEnabled = params.isBackgroundEnabled
    val onLongClick = params.onLongClick

    val uiState = LocalHomeUiState.current
    val bitmap = app.getDisplayIconBitmap(
        isThemed = uiState.isThemedAppIconsEnabled,
        tintColor = AureoleDS.colors.onSurfaceMedium
    )
    val buttonSize = if (isBackgroundEnabled) {
        FolderButtonMetrics.SmallSize
    } else {
        FolderButtonMetrics.MediumSize
    }
    val cornerRadius = if (isBackgroundEnabled) AureoleDS.dimens.radiusMedium else AureoleDS.dimens.iconMedium
    val imgPadding = if (isBackgroundEnabled) AureoleDS.dimens.borderMax else AureoleDS.dimens.minimal

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(vertical = AureoleDS.dimens.xxSmall)
            .widthIn(max = FolderButtonMetrics.ContainerMaxWidth),
    ) {
        Box(
            modifier = Modifier
                .size(buttonSize)
                .clip(RoundedCornerShape(cornerRadius))
                .background(AureoleDS.colors.surfaceVariant.copy(alpha = FOLDER_BG_INACTIVE_ALPHA))
                .combinedClickable(
                    onClick = { onAppClick(app) },
                    onLongClick = onLongClick,
                    role = Role.Button
                )
                .semantics(mergeDescendants = true) {}
                .padding(imgPadding),
            contentAlignment = Alignment.Center
        ) {
            Image(
                bitmap = bitmap,
                contentDescription = app.label,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (showLabels) {
            ContainerFolderLabel(name = app.label)
        }
    }
}

@AureolePreview
@Composable
private fun ContainerFolderItemPreview() {
    val sampleFolder = AppFolder(id = "f1", name = "Social")
    AureoleLauncherTheme {
        ContainerFolderItem(
            params = ContainerFolderItemParams(
                folder = sampleFolder,
                isOpened = false,
                config = ContainerFolderConfig(showFolderLabels = true, isGridFolderEnabled = false),
                onFolderClick = { _, _ -> }
            )
        )
    }
}
