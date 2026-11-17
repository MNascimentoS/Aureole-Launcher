package dev.mnascimentos.aureole.composable.dialogs.model

import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.FolderDetails
import dev.mnascimentos.aureole.core.data.model.FolderIconType
import dev.mnascimentos.aureole.core.data.model.details
import dev.mnascimentos.aureole.core.designsystem.icons.AureoleIconCatalog
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import java.text.BreakIterator

object FolderButtonMetrics {
    val SmallSize = 52.dp
    val MediumSize = 60.dp
    val LabelMaxWidth = 56.dp
    val ContainerMaxWidth = 68.dp
    val GridGap = 18.dp
}

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

/** What the sheet of the folder form shows: the form itself or, in its place, the grid of every icon. */
enum class FolderFormMode { FORM, ALL_ICONS }

/**
 * What the folder form holds while it is open. A null [icon] follows the first suggestion for the name (a new
 * folder); a null [emoji] shows the first of [FOLDER_EMOJIS]; a null [color] uses the color of the container.
 */
data class FolderFormDraft(
    val name: String,
    val iconType: FolderIconType = FolderIconType.ICON,
    val icon: String? = null,
    val emoji: String? = null,
    val color: ThemeColorRef? = null,
)

/**
 * Draft the form opens with: the details of [folder], or an empty new folder. A saved folder of the icon kind
 * without an icon shows its letter on the home, so it opens on Letter.
 */
fun initialFolderDraft(folder: AppFolder?): FolderFormDraft = folder?.details()?.let {
    val type = if (it.iconType == FolderIconType.ICON && it.icon == null) FolderIconType.LETTER else it.iconType
    FolderFormDraft(name = it.name, iconType = type, icon = it.icon, emoji = it.emoji, color = it.color)
} ?: FolderFormDraft(name = "")

/** Name that is saved and shown in the preview: the typed text, trimmed, or [placeholder] when it is blank. */
fun resolveFolderName(raw: String, placeholder: String): String = raw.ifBlank { placeholder }.trim()

/** Icon the draft stands for: the one chosen, else the first suggestion for its name. */
fun FolderFormDraft.effectiveIcon(): String = icon ?: folderIconSuggestions(name).first()

/** Emoji the draft stands for: the one chosen, else the first common one. */
fun FolderFormDraft.effectiveEmoji(): String = emoji ?: FOLDER_EMOJIS.first()

/**
 * What saving the draft writes. Only the kind in use is pinned (a new folder saves the suggested icon); an emoji
 * chosen earlier is kept even when the folder now shows its icon or letter.
 */
fun FolderFormDraft.toDetails(placeholder: String): FolderDetails = FolderDetails(
    name = resolveFolderName(name, placeholder),
    icon = if (iconType == FolderIconType.ICON) effectiveIcon() else icon,
    iconType = iconType,
    emoji = if (iconType == FolderIconType.EMOJI) effectiveEmoji() else emoji,
    color = color
)

/** First letter of [label] in upper case, or "A" when it is empty. */
fun folderLetter(label: String): String = label.trim().take(1).uppercase().ifEmpty { "A" }

/** Tapping the marked color again clears it (back to the container color); any other color is chosen. */
fun toggleFolderColor(current: ThemeColorRef?, tapped: ThemeColorRef): ThemeColorRef? =
    if (current == tapped) null else tapped

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

/** True when the tile of [key] is the icon [selected] stands for (old keys included). */
fun isFolderIconMarked(key: String, selected: String?): Boolean =
    selected != null && (AureoleIconCatalog.resolveKey(selected) ?: selected) == key

/** Index of [type] in the Icon | Letter | Emoji segmented control, and back. */
fun folderIconTypeIndex(type: FolderIconType): Int = FolderIconType.entries.indexOf(type)

fun folderIconTypeAt(index: Int): FolderIconType = FolderIconType.entries.getOrElse(index) { FolderIconType.ICON }

/** What the sheet body of the folder form draws. */
data class FolderFormBodyState(
    val draft: FolderFormDraft,
    val showDelete: Boolean
)

/** Callbacks of the sheet body of the folder form. */
data class FolderFormBodyActions(
    val onDraftChange: (FolderFormDraft) -> Unit,
    val onOpenAllIcons: () -> Unit,
    val onDelete: (() -> Unit)?
)
