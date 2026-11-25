package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.dialogs.model.FolderButtonMetrics
import dev.mnascimentos.aureole.composable.preview.aureoleDashedBorder
import dev.mnascimentos.aureole.core.designsystem.icons.AddNew
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerConfig
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupSheetMetrics

/**
 * The dashed "+" slots the editor preview draws after the items (RF-B4): one after the last item, three in an empty
 * group, where only the first takes a tap ([ContainerConfig.onAddSlotClick] opens "Add apps"). None on the home.
 */
@Composable
internal fun ContainerAddSlots(config: ContainerConfig) {
    repeat(config.addSlotCount) { index ->
        ContainerAddSlot(
            isBackgroundEnabled = config.isBackgroundEnabled,
            onClick = if (index == 0) config.onAddSlotClick else null,
            modifier = Modifier.padding(AureoleDS.dimens.xxSmall)
        )
    }
}

@Composable
private fun ContainerAddSlot(isBackgroundEnabled: Boolean, onClick: (() -> Unit)?, modifier: Modifier = Modifier) {
    val size = if (isBackgroundEnabled) FolderButtonMetrics.SmallSize else FolderButtonMetrics.MediumSize
    val description = stringResource(R.string.home_container_group_add_apps_title)
    val shape = RoundedCornerShape(AureoleDS.dimens.radiusMedium)
    Box(
        modifier = modifier
            .size(size)
            .alpha(if (onClick != null) 1f else GroupSheetMetrics.FADED_SLOT_ALPHA)
            .clip(shape)
            .aureoleDashedBorder(color = AureoleDS.colors.onSurfaceLow)
            .then(
                if (onClick != null) {
                    Modifier
                        .clickable(role = Role.Button, onClick = onClick)
                        .semantics { contentDescription = description }
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (onClick != null) {
            AureoleDS.icons.AddNew(tint = AureoleDS.colors.onSurfaceMedium, modifier = Modifier.size(AureoleDS.dimens.iconMedium))
        }
    }
}

@AureolePreview
@Composable
private fun ContainerAddSlotsPreview() {
    AureoleLauncherTheme {
        Box {
            ContainerAddSlots(config = ContainerConfig(addSlotCount = 1))
        }
    }
}
