package dev.mnascimentos.aureole.feature.home.components.appsdrawer.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowMetrics

object AppsListMetrics {
    val SettingsBottomSpacerHeight: Dp = 80.dp

    /** How much bigger the "Aureole settings" circle is than an app icon (RF-B1). */
    private val SettingsIconGrowth: Dp = 8.dp

    /** Diameter of the "Aureole settings" circle: the app icon size plus [SettingsIconGrowth]. */
    val SettingsIconSize: Dp = AppItemRowMetrics.IconOuterSize + SettingsIconGrowth

    /** The logo keeps the same fraction of the circle it had in the app icon size. */
    val SettingsGlyphSize: Dp =
        SettingsIconSize * (AppItemRowMetrics.IconInnerSize.value / AppItemRowMetrics.IconOuterSize.value)

    /** Half of the growth: the circle sticks out this much on each side, so the label stays aligned with the apps. */
    val SettingsIconOverflow: Dp = SettingsIconGrowth / 2

    /** Extra space above the divider, on top of the divider's own padding: 32 dp from the last app (RF-B2). */
    val DividerExtraTop: Dp = 24.dp

    /** Extra space below the divider, on top of the divider's own padding: 16 dp to the item (RF-B2). */
    val DividerExtraBottom: Dp = 8.dp
}
