package dev.mnascimentos.aureole.feature.home.components.overlays

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.core.designsystem.icons.AddNew
import dev.mnascimentos.aureole.core.designsystem.icons.AllApps
import dev.mnascimentos.aureole.core.designsystem.icons.Calendar
import dev.mnascimentos.aureole.core.designsystem.icons.Click
import dev.mnascimentos.aureole.core.designsystem.icons.ColorFill
import dev.mnascimentos.aureole.core.designsystem.icons.Layout
import dev.mnascimentos.aureole.core.designsystem.icons.Logo
import dev.mnascimentos.aureole.core.designsystem.icons.MultipleView
import dev.mnascimentos.aureole.core.designsystem.icons.ScrollGroup
import dev.mnascimentos.aureole.core.designsystem.icons.Text
import dev.mnascimentos.aureole.core.designsystem.icons.VerticalContainer
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.overlays.model.CustomizeAddItem
import dev.mnascimentos.aureole.feature.home.components.overlays.model.CustomizeAdjustItem

private const val LOGO_ASPECT_RATIO = 128f / 73f

/**
 * Icon of an "Add to home" grid entry, from the design system catalog. The wide Aureole logo (Settings entry)
 * ignores the square [modifier] size and keeps its own proportion at the tile box width.
 */
@Composable
internal fun CustomizeItemIcon(item: CustomizeAddItem, tint: Color, modifier: Modifier = Modifier) {
    val icons = AureoleDS.icons
    when (item) {
        CustomizeAddItem.CLOCK -> icons.Calendar(modifier, tint)
        CustomizeAddItem.APPS_LIST -> icons.AllApps(modifier, tint)
        CustomizeAddItem.CONTAINER -> icons.VerticalContainer(modifier, tint)
        CustomizeAddItem.WIDGET_GROUP -> icons.MultipleView(modifier, tint)
        CustomizeAddItem.SCROLL_GROUP -> icons.ScrollGroup(modifier, tint)
        CustomizeAddItem.QUICK_BUTTON -> icons.Click(modifier, tint)
        CustomizeAddItem.WIDGET -> icons.AddNew(modifier, tint)
        CustomizeAddItem.SETTINGS -> icons.Logo(
            modifier = modifier
                .wrapContentSize(unbounded = true)
                .width(AureoleDS.dimens.iconHuge)
                .aspectRatio(LOGO_ASPECT_RATIO),
            tint = tint
        )
    }
}

/** Icon of an "Adjust" button. */
@Composable
internal fun CustomizeItemIcon(item: CustomizeAdjustItem, tint: Color, modifier: Modifier = Modifier) {
    val icons = AureoleDS.icons
    when (item) {
        CustomizeAdjustItem.LAYOUT -> icons.Layout(modifier, tint)
        CustomizeAdjustItem.PALETTE -> icons.ColorFill(modifier, tint)
        CustomizeAdjustItem.FONT -> icons.Text(modifier, tint)
    }
}

@AureolePreview
@Composable
private fun CustomizeItemIconsPreview() {
    AureoleLauncherTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small),
            modifier = Modifier.padding(AureoleDS.dimens.medium)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)) {
                CustomizeAddItem.entries.forEach { item ->
                    CustomizeItemIcon(
                        item = item,
                        modifier = Modifier.size(AureoleDS.dimens.iconMedium),
                        tint = AureoleDS.colors.onSurfaceHigh
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)) {
                CustomizeAdjustItem.entries.forEach { item ->
                    CustomizeItemIcon(
                        item = item,
                        modifier = Modifier.size(AureoleDS.dimens.iconMedium),
                        tint = AureoleDS.colors.onSurfaceHigh
                    )
                }
            }
        }
    }
}
