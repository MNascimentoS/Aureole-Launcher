package dev.mnascimentos.aureole.feature.home.components.appsdrawer.appitem

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.composable.appitem.AppItemRow
import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowActions
import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowConfig
import dev.mnascimentos.aureole.core.data.model.AppInfo
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
