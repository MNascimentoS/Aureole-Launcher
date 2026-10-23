package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.QuickButtonIcon
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.ColorFill
import dev.mnascimentos.aureole.core.designsystem.icons.Smile
import dev.mnascimentos.aureole.core.designsystem.icons.SmileColor
import dev.mnascimentos.aureole.core.designsystem.icons.Stroke
import dev.mnascimentos.aureole.core.designsystem.icons.StrokeColor
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.QuickButtonColorPickerTarget
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions

@Composable
internal fun QuickButtonAppearanceSheetContent(
    activeModel: QuickButtonModel,
    activeAppInfo: AppInfo?,
    actions: HomeScreenActions,
    onPickColor: (QuickButtonColorPickerTarget) -> Unit,
    onOpenIconPicker: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        AppearanceColorRow(
            title = "Background color",
            icon = { m -> AureoleDS.icons.ColorFill(m) },
            colorValue = Color(activeModel.appearance.backgroundColor),
            onClick = { onPickColor(QuickButtonColorPickerTarget.BACKGROUND) }
        )
        AppearanceToggleRow(
            title = "Stroke",
            icon = { m -> AureoleDS.icons.Stroke(m) },
            isChecked = activeModel.appearance.strokeEnabled,
            onToggle = {
                val newStroke = !activeModel.appearance.strokeEnabled
                actions.onUpdateQuickButtonAppearance(activeModel.appearance.copy(strokeEnabled = newStroke))
            }
        )
        if (activeModel.appearance.strokeEnabled) {
            AppearanceColorRow(
                title = "Stroke color",
                icon = { m -> AureoleDS.icons.StrokeColor(m) },
                colorValue = Color(activeModel.appearance.strokeColor),
                onClick = { onPickColor(QuickButtonColorPickerTarget.STROKE) }
            )
        }
        AppearanceIconRow(
            title = "Icon",
            icon = { m -> AureoleDS.icons.Smile(m) },
            selectedIcon = activeModel.appearance.icon,
            appInfo = activeAppInfo,
            onClick = onOpenIconPicker
        )
        AppearanceColorRow(
            title = "Icon color",
            icon = { m -> AureoleDS.icons.SmileColor(m) },
            colorValue = Color(activeModel.appearance.iconColor),
            onClick = { onPickColor(QuickButtonColorPickerTarget.ICON) }
        )
        Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))
    }
}

@Composable
internal fun AppearanceColorRow(
    title: String,
    icon: @Composable (Modifier) -> Unit,
    colorValue: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .clickable { onClick() }
            .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon(Modifier.size(AureoleDS.dimens.iconMedium))
        Spacer(modifier = Modifier.width(AureoleDS.dimens.medium))
        AureoleText(
            text = title,
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium, fontWeight = FontWeight.Medium),
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .size(AureoleDS.dimens.iconLarge)
                .background(colorValue, CircleShape)
                .border(AureoleDS.dimens.minimal, AureoleDS.colors.surfaceVariant, CircleShape)
        )
    }
}

@Composable
internal fun AppearanceToggleRow(
    title: String,
    icon: @Composable (Modifier) -> Unit,
    isChecked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .clickable { onToggle() }
            .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon(Modifier.size(AureoleDS.dimens.iconMedium))
        Spacer(modifier = Modifier.width(AureoleDS.dimens.medium))
        AureoleText(
            text = title,
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium, fontWeight = FontWeight.Medium),
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .size(AureoleDS.dimens.iconLarge)
                .border(
                    width = AureoleDS.dimens.minimal,
                    color = if (isChecked) AureoleDS.colors.onSurfaceHigh else AureoleDS.colors.onSurfaceLow,
                    shape = RoundedCornerShape(AureoleDS.dimens.radiusMinimal)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isChecked) {
                Box(
                    modifier = Modifier.size(
                        AureoleDS.dimens.iconSmallCompact
                    ).background(AureoleDS.colors.onSurfaceHigh, RoundedCornerShape(AureoleDS.dimens.minimal))
                )
            }
        }
    }
}

@Composable
internal fun AppearanceIconRow(
    title: String,
    icon: @Composable (Modifier) -> Unit,
    selectedIcon: QuickButtonIcon,
    appInfo: AppInfo? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .clickable { onClick() }
            .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon(Modifier.size(AureoleDS.dimens.iconMedium))
        Spacer(modifier = Modifier.width(AureoleDS.dimens.medium))
        AureoleText(
            text = title,
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium, fontWeight = FontWeight.Medium),
            modifier = Modifier.weight(1f)
        )
        if (appInfo != null) {
            Image(
                bitmap = appInfo.getIconBitmap(),
                contentDescription = null,
                modifier = Modifier.size(AureoleDS.dimens.iconLarge)
            )
        } else {
            Icon(
                painter = painterResource(id = selectedIcon.iconRes),
                contentDescription = null,
                tint = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.size(AureoleDS.dimens.iconLarge)
            )
        }
    }
}

@Composable
internal fun QuickButtonIconPickerDialog(
    selectedIcon: QuickButtonIcon,
    onIconSelected: (QuickButtonIcon) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            AureoleText(
                text = "Select Icon",
                format = AureoleTextFormatParams(style = AureoleDS.typography.titleMedium, fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                QuickButtonIconFlowRow(selectedIcon = selectedIcon, onIconSelected = onIconSelected)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { AureoleText("Cancel") }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuickButtonIconFlowRow(
    selectedIcon: QuickButtonIcon,
    onIconSelected: (QuickButtonIcon) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
    ) {
        QuickButtonIcon.entries.forEach { iconItem ->
            val isSelected = iconItem == selectedIcon
            val borderCol = if (isSelected) AureoleDS.colors.onSurfaceHigh else AureoleDS.colors.outline
            Box(
                modifier = Modifier
                    .size(AureoleDS.dimens.xxLarge)
                    .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
                    .background(if (isSelected) AureoleDS.colors.surfaceVariant else Color.Transparent)
                    .border(
                        width = if (isSelected) AureoleDS.dimens.minimal else AureoleDS.dimens.borderMinimal,
                        color = borderCol,
                        shape = RoundedCornerShape(AureoleDS.dimens.radiusSmall)
                    )
                    .clickable { onIconSelected(iconItem) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = selectedIcon.iconRes),
                    contentDescription = iconItem.name,
                    tint = AureoleDS.colors.onSurfaceHigh,
                    modifier = Modifier.size(AureoleDS.dimens.iconLarge)
                )
            }
        }
    }
}

@AureolePreview
@Composable
private fun QuickButtonAppearanceSheetContentPreview() {
    val dummyActions = HomeScreenActions(
        onWidgetRowHeightChanged = {},
        onAppClick = {},
        onExpandNotificationShade = {},
        onFolderIntent = {},
        onSetAddAppToFolderDialogVisible = {},
        onSetRenameFolderDialogVisible = {},
        onSearchQueryChanged = {},
        onSettingsClick = {},
        onAllAppsDrawerClose = {},
        onAllAppsDrawerOpen = {},
    )
    AureoleLauncherTheme {
        QuickButtonAppearanceSheetContent(
            activeModel = QuickButtonModel(),
            activeAppInfo = null,
            actions = dummyActions,
            onPickColor = {},
            onOpenIconPicker = {},
        )
    }
}

@AureolePreview
@Composable
private fun AppearanceRowsPreview() {
    AureoleLauncherTheme {
        Column {
            AppearanceColorRow(
                title = "Preview Color Row",
                icon = { m -> AureoleDS.icons.ColorFill(m) },
                colorValue = Color.Red,
                onClick = {},
            )
            AppearanceIconRow(
                title = "Preview Icon Row",
                icon = { m -> AureoleDS.icons.Smile(m) },
                selectedIcon = QuickButtonIcon.STAR,
                appInfo = null,
                onClick = {},
            )
        }
    }
}

@AureolePreview
@Composable
private fun AppearanceToggleRowPreview() {
    AureoleLauncherTheme {
        Column {
            AppearanceToggleRow(
                title = "Preview Toggle Row Checked",
                icon = { m -> AureoleDS.icons.Stroke(m) },
                isChecked = true,
                onToggle = {},
            )
            AppearanceToggleRow(
                title = "Preview Toggle Row Unchecked",
                icon = { m -> AureoleDS.icons.Stroke(m) },
                isChecked = false,
                onToggle = {},
            )
        }
    }
}

@AureolePreview
@Composable
private fun QuickButtonIconPickerDialogPreview() {
    AureoleLauncherTheme {
        QuickButtonIconPickerDialog(
            selectedIcon = QuickButtonIcon.STAR,
            onIconSelected = {},
            onDismiss = {},
        )
    }
}
