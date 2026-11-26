package dev.mnascimentos.aureole.composable.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.color.AureoleColorField
import dev.mnascimentos.aureole.composable.color.model.ColorFieldParams
import dev.mnascimentos.aureole.composable.controls.AureoleSegmentedControl
import dev.mnascimentos.aureole.composable.dialogs.model.FolderFormBodyActions
import dev.mnascimentos.aureole.composable.dialogs.model.FolderFormBodyState
import dev.mnascimentos.aureole.composable.dialogs.model.FolderFormDraft
import dev.mnascimentos.aureole.composable.dialogs.model.effectiveEmoji
import dev.mnascimentos.aureole.composable.dialogs.model.effectiveIcon
import dev.mnascimentos.aureole.composable.dialogs.model.folderEmojiChoices
import dev.mnascimentos.aureole.composable.dialogs.model.folderIconSuggestions
import dev.mnascimentos.aureole.composable.dialogs.model.folderIconTypeAt
import dev.mnascimentos.aureole.composable.dialogs.model.folderIconTypeIndex
import dev.mnascimentos.aureole.composable.dialogs.model.isFolderIconMarked
import dev.mnascimentos.aureole.composable.icons.AureoleIconCatalogGrid
import dev.mnascimentos.aureole.composable.menu.AureoleMenuEntries
import dev.mnascimentos.aureole.composable.menu.AureoleMenuRow
import dev.mnascimentos.aureole.composable.menu.AureoleMenuSectionLabel
import dev.mnascimentos.aureole.composable.menu.model.MenuEntry
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.core.data.model.FolderIconType
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.AllApps
import dev.mnascimentos.aureole.core.designsystem.icons.AureoleIconCatalog
import dev.mnascimentos.aureole.core.designsystem.icons.ChevronLeft
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Body of the folder form, in a single scroll (pasta-form RF-01, RF-09, RF-10, RF-06): what the folder shows
 * (Icon | Letter | Emoji) with its choices, the folder color and, when editing, "Delete folder" at the end.
 */
@Composable
internal fun FolderFormBody(state: FolderFormBodyState, actions: FolderFormBodyActions) {
    val draft = state.draft
    val update = actions.onDraftChange
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = AureoleDS.dimens.xSmall),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        FolderIconTypeControl(type = draft.iconType, onSelect = { update(draft.copy(iconType = it)) })
        when (draft.iconType) {
            FolderIconType.ICON -> FolderIconSuggestionsSection(
                draft = draft,
                onPick = { update(draft.copy(icon = it)) },
                onOpenAllIcons = actions.onOpenAllIcons
            )
            FolderIconType.LETTER -> FolderLetterNote()
            FolderIconType.EMOJI -> FolderEmojiSection(
                draft = draft,
                onPick = { update(draft.copy(emoji = it)) }
            )
        }
        AureoleColorField(
            params = ColorFieldParams(
                title = stringResource(R.string.home_folder_color),
                selected = draft.color,
                themeColor = AureoleDS.colors.surfaceVariant,
                role = ComponentColorRole.CARD,
                onSelect = { update(draft.copy(color = it)) }
            )
        )
        actions.onDelete?.takeIf { state.showDelete }?.let { onDelete ->
            AureoleMenuEntries(
                entries = listOf(
                    MenuEntry.Divider,
                    MenuEntry.Row(
                        MenuRowParams(
                            title = stringResource(R.string.home_folder_form_delete),
                            onClick = onDelete,
                            icon = { m, tint -> AureoleDS.icons.Delete(m, tint = tint) },
                            isDestructive = true
                        )
                    )
                )
            )
        }
    }
}

@Composable
private fun FolderIconTypeControl(type: FolderIconType, onSelect: (FolderIconType) -> Unit) {
    val description = stringResource(R.string.home_folder_type_label)
    AureoleSegmentedControl(
        options = listOf(
            stringResource(R.string.home_folder_type_icon),
            stringResource(R.string.home_folder_type_letter),
            stringResource(R.string.home_folder_type_emoji)
        ),
        selectedIndex = folderIconTypeIndex(type),
        onSelect = { onSelect(folderIconTypeAt(it)) },
        modifier = Modifier.semantics { contentDescription = description }
    )
}

@Composable
private fun FolderIconSuggestionsSection(
    draft: FolderFormDraft,
    onPick: (String) -> Unit,
    onOpenAllIcons: () -> Unit
) {
    val marked = draft.effectiveIcon()
    val name = draft.name.trim()
    AureoleMenuSectionLabel(
        text = if (name.isEmpty()) {
            stringResource(R.string.home_folder_suggested)
        } else {
            stringResource(R.string.home_folder_suggested_for, name)
        }
    )
    Row(horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)) {
        folderIconSuggestions(draft.name, draft.icon).forEach { key ->
            FolderChoiceTile(isSelected = isFolderIconMarked(key, marked), onClick = { onPick(key) }) { tint ->
                FolderIconRegistry.RenderIcon(
                    name = key,
                    tint = tint,
                    modifier = Modifier.size(AureoleDS.dimens.iconLarge)
                )
            }
        }
    }
    AureoleMenuRow(
        MenuRowParams(
            title = stringResource(R.string.home_folder_all_icons),
            subtitle = pluralStringResource(
                R.plurals.home_folder_all_icons_count,
                AureoleIconCatalog.entries.size,
                AureoleIconCatalog.entries.size,
                AureoleIconCatalog.categories.size
            ),
            onClick = onOpenAllIcons,
            icon = { m, tint -> AureoleDS.icons.AllApps(m, tint = tint) },
            showChevron = true
        )
    )
}

@Composable
private fun FolderLetterNote() {
    AureoleText(
        text = stringResource(R.string.home_folder_letter_note),
        color = AureoleDS.colors.onSurfaceMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AureoleDS.dimens.xxSmall, vertical = AureoleDS.dimens.xSmall),
        format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
    )
}

/** A choice of the folder form (an icon or an emoji): filled when marked, quiet otherwise; 48 dp to touch. */
@Composable
private fun RowScope.FolderChoiceTile(
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (tint: Color) -> Unit
) {
    Box(
        modifier = modifier
            .weight(1f)
            .height(AureoleDS.dimens.minTouchTarget)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .background(if (isSelected) AureoleDS.colors.onSurfaceHigh else AureoleDS.colors.surfaceVariant)
            .clickable(role = Role.RadioButton, onClick = onClick)
            .semantics { this.selected = isSelected },
        contentAlignment = Alignment.Center
    ) {
        content(if (isSelected) AureoleDS.colors.surface else AureoleDS.colors.onSurfaceHigh)
    }
}

/** Emoji choices in rows; the marked one has a light frame, and "+" opens the keyboard for any other one. */
@Composable
private fun FolderEmojiSection(draft: FolderFormDraft, onPick: (String) -> Unit) {
    FolderEmojiGrid(
        choices = folderEmojiChoices(draft.emoji),
        marked = draft.effectiveEmoji(),
        onPick = onPick
    )
}

/** Tile of one emoji: framed when marked (an emoji keeps its own colors, so it is not filled). */
@Composable
internal fun RowScope.FolderEmojiTile(emoji: String, isSelected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(AureoleDS.dimens.radiusMedium)
    Box(
        modifier = Modifier
            .weight(1f)
            .height(AureoleDS.dimens.minTouchTarget)
            .clip(shape)
            .background(AureoleDS.colors.surfaceVariant)
            .border(
                width = if (isSelected) AureoleDS.dimens.borderMax else AureoleDS.dimens.borderMinimal,
                color = if (isSelected) AureoleDS.colors.onSurfaceHigh else Color.Transparent,
                shape = shape
            )
            .clickable(role = Role.RadioButton, onClick = onClick)
            .semantics { this.selected = isSelected },
        contentAlignment = Alignment.Center
    ) {
        AureoleText(
            text = emoji,
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(style = AureoleDS.typography.titleLarge)
        )
    }
}

/**
 * The grid of every icon, in place of the form ("All icons", pasta-form RF-01/RF-02): a row back to the form and
 * the shared [AureoleIconCatalogGrid], which opens on the category of [selectedKey].
 */
@Composable
internal fun ColumnScope.FolderAllIconsBody(selectedKey: String?, onSelect: (String) -> Unit, onBack: () -> Unit) {
    AureoleMenuRow(
        MenuRowParams(
            title = stringResource(R.string.home_folder_all_icons_back),
            onClick = onBack,
            icon = { m, tint -> AureoleDS.icons.ChevronLeft(m, tint = tint) }
        )
    )
    Spacer(modifier = Modifier.height(AureoleDS.dimens.xxSmall))
    AureoleIconCatalogGrid(
        selectedKey = selectedKey,
        onSelect = onSelect,
        modifier = Modifier.weight(1f, fill = false)
    )
}

@AureolePreview
@Composable
private fun FolderFormBodyPreview() {
    AureoleLauncherTheme {
        Column {
            FolderFormBody(
                state = FolderFormBodyState(draft = FolderFormDraft(name = "Bank"), showDelete = true),
                actions = FolderFormBodyActions(onDraftChange = {}, onOpenAllIcons = {}, onDelete = {})
            )
        }
    }
}
