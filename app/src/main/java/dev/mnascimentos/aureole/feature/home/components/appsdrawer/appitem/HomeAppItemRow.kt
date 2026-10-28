package dev.mnascimentos.aureole.feature.home.components.appsdrawer.appitem

import android.content.ComponentName
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.composable.appitem.AppItemRow
import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowActions
import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowConfig
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState

@Composable
fun HomeAppItemRow(
    app: AppInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    config: AppItemRowConfig = AppItemRowConfig(),
    actions: AppItemRowActions = AppItemRowActions()
) {
    val uiState = LocalHomeUiState.current
    val homeActions = LocalHomeActions.current
    AppItemRow(
        app = app,
        onClick = onClick,
        modifier = modifier,
        config = config.copy(
            isThemedAppIconsEnabled = uiState.isThemedAppIconsEnabled,
            isLeftHandedMode = uiState.isLeftHandedMode
        ),
        actions = actions.copy(onOpenSettings = actions.onOpenSettings ?: homeActions.onSettingsClick)
    )
}

@AureolePreview
@Composable
private fun HomeAppItemRowPreview() {
    val mockApp = AppInfo(
        label = "Sample App",
        packageName = "com.example.sample",
        componentName = ComponentName("com.example.sample", "MainActivity"),
        icon = 0xFF1B65C0.toInt().toDrawable()
    )
    AureoleLauncherTheme {
        HomeAppItemRow(
            app = mockApp,
            onClick = {}
        )
    }
}
