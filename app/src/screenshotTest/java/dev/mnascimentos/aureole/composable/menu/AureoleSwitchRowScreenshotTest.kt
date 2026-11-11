package dev.mnascimentos.aureole.composable.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState

@Composable
private fun SwitchRowsCase(isLeftHanded: Boolean) {
    CompositionLocalProvider(LocalHomeUiState provides MainUiState(isLeftHandedMode = isLeftHanded)) {
        AureoleLauncherTheme {
            Column(modifier = Modifier.padding(AureoleDS.dimens.medium)) {
                AureoleSwitchRow(
                    title = "Show all apps on Home",
                    description = "If disabled, shows only favorited apps",
                    checked = true,
                    onCheckedChange = {}
                )
                AureoleSwitchRow(title = "Show position indicator", checked = false, onCheckedChange = {})
                AureoleMenuRow(
                    MenuRowParams(
                        title = "Appearance",
                        subtitle = "Cells, folders and background",
                        onClick = {},
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
