package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.data.model.ShortcutType
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Check
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.ShortcutSelectionSheetParams
import dev.mnascimentos.aureole.feature.home.model.MainUiState

@Composable
internal fun ShortcutSelectionSheetContent(
    params: ShortcutSelectionSheetParams,
    onApplyShortcut: (ShortcutAction) -> Unit,
    onShowAppPicker: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        if (params.isShowingAppPicker) {
            AppSelectionList(
                apps = params.uiState.apps,
                currentAction = params.currentAction,
                onApplyShortcut = onApplyShortcut
            )
        } else {
            DefaultShortcutSelectionList(
                params = params,
                onApplyShortcut = onApplyShortcut,
                onShowAppPicker = onShowAppPicker
            )
        }
        Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))
    }
}

@Composable
private fun AppSelectionList(
    apps: List<AppInfo>,
    currentAction: ShortcutAction,
    onApplyShortcut: (ShortcutAction) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        apps.sortedBy { it.label.lowercase() }.forEach { app ->
            val isAppSelected = currentAction.type == ShortcutType.OPEN_APP &&
                currentAction.target == app.packageName
            AppOptionRow(
                app = app,
                isSelected = isAppSelected,
                onClick = { onApplyShortcut(ShortcutAction.openApp(app.packageName)) }
            )
        }
    }
}

@Composable
private fun DefaultShortcutSelectionList(
    params: ShortcutSelectionSheetParams,
    onApplyShortcut: (ShortcutAction) -> Unit,
    onShowAppPicker: () -> Unit
) {
    val currentAction = params.currentAction
    val isAllAppsSelected = currentAction.type == ShortcutAction.AllApps.type

    val isOpenAppSelected = currentAction.type == ShortcutType.OPEN_APP
    val selectedApp = if (isOpenAppSelected && currentAction.target.isNotEmpty()) {
        params.uiState.apps.find { it.packageName == currentAction.target }
    } else {
        null
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        ShortcutOptionRow(
            title = ShortcutAction.AllApps.displayName,
            isSelected = isAllAppsSelected,
            onClick = { onApplyShortcut(ShortcutAction.AllApps) }
        )

        ShortcutOptionRow(
            title = stringResource(R.string.home_quickbutton_open_app_title),
            isSelected = isOpenAppSelected,
            endContent = if (selectedApp != null) {
                { SelectedAppEndContent(selectedApp) }
            } else {
                null
            },
            onClick = onShowAppPicker
        )

        params.defaultShortcutOptions.filterNot { it.type == ShortcutType.ALL_APPS }.forEach { action ->
            val isSelected = currentAction.type == action.type &&
                currentAction.target == action.target
            ShortcutOptionRow(
                title = action.displayName,
                isSelected = isSelected,
                onClick = { onApplyShortcut(action) }
            )
        }
    }
}

@Composable
private fun SelectedAppEndContent(app: AppInfo) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AureoleText(
            text = app.label,
            color = AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall)
        )
        Spacer(modifier = Modifier.width(AureoleDS.dimens.xSmall))
        Image(
            bitmap = app.getIconBitmap(),
            contentDescription = null,
            modifier = Modifier.size(AureoleDS.dimens.iconMedium)
        )
    }
}

@Composable
internal fun ShortcutOptionRow(
    title: String,
    isSelected: Boolean,
    endContent: (@Composable () -> Unit)? = null,
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
        AureoleText(
            text = title,
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.weight(1f)
        )
        if (endContent != null) {
            endContent()
            Spacer(modifier = Modifier.width(AureoleDS.dimens.small))
        }
        if (isSelected) {
            AureoleDS.icons.Check(Modifier.size(AureoleDS.dimens.iconMedium))
        } else {
            Spacer(modifier = Modifier.size(AureoleDS.dimens.iconMedium))
        }
    }
}

@Composable
internal fun AppOptionRow(
    app: AppInfo,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .clickable { onClick() }
            .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            bitmap = app.getIconBitmap(),
            contentDescription = null,
            modifier = Modifier.size(AureoleDS.dimens.xLarge)
        )
        Spacer(modifier = Modifier.width(AureoleDS.dimens.medium))
        AureoleText(
            text = app.label,
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.weight(1f)
        )
        if (isSelected) {
            AureoleDS.icons.Check(Modifier.size(AureoleDS.dimens.iconMedium))
        } else {
            Spacer(modifier = Modifier.size(AureoleDS.dimens.iconMedium))
        }
    }
}

@AureolePreview
@Composable
private fun ShortcutSelectionSheetContentPreview() {
    AureoleLauncherTheme {
        ShortcutSelectionSheetContent(
            params = ShortcutSelectionSheetParams(
                isShowingAppPicker = false,
                currentAction = ShortcutAction.AllApps,
                defaultShortcutOptions = listOf(ShortcutAction.AllApps),
                uiState = MainUiState()
            ),
            onApplyShortcut = {},
            onShowAppPicker = {}
        )
    }
}
