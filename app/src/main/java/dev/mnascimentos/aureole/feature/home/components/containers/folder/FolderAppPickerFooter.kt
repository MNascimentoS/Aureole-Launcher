package dev.mnascimentos.aureole.feature.home.components.containers.folder

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.search.AureoleSearchField
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Close
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderAppPickerFooterActions
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderAppPickerFooterParams

/**
 * Fixed footer of the folder picker, kept above the keyboard with `imePadding()`. From top to bottom:
 * the tray of marked apps (only while searching or with the keyboard open), the search field and Cancel/Save.
 * Save sits on the thumb side.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun FolderAppPickerFooter(
    params: FolderAppPickerFooterParams,
    actions: FolderAppPickerFooterActions,
    modifier: Modifier = Modifier
) {
    val showTray = params.trayApps.isNotEmpty() && (params.searchQuery.isNotBlank() || WindowInsets.isImeVisible)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        if (showTray) {
            FolderAppPickerTray(
                apps = params.trayApps,
                isLeftHandedMode = params.isLeftHandedMode,
                onRemove = actions.onRemove
            )
        }
        AureoleSearchField(
            query = params.searchQuery,
            onQueryChange = actions.onQueryChange,
            placeholder = stringResource(R.string.core_common_search_app_placeholder),
            onSearch = actions.onSearch
        )
        FolderAppPickerActions(params = params, onDismiss = actions.onDismiss, onSave = actions.onSave)
    }
}

@Composable
private fun FolderAppPickerActions(
    params: FolderAppPickerFooterParams,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cancel = @Composable {
        OutlinedButton(onClick = onDismiss) { AureoleText(stringResource(R.string.core_common_cancel)) }
    }
    val save = @Composable {
        Button(onClick = onSave) {
            AureoleText(
                pluralStringResource(R.plurals.home_folder_picker_save_count, params.saveCount, params.saveCount)
            )
        }
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            AureoleDS.dimens.xSmall,
            if (params.isLeftHandedMode) Alignment.Start else Alignment.End
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (params.isLeftHandedMode) {
            save()
            cancel()
        } else {
            cancel()
            save()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FolderAppPickerTray(
    apps: List<AppInfo>,
    isLeftHandedMode: Boolean,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Left-handed: the row runs right to left, so the chips start at the thumb side (the left edge).
    val direction = if (isLeftHandedMode) LayoutDirection.Rtl else LayoutDirection.Ltr
    Column(modifier = modifier.fillMaxWidth()) {
        AureoleText(
            text = stringResource(R.string.home_folder_picker_selected_label),
            color = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.padding(bottom = AureoleDS.dimens.xxSmall),
            format = AureoleTextFormatParams(style = AureoleDS.typography.labelSmall)
        )
        CompositionLocalProvider(LocalLayoutDirection provides direction) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
            ) {
                items(apps, key = { it.packageName }) { app ->
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        FolderAppPickerChip(
                            app = app,
                            onRemove = { onRemove(app.packageName) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FolderAppPickerChip(
    app: AppInfo,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconBitmap = remember(app.packageName) { app.getIconBitmap() }
    val removeDescription = stringResource(R.string.home_folder_picker_remove_chip, app.label)
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusFull))
            .background(AureoleDS.colors.surfaceVariant)
            .padding(start = AureoleDS.dimens.xSmall, end = AureoleDS.dimens.xxSmall)
            .padding(vertical = AureoleDS.dimens.xxSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            bitmap = iconBitmap,
            contentDescription = null,
            modifier = Modifier
                .size(AureoleDS.dimens.iconMedium)
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusMinimal))
        )
        AureoleText(
            text = app.label,
            color = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier
                .padding(horizontal = AureoleDS.dimens.xSmall)
                .widthIn(max = AureoleDS.dimens.sizeExtraLarge),
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        )
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .size(AureoleDS.dimens.large)
                .semantics { contentDescription = removeDescription }
        ) {
            AureoleDS.icons.Close(
                tint = AureoleDS.colors.onSurfaceMedium,
                modifier = Modifier.size(AureoleDS.dimens.iconSmall)
            )
        }
    }
}

@AureolePreview
@Composable
private fun FolderAppPickerFooterPreview() {
    AureoleLauncherTheme {
        FolderAppPickerFooter(
            params = FolderAppPickerFooterParams(trayApps = emptyList(), searchQuery = "cam", saveCount = 2),
            actions = FolderAppPickerFooterActions(
                onQueryChange = {},
                onSearch = {},
                onRemove = {},
                onDismiss = {},
                onSave = {}
            )
        )
    }
}
