package dev.mnascimentos.aureole.core.data.model

import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRole

/** "Theme" of the group background: the theme surface, the look the old "Background enabled" switch gave. */
val CONTAINER_BACKGROUND_THEME_REF: ThemeColorRef = ThemeColorRef.Role(ThemeColorRole.SURFACE)

/** "Theme" of the background of each item: the theme variant every item had before (D-12, D-16). */
val CONTAINER_ITEM_THEME_REF: ThemeColorRef = ThemeColorRef.Role(ThemeColorRole.VARIANT)

/** The two colors of a group: its [background] and the [item] background; null is "None". */
data class ContainerColorRefs(val background: ThemeColorRef?, val item: ThemeColorRef?)

/**
 * The colors an existing group gets when its old switch becomes colors (RF-E3): background off is "None", on is
 * its old fixed color when it had one ([legacyBackgroundArgb], never drawn before but saved) or "Theme"; the items
 * keep the theme variant they always had.
 */
fun migratedContainerColors(isBackgroundEnabled: Boolean, legacyBackgroundArgb: Long?): ContainerColorRefs {
    val background = when {
        !isBackgroundEnabled -> null
        legacyBackgroundArgb != null && legacyBackgroundArgb != 0L -> ThemeColorRef.Fixed(legacyBackgroundArgb.toInt())
        else -> CONTAINER_BACKGROUND_THEME_REF
    }
    return ContainerColorRefs(background = background, item = CONTAINER_ITEM_THEME_REF)
}

/** The colors a new group starts with (D-12): background "Theme" when [hasBackground], items "Theme". */
fun newContainerColors(hasBackground: Boolean): ContainerColorRefs = ContainerColorRefs(
    background = if (hasBackground) CONTAINER_BACKGROUND_THEME_REF else null,
    item = CONTAINER_ITEM_THEME_REF
)
