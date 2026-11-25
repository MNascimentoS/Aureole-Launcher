package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.composable.reorder.rememberReorderAutoScroll
import dev.mnascimentos.aureole.composable.reorder.reorderAutoScrollViewport
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.utils.fadingEdges
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupAppListParams
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupItemsTabParams
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupSheetBodyParams
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupSheetPage
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupTab
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupTip
import dev.mnascimentos.aureole.feature.home.components.quickbutton.QuickButtonShortcutPickerContent

/** The content of the group sheet: the tab chosen, or the page open in its place (RF-B10). */
@Composable
internal fun ColumnScope.GroupSheetBody(params: GroupSheetBodyParams) {
    val state = params.state
    val nav = params.nav
    var query by remember(nav.page) { mutableStateOf("") }
    when (val page = nav.page) {
        GroupSheetPage.Tabs -> GroupTabs(params)
        GroupSheetPage.AddApps -> GroupAddAppsPage(
            GroupAppListParams(
                allApps = params.allApps,
                query = query,
                onQueryChange = { query = it },
                isMarked = state::hasApp,
                onToggle = state::toggleApp
            )
        )
        GroupSheetPage.NewFolder -> GroupNewFolderPage(
            draft = nav.newFolder,
            onNameChange = { nav.newFolder = nav.newFolder.copy(name = it) },
            allApps = params.allApps,
            onAppsChange = { nav.newFolder = nav.newFolder.copy(apps = it) }
        )
        GroupSheetPage.QuickButton -> GroupScrollingPage {
            QuickButtonShortcutPickerContent(model = QuickButtonModel(), apps = params.allApps, onPick = { action ->
                state.addQuickButton(action)
                nav.back()
            })
        }
        is GroupSheetPage.Color -> GroupColorPage(state = state, target = page.target) { picked ->
            // Applied at once, then back to the Appearance tab (RF-E2).
            state.setColor(page.target, picked)
            nav.back()
        }
        is GroupSheetPage.FolderEditor -> GroupFolderEditorPage(nav, state, page.folderId)
        is GroupSheetPage.QuickButtonEditor -> GroupQuickButtonEditorPage(state, page.quickButtonId)
    }
}

/** The tab chosen, in one scrolling column that fades under the tab bar, with the tip of the tab at its top. */
@Composable
private fun ColumnScope.GroupTabs(params: GroupSheetBodyParams) {
    val nav = params.nav
    val tips = params.tips
    val scrollState = rememberScrollState()
    val autoScroll = rememberReorderAutoScroll(scrollState)
    Column(
        modifier = Modifier
            .weight(1f, fill = false)
            .fillMaxWidth()
            .reorderAutoScrollViewport(autoScroll)
            .fadingEdges(scrollState, AureoleDS.dimens.xSmall)
            .verticalScroll(scrollState)
    ) {
        GroupTipCard(
            tips = tips,
            tab = nav.tab,
            onAddApp = { nav.open(GroupSheetPage.AddApps) },
            onChoosePosition = { params.onSelectTab(GroupTab.LAYOUT) },
            modifier = Modifier.padding(bottom = AureoleDS.dimens.small)
        )
        when (nav.tab) {
            GroupTab.ITEMS -> GroupItemsTab(
                GroupItemsTabParams(
                    state = params.state,
                    allApps = params.allApps,
                    nav = nav,
                    tip = tips.tip,
                    autoScroll = autoScroll,
                    onRemove = params.onRemove
                )
            )
            GroupTab.LAYOUT -> GroupLayoutTab(
                state = params.state,
                isPositionPulsing = tips.tip == GroupTip.PICK_POSITION,
                onPositionPicked = tips::onPositionPicked
            )
            GroupTab.APPEARANCE -> GroupAppearanceTab(state = params.state) { nav.open(GroupSheetPage.Color(it)) }
        }
        Spacer(modifier = Modifier.height(AureoleDS.dimens.large))
    }
}

/** A page whose content does not scroll by itself: it scrolls in the room the sheet has. */
@Composable
private fun ColumnScope.GroupScrollingPage(content: @Composable ColumnScope.() -> Unit) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .weight(1f, fill = false)
            .fillMaxWidth()
            .fadingEdges(scrollState, AureoleDS.dimens.xSmall)
            .verticalScroll(scrollState),
        content = content
    )
}
