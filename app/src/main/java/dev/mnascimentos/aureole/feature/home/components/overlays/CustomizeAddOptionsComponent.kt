package dev.mnascimentos.aureole.feature.home.components.overlays

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.ScrollOrientation
import dev.mnascimentos.aureole.core.designsystem.icons.Add
import dev.mnascimentos.aureole.core.designsystem.icons.AllApps
import dev.mnascimentos.aureole.core.designsystem.icons.Click
import dev.mnascimentos.aureole.core.designsystem.icons.Favorite
import dev.mnascimentos.aureole.core.designsystem.icons.MultipleView
import dev.mnascimentos.aureole.core.designsystem.icons.ScrollGroup
import dev.mnascimentos.aureole.core.designsystem.icons.VerticalContainer
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions

/** The "Add …" half of the Customize sheet, in the order of the customize proposal. */
@Composable
internal fun CustomizeAddOptionsComponent(
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit
) {
    val iconModifier = Modifier.size(AureoleDS.dimens.iconLarge)
    val tint = AureoleDS.colors.onSurfaceMedium
    fun addItem(type: LauncherItemType, orientation: ScrollOrientation? = null): () -> Unit = {
        actions.onAddGridItem(type, null, orientation)
        onDismissRequest()
    }
    Column {
        CustomizeOptionItem(
            title = stringResource(R.string.home_customize_add_clock),
            icon = { AureoleDS.icons.Add(modifier = iconModifier, tint = tint) },
            onClick = addItem(LauncherItemType.CLOCK)
        )
        CustomizeOptionItem(
            title = stringResource(R.string.home_customize_add_favorites),
            icon = { AureoleDS.icons.Favorite(modifier = iconModifier, tint = tint) },
            onClick = {
                actions.onAddFavoritesList()
                onDismissRequest()
            }
        )
        CustomizeOptionItem(
            title = stringResource(R.string.home_customize_add_all_apps),
            icon = { AureoleDS.icons.AllApps(modifier = iconModifier, tint = tint) },
            onClick = addItem(LauncherItemType.ALL_APPS_LIST)
        )
        CustomizeOptionItem(
            title = stringResource(R.string.home_customize_add_container),
            icon = { AureoleDS.icons.VerticalContainer(modifier = iconModifier, tint = tint) },
            onClick = addItem(LauncherItemType.SHORTCUTS_CONTAINER)
        )
        CustomizeOptionItem(
            title = stringResource(R.string.home_customize_add_widget_group),
            icon = { AureoleDS.icons.MultipleView(modifier = iconModifier, tint = tint) },
            onClick = addItem(LauncherItemType.WIDGET_LIST)
        )
        CustomizeOptionItem(
            title = stringResource(R.string.home_customize_add_scrollview),
            icon = { AureoleDS.icons.ScrollGroup(modifier = iconModifier, tint = tint) },
            onClick = addItem(LauncherItemType.SCROLL_VIEW, ScrollOrientation.VERTICAL)
        )
        CustomizeOptionItem(
            title = stringResource(R.string.home_customize_add_quick_button),
            icon = { AureoleDS.icons.Click(modifier = iconModifier, tint = tint) },
            onClick = addItem(LauncherItemType.QUICK_BUTTON)
        )
    }
}

@AureolePreview
@Composable
private fun CustomizeAddOptionsPreview() {
    AureoleLauncherTheme {
        CustomizeAddOptionsComponent(actions = createDefaultHomeScreenActions(), onDismissRequest = {})
    }
}
