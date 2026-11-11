package dev.mnascimentos.aureole.composable.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.menu.model.MenuQuickAction
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Info
import dev.mnascimentos.aureole.core.designsystem.icons.Settings
import dev.mnascimentos.aureole.core.designsystem.icons.Favorite
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState

@Composable
private fun MenuRowsCase(isLeftHanded: Boolean) {
    CompositionLocalProvider(LocalHomeUiState provides MainUiState(isLeftHandedMode = isLeftHanded)) {
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
private fun QuickActionsCase(isLeftHanded: Boolean) {
    CompositionLocalProvider(LocalHomeUiState provides MainUiState(isLeftHandedMode = isLeftHanded)) {
        AureoleLauncherTheme {
            AureoleMenuQuickActions(
                actions = listOf(
                    MenuQuickAction("Favorite", { m, c -> AureoleDS.icons.Favorite(modifier = m, tint = c) }, {}, true),
                    MenuQuickAction("Info", { m, c -> AureoleDS.icons.Info(modifier = m, tint = c) }, {}),
                    MenuQuickAction(
                        "Hold to uninstall",
                        { m, c -> AureoleDS.icons.Delete(modifier = m, tint = c) },
                        {},
                        isDestructive = true,
                        requiresHold = true
                    )
                ),
                modifier = Modifier.padding(AureoleDS.dimens.medium)
            )
        }
    }
}

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
private fun MenuQuickActionsRightHandedScreenshotTest() = QuickActionsCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun MenuQuickActionsLeftHandedScreenshotTest() = QuickActionsCase(isLeftHanded = true)
