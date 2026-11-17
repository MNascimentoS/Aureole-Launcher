package dev.mnascimentos.aureole.feature.settings.model

import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R

/** The three areas of the settings root, each opened as a bottom sheet over it. */
enum class SettingsArea { APPEARANCE, BEHAVIOR, ADVANCED }

/**
 * Sheets an area opens in its place: the area sheet goes down, this one comes up, and back. The photo editor opens
 * from the background sheet in its place, and gives it back on close.
 */
enum class SettingsChildKind { BACKGROUND, CARDS_GLASS, RELEASE_NOTES, CONTRIBUTORS, WALLPAPER_EDITOR }

/**
 * Pickers the Appearance sheet shows inside itself (D-09): the content of the sheet slides to the picker and the
 * same preview above it shows the choice live.
 */
enum class SettingsInnerKind { PALETTE, FONT }

/** The sheet on top of the settings root; none (null) is the root itself. */
sealed interface SettingsSheet {
    /** An area sheet (Appearance, Behavior or Advanced), showing its list or, with [inner], a picker inside it. */
    data class Area(val area: SettingsArea, val inner: SettingsInnerKind? = null) : SettingsSheet

    /**
     * A sheet opened from [returnTo], which comes back when it closes; null goes back to the root. One opened from
     * another child sheet ([parent]) gives that one back first.
     */
    data class Child(
        val kind: SettingsChildKind,
        val returnTo: SettingsArea?,
        val parent: SettingsChildKind? = null
    ) : SettingsSheet
}

/** What happens to the sheets of the settings root. */
sealed interface SettingsSheetEvent {
    /** A root card was tapped. */
    data class OpenArea(val area: SettingsArea) : SettingsSheetEvent

    /** A row of the open sheet asked for another sheet: it swaps in place of the area (or of the child sheet). */
    data class OpenChild(val kind: SettingsChildKind) : SettingsSheetEvent

    /** A row of the open area asked for a picker inside the same sheet (only Appearance has them). */
    data class OpenInner(val kind: SettingsInnerKind) : SettingsSheetEvent

    /** The child sheet [kind] finished closing (saved, if it saves). */
    data class CloseChild(val kind: SettingsChildKind) : SettingsSheetEvent

    /** The area sheet [area] finished closing (drag down, tap outside or back). */
    data class CloseArea(val area: SettingsArea) : SettingsSheetEvent

    /** The system back: a picker goes back to the list of its area, a child to its area, an area to the root. */
    data object Back : SettingsSheetEvent
}

/** The area under the current sheet: the open area, or the one a child returns to. */
val SettingsSheet?.area: SettingsArea?
    get() = when (this) {
        is SettingsSheet.Area -> area
        is SettingsSheet.Child -> returnTo
        null -> null
    }

/**
 * Next sheet after [event]. Close events name the sheet that closed, so a late one (the sheet already left, or
 * another took its place) changes nothing.
 */
fun reduceSettingsSheet(state: SettingsSheet?, event: SettingsSheetEvent): SettingsSheet? = when (event) {
    is SettingsSheetEvent.OpenArea -> SettingsSheet.Area(event.area)
    is SettingsSheetEvent.OpenChild -> state.withChild(event.kind)
    is SettingsSheetEvent.OpenInner -> state.withInner(event.kind)
    is SettingsSheetEvent.CloseChild -> state.afterChildClosed(event.kind)
    is SettingsSheetEvent.CloseArea -> state.afterAreaClosed(event.area)
    SettingsSheetEvent.Back -> state.afterBack()
}

private fun SettingsSheet?.withChild(kind: SettingsChildKind): SettingsSheet {
    if (this is SettingsSheet.Child && this.kind == kind) return this
    val parent = (this as? SettingsSheet.Child)?.kind?.takeIf { settingsChildReturnsToParent(kind) }
    return SettingsSheet.Child(kind, returnTo = area, parent = parent)
}

/** Only the photo editor gives back the sheet it opened from; any other child gives back the area. */
fun settingsChildReturnsToParent(kind: SettingsChildKind): Boolean = kind == SettingsChildKind.WALLPAPER_EDITOR

private fun SettingsSheet?.withInner(kind: SettingsInnerKind): SettingsSheet? =
    if (this is SettingsSheet.Area && settingsAreaHasInner(area)) copy(inner = kind) else this

private fun SettingsSheet?.afterChildClosed(kind: SettingsChildKind): SettingsSheet? =
    if (this is SettingsSheet.Child && this.kind == kind) backToArea() else this

private fun SettingsSheet?.afterAreaClosed(closed: SettingsArea): SettingsSheet? =
    if (this is SettingsSheet.Area && area == closed) null else this

private fun SettingsSheet?.afterBack(): SettingsSheet? = when (this) {
    is SettingsSheet.Child -> backToArea()
    is SettingsSheet.Area -> if (inner != null) copy(inner = null) else null
    null -> null
}

private fun SettingsSheet.Child.backToArea(): SettingsSheet? =
    parent?.let { SettingsSheet.Child(it, returnTo) } ?: returnTo?.let { SettingsSheet.Area(it) }

/** Title of the area sheet. */
@StringRes
fun settingsAreaTitle(area: SettingsArea): Int = when (area) {
    SettingsArea.APPEARANCE -> R.string.settings_appearance_title
    SettingsArea.BEHAVIOR -> R.string.settings_behavior_title
    SettingsArea.ADVANCED -> R.string.settings_advanced_title
}

/**
 * Only the Appearance sheet has pickers inside it (D-09) and the mini-home preview above it (D-04), which shows what
 * those pickers choose.
 */
fun settingsAreaHasInner(area: SettingsArea): Boolean = area == SettingsArea.APPEARANCE

/** Title of the sheet: the picker shown inside it, or the area. */
@StringRes
fun settingsSheetTitle(area: SettingsArea, inner: SettingsInnerKind?): Int = when (inner) {
    SettingsInnerKind.PALETTE -> R.string.settings_appearance_color_palette
    SettingsInnerKind.FONT -> R.string.home_customize_adjust_font
    null -> settingsAreaTitle(area)
}
