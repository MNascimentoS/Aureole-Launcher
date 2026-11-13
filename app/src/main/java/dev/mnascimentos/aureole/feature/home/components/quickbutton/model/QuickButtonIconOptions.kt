package dev.mnascimentos.aureole.feature.home.components.quickbutton.model

import dev.mnascimentos.aureole.core.data.model.QuickButtonIcon

/**
 * Icons the appearance grid offers: one per drawable. [QuickButtonIcon.CREATE] and [QuickButtonIcon.EDIT] share a
 * drawable and the enum names are persisted, so both stay in the enum and the grid lists only the first one.
 */
fun quickButtonPickerIcons(): List<QuickButtonIcon> = QuickButtonIcon.entries.distinctBy { it.iconRes }

/** Whether [icon] is the one drawn by [selected], so a saved alias still highlights its tile in the grid. */
fun isSameQuickButtonIcon(icon: QuickButtonIcon, selected: QuickButtonIcon): Boolean = icon.iconRes == selected.iconRes
