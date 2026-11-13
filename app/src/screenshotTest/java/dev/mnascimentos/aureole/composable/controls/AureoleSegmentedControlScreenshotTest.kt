package dev.mnascimentos.aureole.composable.controls

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@PreviewTest
@AureolePreview
@Composable
private fun AureoleSegmentedControlScreenshotTest() {
    AureoleLauncherTheme {
        AureoleSegmentedControl(
            options = listOf("None", "Light", "Medium", "Max"),
            selectedIndex = 2,
            onSelect = {},
            modifier = Modifier.padding(AureoleDS.dimens.medium)
        )
    }
}
