package dev.mnascimentos.aureole.composable.dialogs.model

import java.text.BreakIterator

/** Columns of the emoji grid of the folder form. */
const val FOLDER_EMOJI_COLUMNS = 6

/** Common emojis offered for a folder; the "+" tile after them opens the keyboard for any other one. */
val FOLDER_EMOJIS: List<String> = listOf(
    "💰", // money bag
    "🏦", // bank
    "💳", // credit card
    "📈", // chart
    "🎮", // video game
    "🎧", // headphones
    "📚", // books
    "✈️", // airplane
    "🏥", // hospital
    "🛠️", // tools
    "💼" // briefcase
)

/**
 * The emojis the grid lists: [FOLDER_EMOJIS], with [current] in front when it is one typed through the keyboard
 * (the grid keeps its size, so the last common one leaves).
 */
fun folderEmojiChoices(current: String?): List<String> =
    if (current.isNullOrBlank() || current in FOLDER_EMOJIS) {
        FOLDER_EMOJIS
    } else {
        listOf(current) + FOLDER_EMOJIS.dropLast(1)
    }

/**
 * The emoji to keep from what was typed in the emoji field: its last visible character (a whole emoji, skin tone
 * and joined sequences included), or null when nothing but spaces was typed.
 */
fun lastGrapheme(text: String): String? {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) return null
    val iterator = BreakIterator.getCharacterInstance().apply { setText(trimmed) }
    val end = iterator.last()
    val start = iterator.previous()
    return trimmed.substring(start, end)
}
