package dev.mnascimentos.aureole.composable.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import dev.mnascimentos.aureole.composable.menu.model.MenuEntry
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.composable.menu.model.withDestructiveDivider
import dev.mnascimentos.aureole.core.designsystem.components.AureoleDivider
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Draws [entries] as a vertical list, destructive ones last after a divider (see [withDestructiveDivider]).
 * Meant for the content slot of the menu sheet.
 */
@Composable
internal fun ColumnScope.AureoleMenuEntries(entries: List<MenuEntry>) {
    withDestructiveDivider(entries).forEach { entry ->
        when (entry) {
            is MenuEntry.Row -> AureoleMenuRow(entry.params)
            is MenuEntry.Section -> AureoleMenuSectionLabel(text = entry.text)
            is MenuEntry.Note -> AureoleMenuNote(text = entry.text)
            is MenuEntry.Content -> entry.content()
            MenuEntry.Divider -> AureoleDivider()
        }
    }
}

@AureolePreview
@Composable
private fun AureoleMenuEntriesPreview() {
    AureoleLauncherTheme {
        Column {
            AureoleMenuEntries(
                entries = listOf(
                    MenuEntry.Section(text = "SECTION"),
                    MenuEntry.Row(MenuRowParams(title = "Action", onClick = {}, showChevron = true)),
                    MenuEntry.Row(MenuRowParams(title = "Remove", onClick = {}, isDestructive = true)),
                    MenuEntry.Row(MenuRowParams(title = "Info", onClick = {}, showChevron = true))
                )
            )
        }
    }
}
