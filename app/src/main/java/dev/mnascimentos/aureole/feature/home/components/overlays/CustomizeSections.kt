package dev.mnascimentos.aureole.feature.home.components.overlays

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.menu.AureoleMenuSectionLabel
import dev.mnascimentos.aureole.composable.mirror.LeftHandMirror
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Check
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.overlays.model.CUSTOMIZE_ADD_COLUMNS
import dev.mnascimentos.aureole.feature.home.components.overlays.model.CustomizeAddItem
import dev.mnascimentos.aureole.feature.home.components.overlays.model.CustomizeAdjustItem

private object CustomizeMetrics {
    val AdjustHeight = 52.dp
    val TileHeight = 92.dp
    val IconTile = 40.dp
    val BadgeSize = 16.dp
    const val CARD_ALPHA = 0.5f
    const val DIMMED_ALPHA = 0.55f
}

/** "Adjust" section: Layout, Palette and Font side by side. Mirrored in the left-handed mode. */
@Composable
internal fun CustomizeAdjustSection(
    onAdjust: (CustomizeAdjustItem) -> Unit,
    isLeftHanded: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        AureoleMenuSectionLabel(text = stringResource(R.string.home_customize_section_adjust))
        LeftHandMirror(isLeftHanded) {
            Row(horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)) {
                CustomizeAdjustItem.entries.forEach { item ->
                    AdjustButton(item = item, onClick = { onAdjust(item) }, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun AdjustButton(item: CustomizeAdjustItem, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .heightIn(min = CustomizeMetrics.AdjustHeight)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = CustomizeMetrics.CARD_ALPHA))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = AureoleDS.dimens.xSmall),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomizeItemIcon(
            item = item,
            modifier = Modifier.size(AureoleDS.dimens.iconMedium),
            tint = AureoleDS.colors.onSurfaceMedium
        )
        Spacer(modifier = Modifier.padding(horizontal = AureoleDS.dimens.xxSmall))
        AureoleText(
            text = stringResource(item.labelRes),
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium, maxLines = 1)
        )
    }
}

/**
 * "Add to home" section: a grid of [CUSTOMIZE_ADD_COLUMNS] columns. Items in [onHome] are dimmed and carry a
 * check mark but stay tappable. Mirrored in the left-handed mode.
 */
@Composable
internal fun CustomizeAddSection(
    onHome: Set<CustomizeAddItem>,
    onAdd: (CustomizeAddItem) -> Unit,
    isLeftHanded: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        AureoleMenuSectionLabel(text = stringResource(R.string.home_customize_section_add))
        LeftHandMirror(isLeftHanded) {
            Column(verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)) {
                CustomizeAddItem.rows.forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)) {
                        row.forEach { item ->
                            AddTile(
                                item = item,
                                isOnHome = item in onHome,
                                onClick = { onAdd(item) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(CUSTOMIZE_ADD_COLUMNS - row.size) { Spacer(modifier = Modifier.weight(1f)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddTile(item: CustomizeAddItem, isOnHome: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val label = stringResource(item.labelRes)
    val onHomeLabel = stringResource(R.string.home_customize_item_on_home)
    Column(
        modifier = modifier
            .heightIn(min = CustomizeMetrics.TileHeight)
            .alpha(if (isOnHome) CustomizeMetrics.DIMMED_ALPHA else 1f)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = CustomizeMetrics.CARD_ALPHA))
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { if (isOnHome) contentDescription = "$label, $onHomeLabel" }
            .padding(AureoleDS.dimens.xSmall),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(CustomizeMetrics.IconTile)
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
                .background(AureoleDS.colors.background),
            contentAlignment = Alignment.Center
        ) {
            CustomizeItemIcon(
                item = item,
                modifier = Modifier.size(AureoleDS.dimens.iconLarge),
                tint = AureoleDS.colors.onSurfaceHigh
            )
            if (isOnHome) OnHomeBadge(modifier = Modifier.align(Alignment.TopEnd))
        }
        Spacer(modifier = Modifier.padding(top = AureoleDS.dimens.xxSmall))
        AureoleText(
            text = label,
            color = AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.labelSmall,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        )
    }
}

@Composable
private fun OnHomeBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(CustomizeMetrics.BadgeSize)
            .clip(CircleShape)
            .background(AureoleDS.colors.badgeContainer),
        contentAlignment = Alignment.Center
    ) {
        AureoleDS.icons.Check(modifier = Modifier.size(AureoleDS.dimens.iconXSmall), tint = AureoleDS.colors.onError)
    }
}

@AureolePreview
@Composable
private fun CustomizeSectionsPreview() {
    AureoleLauncherTheme {
        Column(modifier = Modifier.background(AureoleDS.colors.surface).padding(AureoleDS.dimens.medium)) {
            CustomizeAdjustSection(onAdjust = {}, isLeftHanded = false)
            CustomizeAddSection(
                onHome = setOf(CustomizeAddItem.CLOCK, CustomizeAddItem.ALL_APPS),
                onAdd = {},
                isLeftHanded = false
            )
        }
    }
}
