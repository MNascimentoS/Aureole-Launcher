package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.menu.AureoleMenuRow
import dev.mnascimentos.aureole.composable.menu.AureoleMenuSectionLabel
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Main screen of the container editor: the OPTIONS section with the three sub screens. Save and Cancel live in the
 * bottom bar of the overlay, on the thumb side.
 */
@Composable
internal fun MainSettingsContent(
    componentCount: Int,
    onAppearanceClick: () -> Unit,
    onBehaviorClick: () -> Unit,
    onManageItemsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        AureoleMenuSectionLabel(text = stringResource(R.string.home_container_edit_options_label))
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.home_container_edit_appearance),
                subtitle = stringResource(R.string.home_container_edit_appearance_subtitle),
                onClick = onAppearanceClick,
                showChevron = true
            )
        )
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.home_container_edit_behavior),
                subtitle = stringResource(R.string.home_container_edit_behavior_subtitle),
                onClick = onBehaviorClick,
                showChevron = true
            )
        )
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.home_container_edit_manage_items),
                subtitle = pluralStringResource(
                    R.plurals.home_container_edit_components_count,
                    componentCount,
                    componentCount
                ),
                onClick = onManageItemsClick,
                showChevron = true
            )
        )
    }
}

@AureolePreview
@Composable
private fun MainSettingsContentPreview() {
    AureoleLauncherTheme {
        MainSettingsContent(
            componentCount = 4,
            onAppearanceClick = {},
            onBehaviorClick = {},
            onManageItemsClick = {}
        )
    }
}
