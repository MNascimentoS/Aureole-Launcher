package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.clock.scaledLayout
import dev.mnascimentos.aureole.composable.controls.AureoleSheetTabBar
import dev.mnascimentos.aureole.composable.dialogs.model.folderFormTitleRes
import dev.mnascimentos.aureole.composable.preview.AureolePreviewFrame
import dev.mnascimentos.aureole.composable.preview.aureoleDashedBorder
import dev.mnascimentos.aureole.composable.preview.model.previewFitScale
import dev.mnascimentos.aureole.composable.sheets.AureoleSheetSavedHint
import dev.mnascimentos.aureole.composable.sheets.asSheetPage
import dev.mnascimentos.aureole.composable.tips.TipPulse
import dev.mnascimentos.aureole.composable.undo.AureoleUndoSnackbar
import dev.mnascimentos.aureole.composable.undo.model.UndoSnackbarState
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerPreviewMetrics
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupRemovalBar
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupSheetNav
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupSheetPage
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupTab
import dev.mnascimentos.aureole.feature.home.components.containers.model.addSlotCountFor
import dev.mnascimentos.aureole.feature.home.components.containers.model.groupItemCount
import dev.mnascimentos.aureole.feature.home.components.containers.model.isLyingDown

/**
 * The header of the page open in [nav] (RF-B10): back arrow, title and subtitle, no tab bar; on the tabs, [this].
 * Back ([onBack]) returns to the tab; dragging down or tapping outside closes the sheet ([onClose]).
 */
@Composable
internal fun AureoleEditOverlayConfig.forGroupPage(
    nav: GroupSheetNav,
    state: EditContainerState,
    onBack: () -> Unit,
    onClose: () -> Unit
): AureoleEditOverlayConfig {
    val (title, subtitle) = when (val page = nav.page) {
        GroupSheetPage.Tabs -> return this
        GroupSheetPage.AddApps -> {
            val count = groupItemCount(state.itemsList)
            stringResource(R.string.home_container_group_add_apps_title) to if (count == 0) {
                stringResource(R.string.home_container_group_add_apps_empty)
            } else {
                pluralStringResource(R.plurals.home_container_group_add_apps_count, count, count)
            }
        }
        GroupSheetPage.NewFolder -> stringResource(R.string.home_container_group_new_folder_title) to
            stringResource(R.string.home_container_group_new_folder_subtitle)
        GroupSheetPage.QuickButton -> stringResource(R.string.home_container_group_add_quick_button) to
            stringResource(R.string.home_container_add_quick_button_subtitle)
        is GroupSheetPage.Color -> stringResource(page.target.titleRes) to
            stringResource(R.string.home_container_group_color_subtitle)
        is GroupSheetPage.FolderEditor -> stringResource(folderFormTitleRes(nav.folderMode, isEditing = true)) to null
        is GroupSheetPage.QuickButtonEditor -> stringResource(R.string.home_quickbutton_appearance_title) to null
    }
    return asSheetPage(title = title, subtitle = subtitle, onBack = onBack, onClose = onClose)
}

/**
 * The group drawn above the sheet as on the home (RF-B3, RF-B4): at the size of its cell, shrunk if it does not fit,
 * inside the dashed outline of the cell, with the "+" slots that open "Add apps" ([onAddApps]). Never measured yet,
 * it keeps the old bounded preview.
 */
@Composable
internal fun GroupSheetPreview(state: EditContainerState, homeSize: DpSize?, onAddApps: () -> Unit) {
    val config = state.toConfig().copy(addSlotCount = addSlotCountFor(state.itemsList.size), onAddSlotClick = onAddApps)
    AureolePreviewFrame {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth().padding(horizontal = AureoleDS.dimens.large),
            contentAlignment = Alignment.Center
        ) {
            val sizeModifier = homeSize?.let { size ->
                val scale = previewFitScale(size.width.value, size.height.value, maxWidth.value, maxHeight.value)
                Modifier.scaledLayout(scale).size(size)
            } ?: Modifier.unmeasuredPreview(isLyingDown(state.orientation))
            Container(
                config = config,
                onFolderClick = { _, _ -> },
                modifier = Modifier
                    .aureoleDashedBorder()
                    .padding(AureoleDS.dimens.xSmall)
                    .then(sizeModifier)
            )
        }
    }
}

private fun Modifier.unmeasuredPreview(isLyingDown: Boolean): Modifier = if (isLyingDown) {
    this
        .wrapContentWidth(Alignment.CenterHorizontally)
        .heightIn(min = ContainerPreviewMetrics.MinSize, max = ContainerPreviewMetrics.MaxSize)
} else {
    this
        .wrapContentHeight(Alignment.Bottom)
        .widthIn(min = ContainerPreviewMetrics.MinSize, max = ContainerPreviewMetrics.MaxSize)
}

/**
 * Below the tabs' content (RF-B6, RF-C6): the "removed" snackbar with Undo while one shows, the "saved when you
 * close" line on the Items tab only (D-08), and the tab bar; [isLayoutPulsing] rings the Layout tab (tip 2).
 */
@Composable
internal fun ColumnScope.GroupSheetBottomBar(
    nav: GroupSheetNav,
    removal: GroupRemovalBar,
    isLayoutPulsing: Boolean,
    onTabSelected: (GroupTab) -> Unit
) {
    AureoleUndoSnackbar(
        state = removal.removed?.let {
            UndoSnackbarState(
                id = it.id,
                message = TextResource.fromResId(R.string.home_container_group_removed, it.name)
            )
        },
        isLeftHanded = removal.isLeftHanded,
        onUndo = removal.onUndo,
        onTimeout = removal.onTimeout,
        modifier = Modifier.padding(bottom = AureoleDS.dimens.xSmall)
    )
    if (nav.tab == GroupTab.ITEMS) AureoleSheetSavedHint()
    Box(modifier = Modifier.fillMaxWidth()) {
        AureoleSheetTabBar(
            labels = GroupTab.entries.map { stringResource(it.labelRes) },
            selectedIndex = GroupTab.entries.indexOf(nav.tab),
            description = stringResource(R.string.home_container_group_tab_bar_description),
            onSelect = { onTabSelected(GroupTab.entries[it]) }
        )
        if (isLayoutPulsing) {
            // The ring over the Layout segment takes no touch: the tab below still gets the tap.
            Row(modifier = Modifier.matchParentSize().padding(AureoleDS.dimens.xxSmall)) {
                GroupTab.entries.forEach { tab ->
                    TipPulse(isActive = tab == GroupTab.LAYOUT, modifier = Modifier.weight(1f).fillMaxHeight()) {
                        Spacer(modifier = Modifier.matchParentSize())
                    }
                }
            }
        }
    }
}
