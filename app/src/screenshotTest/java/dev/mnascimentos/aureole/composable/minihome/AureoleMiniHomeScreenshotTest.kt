package dev.mnascimentos.aureole.composable.minihome

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@Composable
private fun MiniHomeCase(isLeftHanded: Boolean) {
    AureoleLauncherTheme {
        Box(modifier = Modifier.padding(AureoleDS.dimens.medium)) {
            AureoleMiniHome(content = sampleMiniHomeContent(), isLeftHanded = isLeftHanded)
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun AureoleMiniHomeRightHandedScreenshotTest() = MiniHomeCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun AureoleMiniHomeLeftHandedScreenshotTest() = MiniHomeCase(isLeftHanded = true)
