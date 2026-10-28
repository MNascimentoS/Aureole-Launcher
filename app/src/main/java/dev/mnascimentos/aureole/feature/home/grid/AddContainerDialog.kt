package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.components.AureoleDialog
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Add
import dev.mnascimentos.aureole.core.designsystem.icons.Click
import dev.mnascimentos.aureole.core.designsystem.icons.HorizontalContainer
import dev.mnascimentos.aureole.core.designsystem.icons.MenuIcon
import dev.mnascimentos.aureole.core.designsystem.icons.VerticalContainer
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.grid.model.AddContainerDialogMetrics

@Composable
fun AddContainerDialog(
    onDismissRequest: () -> Unit,
    onSelectType: (LauncherItemType) -> Unit,
    modifier: Modifier = Modifier,
    isNested: Boolean = false
) {
    val titleText = if (isNested) {
        stringResource(R.string.home_container_add_nested_title)
    } else {
        stringResource(R.string.home_container_add_title)
    }
    AureoleDialog(
        onDismissRequest = onDismissRequest,
        title = titleText,
        modifier = modifier
    ) {
        AddContainerOptionList(
            isNested = isNested,
            onSelectType = { type ->
                onSelectType(type)
                onDismissRequest()
            }
        )
    }
}

@Composable
private fun AddContainerOptionList(
    isNested: Boolean,
    onSelectType: (LauncherItemType) -> Unit
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.smallCompact)
    ) {
        AddContainerOptionItem(
            title = stringResource(R.string.home_container_add_clock),
            icon = { m -> AureoleDS.icons.Add(m) },
            onClick = { onSelectType(LauncherItemType.CLOCK) }
        )
        AddContainerOptionItem(
            title = stringResource(R.string.home_container_add_apps_list),
            icon = { m -> AureoleDS.icons.HorizontalContainer(m) },
            onClick = { onSelectType(LauncherItemType.APPS_LIST) }
        )
        AddContainerOptionItem(
            title = stringResource(R.string.home_container_add_all_apps_list),
            icon = { m -> AureoleDS.icons.HorizontalContainer(m) },
            onClick = { onSelectType(LauncherItemType.ALL_APPS_LIST) }
        )
        if (!isNested) {
            AddContainerOptionItem(
                title = stringResource(R.string.home_container_add_shortcuts_container),
                icon = { m -> AureoleDS.icons.MenuIcon(m) },
                onClick = { onSelectType(LauncherItemType.SHORTCUTS_CONTAINER) }
            )
        }
        AddContainerOptionItem(
            title = stringResource(R.string.home_container_add_widget_list),
            icon = { m -> AureoleDS.icons.Add(m) },
            onClick = { onSelectType(LauncherItemType.WIDGET_LIST) }
        )
        AddContainerOptionItem(
            title = stringResource(R.string.home_container_add_quick_button),
            icon = { m -> AureoleDS.icons.Click(m) },
            onClick = { onSelectType(LauncherItemType.QUICK_BUTTON) }
        )
        if (!isNested) {
            AddContainerOptionItem(
                title = stringResource(R.string.home_container_add_scroll_view),
                icon = { m -> AureoleDS.icons.VerticalContainer(m) },
                onClick = { onSelectType(LauncherItemType.SCROLL_VIEW) }
            )
        }
    }
}

@Composable
private fun AddContainerOptionItem(
    title: String,
    icon: @Composable (Modifier) -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AddContainerDialogMetrics.ItemCornerRadius))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.45f))
            .clickable { onClick() }
            .padding(horizontal = AureoleDS.dimens.small, vertical = AureoleDS.dimens.smallCompact),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(AureoleDS.dimens.iconXLarge)
                .clip(RoundedCornerShape(AddContainerDialogMetrics.IconBadgeCornerRadius))
                .background(AureoleDS.colors.surface.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            icon(Modifier.size(AureoleDS.dimens.iconMedium))
        }
        Spacer(modifier = Modifier.width(AureoleDS.dimens.small))
        AureoleText(
            text = title,
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        )
    }
}

@AureolePreview
@Composable
private fun AddContainerDialogPreview() {
    AureoleLauncherTheme {
        AddContainerDialog(
            onDismissRequest = {},
            onSelectType = {}
        )
    }
}
