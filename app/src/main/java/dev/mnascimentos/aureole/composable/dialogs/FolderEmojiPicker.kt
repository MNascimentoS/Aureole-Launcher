package dev.mnascimentos.aureole.composable.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.dialogs.model.FOLDER_EMOJIS
import dev.mnascimentos.aureole.composable.dialogs.model.FOLDER_EMOJI_COLUMNS
import dev.mnascimentos.aureole.composable.dialogs.model.lastGrapheme
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Plus
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Short grid of common emojis ([FOLDER_EMOJI_COLUMNS] per row) with a "+" tile at the end; [marked] is framed.
 * "+" opens a small field with the keyboard, where any emoji can be typed (the system emoji panel); the last one
 * typed is picked and the field closes.
 */
@Composable
internal fun FolderEmojiGrid(choices: List<String>, marked: String, onPick: (String) -> Unit) {
    var isTyping by remember { mutableStateOf(false) }
    val cells: List<String?> = choices + listOf(null)
    Column(verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)) {
        cells.chunked(FOLDER_EMOJI_COLUMNS).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)) {
                row.forEach { emoji ->
                    if (emoji == null) {
                        MoreEmojiTile(isActive = isTyping, onClick = { isTyping = !isTyping })
                    } else {
                        FolderEmojiTile(emoji = emoji, isSelected = emoji == marked, onClick = { onPick(emoji) })
                    }
                }
                // Keeps the tiles of a short last row as wide as the others.
                repeat(FOLDER_EMOJI_COLUMNS - row.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
        if (isTyping) {
            FolderEmojiField(
                onEmoji = { emoji ->
                    onPick(emoji)
                    isTyping = false
                }
            )
        }
    }
}

@Composable
private fun RowScope.MoreEmojiTile(isActive: Boolean, onClick: () -> Unit) {
    val label = stringResource(R.string.home_folder_emoji_more)
    val shape = RoundedCornerShape(AureoleDS.dimens.radiusMedium)
    Box(
        modifier = Modifier
            .weight(1f)
            .height(AureoleDS.dimens.minTouchTarget)
            .clip(shape)
            .background(AureoleDS.colors.surfaceVariant)
            .border(
                width = AureoleDS.dimens.borderMinimal,
                color = if (isActive) AureoleDS.colors.onSurfaceHigh else AureoleDS.colors.outline,
                shape = shape
            )
            .clickable(role = Role.Button, onClickLabel = label, onClick = onClick)
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center
    ) {
        AureoleDS.icons.Plus(Modifier.size(AureoleDS.dimens.iconMedium), AureoleDS.colors.onSurfaceHigh)
    }
}

/** Field that opens focused, with the keyboard; the last character typed (a whole emoji) goes to [onEmoji]. */
@Composable
private fun FolderEmojiField(onEmoji: (String) -> Unit) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    var text by remember { mutableStateOf("") }
    val hint = stringResource(R.string.home_folder_emoji_hint)
    val shape = RoundedCornerShape(AureoleDS.dimens.radiusMedium)
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboard?.show()
    }
    BasicTextField(
        value = text,
        onValueChange = { typed ->
            val emoji = lastGrapheme(typed)
            if (emoji == null) {
                text = typed
            } else {
                text = ""
                focusManager.clearFocus()
                onEmoji(emoji)
            }
        },
        singleLine = true,
        textStyle = AureoleDS.typography.titleMedium.copy(color = AureoleDS.colors.onSurfaceHigh),
        cursorBrush = SolidColor(AureoleDS.colors.onSurfaceHigh),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .semantics { contentDescription = hint },
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = AureoleDS.dimens.minTouchTarget)
                    .clip(shape)
                    .border(AureoleDS.dimens.borderMinimal, AureoleDS.colors.onSurfaceHigh, shape)
                    .padding(horizontal = AureoleDS.dimens.medium),
                contentAlignment = Alignment.CenterStart
            ) {
                if (text.isEmpty()) {
                    AureoleText(
                        text = hint,
                        color = AureoleDS.colors.onSurfaceLow,
                        format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium, maxLines = 1)
                    )
                }
                innerTextField()
            }
        }
    )
}

@AureolePreview
@Composable
private fun FolderEmojiGridPreview() {
    AureoleLauncherTheme {
        FolderEmojiGrid(choices = FOLDER_EMOJIS, marked = FOLDER_EMOJIS.first(), onPick = {})
    }
}
