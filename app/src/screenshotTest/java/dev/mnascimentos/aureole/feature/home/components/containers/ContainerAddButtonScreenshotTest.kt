package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.folder.ContainerAddFolderButton

@PreviewTest
@AureolePreview
@Composable
private fun ContainerAddButtonScreenshotTest() {
    AureoleLauncherTheme {
        Row(modifier = Modifier.padding(AureoleDS.dimens.medium)) {
            ContainerAddFolderButton(onClick = {})
            ContainerAddFolderButton(onClick = {}, isBackgroundEnabled = true)
        }
    }
}
