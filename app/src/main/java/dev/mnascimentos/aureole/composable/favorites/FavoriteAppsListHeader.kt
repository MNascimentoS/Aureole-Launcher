package dev.mnascimentos.aureole.composable.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.composable.favorites.model.ShowAllAppsParams
import dev.mnascimentos.aureole.composable.favorites.model.ShowLabelsParams
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Check
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Options of a home apps list on one line (RF-A1): the "Show all apps" and "Labels" chips, which apply at once.
 * "Labels" only matters with all apps shown, so it is disabled with "Show all apps" off. Without
 * [FavoriteAppsDialogConfig.showAllApps] (Settings, drawer favorites) there is no line.
 */
@Composable
internal fun FavoriteAppsListHeader(
    config: FavoriteAppsDialogConfig,
    modifier: Modifier = Modifier
) {
    val showAllApps = config.showAllApps ?: return
    // A chip that does not fit next to the other goes to the next line, whole, instead of breaking its label.
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        FavoriteAppsOptionChip(
            label = stringResource(R.string.home_apps_list_show_all),
            selected = showAllApps.checked,
            onClick = { showAllApps.onCheckedChange(!showAllApps.checked) }
        )
        config.showLabels?.let { labels ->
            FavoriteAppsOptionChip(
                label = stringResource(R.string.home_favorites_chip_labels),
                selected = labels.checked,
                onClick = { labels.onCheckedChange(!labels.checked) },
                enabled = showAllApps.checked
            )
        }
    }
}

/** A toggle chip of the options line: filled with a check while on. */
@Composable
private fun FavoriteAppsOptionChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        enabled = enabled,
        label = { AureoleText(label, format = AureoleTextFormatParams(maxLines = 1)) },
        leadingIcon = if (selected) {
            {
                // The icon has its own default tint; the chip's leading icon color does not reach it.
                AureoleDS.icons.Check(
                    modifier = Modifier.size(AureoleDS.dimens.iconSmall),
                    tint = if (enabled) AureoleDS.colors.surface else AureoleDS.colors.onSurfaceLow
                )
            }
        } else {
            null
        },
        shape = RoundedCornerShape(AureoleDS.dimens.radiusFull),
        border = FilterChipDefaults.filterChipBorder(
            enabled = enabled,
            selected = selected,
            borderColor = AureoleDS.colors.onSurfaceMedium.copy(alpha = 0.3f),
            selectedBorderColor = AureoleDS.colors.onSurfaceHigh,
            disabledBorderColor = AureoleDS.colors.onSurfaceMedium.copy(alpha = 0.15f),
            disabledSelectedBorderColor = AureoleDS.colors.onSurfaceMedium.copy(alpha = 0.15f),
            borderWidth = AureoleDS.dimens.borderMinimal,
            selectedBorderWidth = AureoleDS.dimens.borderMinimal
        ),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = AureoleDS.colors.surfaceVariant.copy(alpha = 0.6f),
            labelColor = AureoleDS.colors.onSurfaceHigh,
            selectedContainerColor = AureoleDS.colors.onSurfaceHigh,
            selectedLabelColor = AureoleDS.colors.surface,
            selectedLeadingIconColor = AureoleDS.colors.surface,
            disabledContainerColor = AureoleDS.colors.surfaceVariant.copy(alpha = 0.2f),
            disabledLabelColor = AureoleDS.colors.onSurfaceLow,
            disabledSelectedContainerColor = AureoleDS.colors.surfaceVariant.copy(alpha = 0.2f),
            disabledLeadingIconColor = AureoleDS.colors.onSurfaceLow
        )
    )
}

@AureolePreview
@Composable
private fun FavoriteAppsListHeaderPreview() {
    AureoleLauncherTheme {
        FavoriteAppsListHeader(
            config = FavoriteAppsDialogConfig(
                allApps = emptyList(),
                favoriteAppPackages = listOf("a", "b", "c"),
                showAllApps = ShowAllAppsParams(checked = false, onCheckedChange = {}),
                showLabels = ShowLabelsParams(checked = true, onCheckedChange = {})
            )
        )
    }
}
