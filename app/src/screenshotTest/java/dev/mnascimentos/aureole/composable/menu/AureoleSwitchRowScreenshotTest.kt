package dev.mnascimentos.aureole.composable.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.composable.menu.model.SwitchRowParams
import dev.mnascimentos.aureole.core.designsystem.icons.ColorFill
import dev.mnascimentos.aureole.core.designsystem.icons.Dots
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.ProvideHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState

@Composable
private fun SwitchRowsCase(isLeftHanded: Boolean) {
    ProvideHomeUiState(MainUiState(isLeftHandedMode = isLeftHanded)) {
        AureoleLauncherTheme {
            Column(modifier = Modifier.padding(AureoleDS.dimens.medium)) {
                AureoleSwitchRow(
                    SwitchRowParams(
                        title = "Show all apps on Home",
                        description = "If disabled, shows only favorited apps",
                        checked = true,
                        onCheckedChange = {}
                    )
                )
                AureoleSwitchRow(
                    SwitchRowParams(
                        title = "Show position indicator",
                        checked = false,
                        onCheckedChange = {},
                        icon = { m, c -> AureoleDS.icons.Dots(modifier = m, tint = c) }
                    )
                )
                AureoleMenuRow(
                    MenuRowParams(
                        title = "Appearance",
                        subtitle = "Cells, folders and background",
                        onClick = {},
                        icon = { m, c -> AureoleDS.icons.ColorFill(modifier = m, tint = c) },
                        showChevron = true
                    )
                )
            }
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun SwitchRowsRightHandedScreenshotTest() = SwitchRowsCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun SwitchRowsLeftHandedScreenshotTest() = SwitchRowsCase(isLeftHanded = true)
