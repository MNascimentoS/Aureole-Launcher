package dev.mnascimentos.aureole.composable.menu

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.menu.model.MenuEntry
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS

/** The "Customize home" row every component edit sheet carries; [onClick] closes the sheet and opens Customize. */
@Composable
fun customizeHomeRowParams(onClick: () -> Unit): MenuRowParams = MenuRowParams(
    title = stringResource(R.string.home_menu_customize_home),
    subtitle = stringResource(R.string.home_menu_customize_home_subtitle),
    onClick = onClick,
    icon = { m, c -> AureoleDS.icons.Edit(modifier = m, tint = c) },
    showChevron = true
)

/** [customizeHomeRowParams] as a menu entry, to sit right before the destructive group. */
@Composable
fun customizeHomeEntry(onClick: () -> Unit): MenuEntry = MenuEntry.Row(customizeHomeRowParams(onClick))
