package dev.mnascimentos.aureole.composable.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.composable.menu.model.MenuEntry
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.composable.menu.model.MenuSheetParams
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import kotlinx.coroutines.delay

/** Lets the sheet slide out before the caller removes it. */
private const val DISMISS_DELAY_MS = 300L

/**
 * Base of every menu sheet (app, grid item, folder, ...): the edit overlay with a header (icon, title and a
 * settings button), a scrollable list of rows and an optional bottom bar. The thumb side comes from the
 * launcher setting, so the callers do not pass it. [content] receives `dismiss`, which closes the sheet with its
 * 300 ms exit animation and then calls [onDismiss]; rows that close the sheet on click call it instead of [onDismiss].
 */
@Composable
fun AureoleMenuSheet(
    title: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    params: MenuSheetParams = MenuSheetParams(),
    content: @Composable ColumnScope.(dismiss: () -> Unit) -> Unit
) {
    var isVisible by remember { mutableStateOf(true) }
    if (!isVisible) {
        LaunchedEffect(isVisible) {
            delay(DISMISS_DELAY_MS)
            onDismiss()
        }
    }

    val bottomBar = params.bottomBar
    AureoleEditOverlay(
        onDismissRequest = { isVisible = false },
        modifier = modifier,
        params = AureoleEditOverlayParams(
            config = AureoleEditOverlayConfig(
                title = title,
                isVisible = isVisible,
                isLeftHanded = LocalLeftHanded.current,
                onSettingsClick = params.onSettingsClick,
                titleIcon = params.titleIcon
            ),
            bottomBarContent = bottomBar?.let { bar -> { bar() } },
            sheetContent = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    content { isVisible = false }
                    if (bottomBar == null) Spacer(Modifier.height(AureoleDS.dimens.large))
                }
            }
        )
    )
}

/**
 * [AureoleMenuSheet] for a list of rows: [entries] are built with `dismiss` in hand and drawn with the destructive
 * ones (Remove, Uninstall) at the end, after a divider, in red. Every list menu goes through here so the rule holds
 * for all of them.
 */
@Composable
fun AureoleMenuListSheet(
    title: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    params: MenuSheetParams = MenuSheetParams(),
    entries: @Composable (dismiss: () -> Unit) -> List<MenuEntry>
) {
    AureoleMenuSheet(title = title, onDismiss = onDismiss, modifier = modifier, params = params) { dismiss ->
        AureoleMenuEntries(entries = entries(dismiss))
    }
}

@AureolePreview
@Composable
private fun AureoleMenuSheetPreview() {
    AureoleLauncherTheme {
        AureoleMenuListSheet(title = "Menu", onDismiss = {}) {
            listOf(
                MenuEntry.Row(MenuRowParams(title = "Remove", onClick = {}, isDestructive = true)),
                MenuEntry.Section(text = "SECTION"),
                MenuEntry.Row(MenuRowParams(title = "Action", onClick = {}, showChevron = true))
            )
        }
    }
}
