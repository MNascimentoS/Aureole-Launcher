package dev.mnascimentos.aureole.composable.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.composable.menu.model.MenuEntry
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.composable.menu.model.withDestructiveDivider
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val DIVIDER_ALPHA = 0.5f

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
            MenuEntry.Divider -> MenuDivider()
        }
    }
}

@Composable
private fun MenuDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier.padding(vertical = AureoleDS.dimens.xSmall),
        thickness = AureoleDS.dimens.borderMinimal,
        color = AureoleDS.colors.outline.copy(alpha = DIVIDER_ALPHA)
    )
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
