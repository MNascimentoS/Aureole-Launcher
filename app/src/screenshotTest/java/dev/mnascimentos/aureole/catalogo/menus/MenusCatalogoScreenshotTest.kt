/**
 * Menus.
 *
 * @en Menus
 */
package dev.mnascimentos.aureole.catalogo.menus

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.actions.AureoleActionRow
import dev.mnascimentos.aureole.composable.actions.AureolePrimaryActionButton
import dev.mnascimentos.aureole.composable.actions.model.ActionRowParams
import dev.mnascimentos.aureole.composable.menu.AureoleMenuEntries
import dev.mnascimentos.aureole.composable.menu.AureoleMenuNote
import dev.mnascimentos.aureole.composable.menu.AureoleMenuRow
import dev.mnascimentos.aureole.composable.menu.AureoleMenuSectionLabel
import dev.mnascimentos.aureole.composable.menu.model.MenuEntry
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupBox
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupHeader
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupMenuItem
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupMenuItemParams
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Settings
import dev.mnascimentos.aureole.core.designsystem.model.AureolePopupHeaderParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleCatalogPreview
import dev.mnascimentos.aureole.fixtures.CatalogoColumn
import dev.mnascimentos.aureole.fixtures.CatalogoHand
import dev.mnascimentos.aureole.fixtures.CatalogoLabel

private val POPUP_WIDTH = 260.dp

/** Nota, rótulo de seção, linha com chevron e a destrutiva por último (aureole_components §6). */
@Composable
private fun MenuRowsCase(isLeftHanded: Boolean) = CatalogoHand(isLeftHanded) {
    Column(modifier = Modifier.padding(horizontal = AureoleDS.dimens.medium)) {
        AureoleMenuNote(text = "Current size: 2 x 2 cells")
        AureoleMenuSectionLabel(text = "Actions")
        AureoleMenuRow(
            MenuRowParams(
                title = "Settings",
                onClick = {},
                icon = { m, c -> AureoleDS.icons.Settings(modifier = m, tint = c) },
                showChevron = true
            )
        )
        AureoleMenuRow(
            MenuRowParams(
                title = "Remove",
                onClick = {},
                icon = { m, c -> AureoleDS.icons.Delete(modifier = m, tint = c) },
                isDestructive = true
            )
        )
    }
}

/** Linha destrutiva que pede segurar para confirmar. */
@Composable
private fun HoldRowCase(isLeftHanded: Boolean) = CatalogoHand(isLeftHanded) {
    Column(modifier = Modifier.padding(horizontal = AureoleDS.dimens.medium)) {
        AureoleMenuRow(
            MenuRowParams(
                title = "Uninstall",
                subtitle = "Hold to confirm",
                onClick = {},
                icon = { m, c -> AureoleDS.icons.Delete(modifier = m, tint = c) },
                isDestructive = true,
                requiresHold = true
            )
        )
    }
}

/** Lista de entradas do menu (linha, seção, linhas). */
@Composable
private fun EntriesListCase(isLeftHanded: Boolean) = CatalogoHand(isLeftHanded) {
    Column(modifier = Modifier.padding(horizontal = AureoleDS.dimens.medium)) {
        AureoleMenuEntries(
            entries = listOf(
                MenuEntry.Row(MenuRowParams(title = "Remove from folder", onClick = {}, isDestructive = true)),
                MenuEntry.Section(text = "Actions"),
                MenuEntry.Row(MenuRowParams(title = "App info", onClick = {}, showChevron = true)),
                MenuEntry.Row(MenuRowParams(title = "Favorite", onClick = {}, showChevron = true))
            )
        )
    }
}

@Composable
private fun ActionRowCase(isLeftHanded: Boolean, isDestructive: Boolean = false) {
    AureoleActionRow(
        params = ActionRowParams("Save", {}, "Restore", {}, isSecondaryDestructive = isDestructive),
        isLeftHanded = isLeftHanded,
        modifier = Modifier.padding(horizontal = AureoleDS.dimens.medium)
    )
}

/** Popup com cabeçalho de sessão e itens, o destrutivo por último. */
@Composable
private fun PopupItemsCase() {
    AureolePopupBox(modifier = Modifier.padding(horizontal = AureoleDS.dimens.medium).width(POPUP_WIDTH)) {
        AureolePopupHeader(
            title = "Folder name",
            params = AureolePopupHeaderParams(
                sessionTitle = "Folder actions",
                onSettingsClick = {},
                onCloseClick = {}
            )
        )
        AureolePopupMenuItem(title = "Edit shortcuts", onClick = {})
        AureolePopupMenuItem(
            title = "Delete folder",
            params = AureolePopupMenuItemParams(isDestructive = true),
            onClick = {}
        )
    }
}

/**
 * Menus.
 *
 * Grupo menus: linhas do `AureoleMenu`, `AureoleActionRow` e `AureolePopupItem`, destro e canhoto.
 *
 * @en Menus
 */
@PreviewTest
@AureoleCatalogPreview
@Composable
private fun MenusScreenshotTest() = CatalogoColumn {
    CatalogoLabel("AureoleMenuRow: destro / canhoto")
    MenuRowsCase(isLeftHanded = false)
    MenuRowsCase(isLeftHanded = true)
    CatalogoLabel("AureoleMenuRow segurar: destro / canhoto")
    HoldRowCase(isLeftHanded = false)
    HoldRowCase(isLeftHanded = true)
    CatalogoLabel("AureoleMenuEntries: destro / canhoto")
    EntriesListCase(isLeftHanded = false)
    EntriesListCase(isLeftHanded = true)
    CatalogoLabel("AureoleActionRow: destro / canhoto / destrutivo")
    ActionRowCase(isLeftHanded = false)
    ActionRowCase(isLeftHanded = true)
    ActionRowCase(isLeftHanded = false, isDestructive = true)
    CatalogoLabel("AureolePrimaryActionButton")
    AureolePrimaryActionButton(
        label = "Done",
        onClick = {},
        modifier = Modifier.fillMaxWidth().padding(horizontal = AureoleDS.dimens.medium)
    )
    CatalogoLabel("AureolePopupMenuItem")
    PopupItemsCase()
}
