package dev.mnascimentos.aureole.feature.home.components.overlays

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.core.designsystem.icons.AllApps
import dev.mnascimentos.aureole.core.designsystem.icons.Calendar
import dev.mnascimentos.aureole.core.designsystem.icons.Click
import dev.mnascimentos.aureole.core.designsystem.icons.ColorFill
import dev.mnascimentos.aureole.core.designsystem.icons.Favorite
import dev.mnascimentos.aureole.core.designsystem.icons.Layout
import dev.mnascimentos.aureole.core.designsystem.icons.MultipleView
import dev.mnascimentos.aureole.core.designsystem.icons.Plus
import dev.mnascimentos.aureole.core.designsystem.icons.ScrollGroup
import dev.mnascimentos.aureole.core.designsystem.icons.Text
import dev.mnascimentos.aureole.core.designsystem.icons.VerticalContainer
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.overlays.model.CustomizeAddItem
import dev.mnascimentos.aureole.feature.home.components.overlays.model.CustomizeAdjustItem

/** Icon of an "Add to home" grid entry, from the design system catalog. */
@Composable
internal fun CustomizeItemIcon(item: CustomizeAddItem, tint: Color, modifier: Modifier = Modifier) {
    val icons = AureoleDS.icons
    when (item) {
        CustomizeAddItem.CLOCK -> icons.Calendar(modifier, tint)
        CustomizeAddItem.FAVORITES -> icons.Favorite(modifier, tint)
        CustomizeAddItem.ALL_APPS -> icons.AllApps(modifier, tint)
        CustomizeAddItem.CONTAINER -> icons.VerticalContainer(modifier, tint)
        CustomizeAddItem.WIDGET_GROUP -> icons.MultipleView(modifier, tint)
        CustomizeAddItem.SCROLL_GROUP -> icons.ScrollGroup(modifier, tint)
        CustomizeAddItem.QUICK_BUTTON -> icons.Click(modifier, tint)
        CustomizeAddItem.WIDGET -> icons.Plus(modifier, tint)
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
