package dev.mnascimentos.aureole.feature.settings.model

import androidx.compose.runtime.saveable.Saver

private const val AREA_TAG = "area"
private const val CHILD_TAG = "child"
private const val SEPARATOR = ":"

/** The sheet as text, to survive a rotation or a trip to the licenses screen; null for the root. */
fun encodeSettingsSheet(sheet: SettingsSheet?): String? = when (sheet) {
    is SettingsSheet.Area -> listOfNotNull(AREA_TAG, sheet.area.name, sheet.inner?.name).joinToString(SEPARATOR)
    is SettingsSheet.Child -> listOfNotNull(
        CHILD_TAG,
        sheet.kind.name,
        sheet.returnTo?.name.orEmpty(),
        sheet.parent?.name
    ).joinToString(SEPARATOR)
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
            SettingsSheet.Child(
                kind,
                returnTo = parts.getOrNull(2)?.let(::areaNamed),
                parent = parts.getOrNull(3)?.let(::childNamed)
            )
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
