package dev.mnascimentos.aureole.composable.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.composable.menu.model.MenuSheetParams
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import kotlinx.coroutines.delay

/** Lets the sheet slide out before the caller removes it. */
private const val DISMISS_DELAY_MS = 300L

/**
 * Base of every menu sheet (app, grid item, folder, ...): the edit overlay with a header (icon, title and a
 * settings button), a scrollable list of rows and an optional bottom bar. The thumb side comes from the
 * launcher setting, so the callers do not pass it.
 */
@Composable
fun AureoleMenuSheet(
    title: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    params: MenuSheetParams = MenuSheetParams(),
    content: @Composable ColumnScope.() -> Unit
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
                isLeftHanded = LocalHomeUiState.current.isLeftHandedMode,
                onSettingsClick = params.onSettingsClick,
                titleIcon = params.titleIcon
            ),
            bottomBarContent = bottomBar?.let { bar -> { bar() } },
            sheetContent = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    content()
                }
            }
        )
    )
}

@AureolePreview
@Composable
private fun AureoleMenuSheetPreview() {
    AureoleLauncherTheme {
        AureoleMenuSheet(title = "Menu", onDismiss = {}) {
            AureoleMenuRow(MenuRowParams(title = "Remove", onClick = {}, isDestructive = true))
            AureoleMenuSectionLabel(text = "SECTION")
            AureoleMenuRow(MenuRowParams(title = "Action", onClick = {}, showChevron = true))
        }
    }
}
