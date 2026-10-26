package dev.mnascimentos.aureole.feature.home.components.appsdrawer.appitem

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.composable.appitem.AppItemRow
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.feature.home.components.appsdrawer.appitem.model.AppItemRowActions
import dev.mnascimentos.aureole.feature.home.components.appsdrawer.appitem.model.AppItemRowConfig

@Composable
fun AppItemRow(
    app: AppInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    config: AppItemRowConfig = AppItemRowConfig(),
    actions: AppItemRowActions = AppItemRowActions()
) {
    AppItemRow(
        app = app,
        onClick = onClick,
        modifier = modifier,
        config = config,
        actions = actions
    )
}
