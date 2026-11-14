package dev.mnascimentos.aureole.composable.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.composable.menu.model.MenuEntry
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Settings
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.MainUiState

@Composable
private fun MenuRowsCase(isLeftHanded: Boolean) {
    ProvideHomeUiState(MainUiState(isLeftHandedMode = isLeftHanded)) {
        AureoleLauncherTheme {
            Column(modifier = Modifier.padding(AureoleDS.dimens.medium)) {
                AureoleMenuRow(
                    MenuRowParams(
                        title = "Remove",
                        onClick = {},
                        icon = { m, c -> AureoleDS.icons.Delete(modifier = m, tint = c) },
                        isDestructive = true
                    )
                )
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
            }
        }
    }
}

@Composable
private fun HoldRowCase(isLeftHanded: Boolean) {
    ProvideHomeUiState(MainUiState(isLeftHandedMode = isLeftHanded)) {
        AureoleLauncherTheme {
            Column(modifier = Modifier.padding(AureoleDS.dimens.medium)) {
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
    }
}

@Composable
private fun EntriesListCase(isLeftHanded: Boolean) {
    ProvideHomeUiState(MainUiState(isLeftHandedMode = isLeftHanded)) {
        AureoleLauncherTheme {
            Column(modifier = Modifier.padding(AureoleDS.dimens.medium)) {
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
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun MenuEntriesListRightHandedScreenshotTest() = EntriesListCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun MenuEntriesListLeftHandedScreenshotTest() = EntriesListCase(isLeftHanded = true)

@PreviewTest
@AureolePreview
@Composable
private fun MenuRowsRightHandedScreenshotTest() = MenuRowsCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun MenuRowsLeftHandedScreenshotTest() = MenuRowsCase(isLeftHanded = true)

@PreviewTest
@AureolePreview
@Composable
private fun MenuHoldRowRightHandedScreenshotTest() = HoldRowCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun MenuHoldRowLeftHandedScreenshotTest() = HoldRowCase(isLeftHanded = true)
