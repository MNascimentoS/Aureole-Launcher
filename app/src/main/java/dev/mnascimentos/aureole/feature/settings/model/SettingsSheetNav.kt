package dev.mnascimentos.aureole.feature.settings.model

import androidx.annotation.StringRes
import androidx.compose.runtime.saveable.Saver
import dev.mnascimentos.aureole.R

/** The three areas of the settings root, each opened as a bottom sheet over it. */
enum class SettingsArea { APPEARANCE, BEHAVIOR, ADVANCED }

/** Sheets an area opens in its place: the area sheet goes down, this one comes up, and back. */
enum class SettingsChildKind { BACKGROUND, CARDS_GLASS, RELEASE_NOTES, CONTRIBUTORS }

/**
 * Pickers the Appearance sheet shows inside itself (D-09): the content of the sheet slides to the picker and the
 * same preview above it shows the choice live.
 */
enum class SettingsInnerKind { PALETTE, FONT }

/** The sheet on top of the settings root; none (null) is the root itself. */
sealed interface SettingsSheet {
    /** An area sheet (Appearance, Behavior or Advanced), showing its list or, with [inner], a picker inside it. */
    data class Area(val area: SettingsArea, val inner: SettingsInnerKind? = null) : SettingsSheet

    /** A sheet opened from [returnTo], which comes back when it closes; null goes back to the root. */
    data class Child(val kind: SettingsChildKind, val returnTo: SettingsArea?) : SettingsSheet
}

/** What happens to the sheets of the settings root. */
sealed interface SettingsSheetEvent {
    /** A root card was tapped. */
    data class OpenArea(val area: SettingsArea) : SettingsSheetEvent

    /** A row of the open area asked for another sheet: it swaps in place of the area. */
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
    is SettingsSheetEvent.OpenChild -> SettingsSheet.Child(event.kind, returnTo = state.area)
    is SettingsSheetEvent.OpenInner ->
        if (state is SettingsSheet.Area && settingsAreaHasInner(state.area)) state.copy(inner = event.kind) else state
    is SettingsSheetEvent.CloseChild ->
        if (state is SettingsSheet.Child && state.kind == event.kind) state.backToArea() else state
    is SettingsSheetEvent.CloseArea ->
        if (state is SettingsSheet.Area && state.area == event.area) null else state
    SettingsSheetEvent.Back -> when (state) {
        is SettingsSheet.Child -> state.backToArea()
        is SettingsSheet.Area -> if (state.inner != null) state.copy(inner = null) else null
        null -> null
    }
}

private fun SettingsSheet.Child.backToArea(): SettingsSheet? = returnTo?.let { SettingsSheet.Area(it) }

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

private const val AREA_TAG = "area"
private const val CHILD_TAG = "child"
private const val SEPARATOR = ":"

/** The sheet as text, to survive a rotation or a trip to the licenses screen; null for the root. */
fun encodeSettingsSheet(sheet: SettingsSheet?): String? = when (sheet) {
    is SettingsSheet.Area -> listOfNotNull(AREA_TAG, sheet.area.name, sheet.inner?.name).joinToString(SEPARATOR)
    is SettingsSheet.Child -> listOf(CHILD_TAG, sheet.kind.name, sheet.returnTo?.name.orEmpty()).joinToString(SEPARATOR)
    null -> null
}

/** Reads back [encodeSettingsSheet]; anything it does not know is the root. */
fun decodeSettingsSheet(text: String?): SettingsSheet? {
    val parts = text?.split(SEPARATOR) ?: return null
    return when (parts.firstOrNull()) {
        AREA_TAG -> parts.getOrNull(1)?.let(::areaNamed)?.let { area ->
            val inner = parts.getOrNull(2)?.let(::innerNamed)?.takeIf { settingsAreaHasInner(area) }
            SettingsSheet.Area(area, inner)
        }
        CHILD_TAG -> parts.getOrNull(1)?.let(::childNamed)?.let { kind ->
            SettingsSheet.Child(kind, returnTo = parts.getOrNull(2)?.let(::areaNamed))
        }
        else -> null
    }
}

private fun areaNamed(name: String): SettingsArea? = SettingsArea.entries.firstOrNull { it.name == name }

private fun childNamed(name: String): SettingsChildKind? = SettingsChildKind.entries.firstOrNull { it.name == name }

private fun innerNamed(name: String): SettingsInnerKind? = SettingsInnerKind.entries.firstOrNull { it.name == name }

/** Saves the sheet in the saved state of the settings root. */
val SettingsSheetSaver: Saver<SettingsSheet?, String> = Saver(
    save = { encodeSettingsSheet(it) },
    restore = { decodeSettingsSheet(it) }
)
