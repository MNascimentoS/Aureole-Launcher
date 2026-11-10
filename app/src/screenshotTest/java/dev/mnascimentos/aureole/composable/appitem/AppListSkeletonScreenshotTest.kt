package dev.mnascimentos.aureole.composable.appitem

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@PreviewTest
@AureolePreview
@Composable
private fun AppListSkeletonScreenshotTest() {
    AureoleLauncherTheme {
        AppListSkeletonComponent(modifier = Modifier.padding(AureoleDS.dimens.large))
    }
}
