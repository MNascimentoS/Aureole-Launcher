package dev.mnascimentos.aureole.composable.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.dialogs.model.FolderFormDraft
import dev.mnascimentos.aureole.composable.dialogs.model.effectiveEmoji
import dev.mnascimentos.aureole.composable.dialogs.model.effectiveIcon
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Close
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val NAME_PILL_WIDTH_FRACTION = 0.72f
private const val NAME_PILL_BACKGROUND_ALPHA = 0.5f

/**
 * The folder as it will look on the Home, above the sheet: its tile (own color, icon / letter / emoji with an
 * automatic contrast) and, under it, the name pill where the name is typed (pasta-form RF-08). [label] is the name
 * that will be saved (the placeholder when the typed one is blank).
 */
@Composable
fun FolderFormGlyphCard(
    draft: FolderFormDraft,
    label: String,
    onNameChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    autoFocus: Boolean = false
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
    ) {
        FolderPreviewTile(draft = draft, label = label)
        FolderNamePill(value = draft.name, onValueChange = onNameChange, autoFocus = autoFocus)
    }
}

@Composable
private fun FolderPreviewTile(draft: FolderFormDraft, label: String) {
    // A throwaway folder with the draft's look, so the tile is drawn exactly like the one on the Home.
    val folder = AppFolder(
        name = label,
        icon = draft.effectiveIcon(),
        iconType = draft.iconType,
        emoji = draft.effectiveEmoji(),
        color = draft.color
    )
    Box(
        modifier = Modifier
            .size(AureoleDS.dimens.sizeLarge)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMediumLarge))
            .background(folderOwnColor(folder) ?: AureoleDS.colors.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        FolderGlyph(
            folder = folder,
            size = AureoleDS.dimens.iconXLargeMedium,
            tint = AureoleDS.colors.onSurfaceHigh,
            letterStyle = AureoleDS.typography.titleLarge
        )
    }
}

/**
 * Name field in a pill: light background, thin border and a round pencil button at the end. Focused, the border
 * lights up and the pencil becomes a "×" that clears the name; empty, it shows "Name it". [autoFocus] focuses it
 * and opens the keyboard as soon as it appears (a new folder).
 */
@Composable
fun FolderNamePill(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    autoFocus: Boolean = false
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    var isFocused by remember { mutableStateOf(false) }
    val hint = stringResource(R.string.home_folder_name_hint)
    val description = stringResource(R.string.home_folder_name_a11y, value.ifBlank { hint })
    LaunchedEffect(autoFocus) {
        if (autoFocus) {
            focusRequester.requestFocus()
            keyboard?.show()
        }
    }
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = AureoleDS.typography.titleMedium.copy(color = AureoleDS.colors.onSurfaceHigh),
        cursorBrush = SolidColor(AureoleDS.colors.onSurfaceHigh),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        modifier = modifier
            .fillMaxWidth(NAME_PILL_WIDTH_FRACTION)
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused }
            .semantics { contentDescription = description },
        decorationBox = { innerTextField ->
            NamePillDecoration(
                value = value,
                isFocused = isFocused,
                hint = hint,
                onButtonClick = {
                    if (isFocused && value.isNotEmpty()) {
                        onValueChange("")
                    } else {
                        focusRequester.requestFocus()
                        keyboard?.show()
                    }
                },
                innerTextField = innerTextField
            )
        }
    )
}

/** The pill around the text field of [FolderNamePill]: hint when empty, border that lights up and the pencil / "×". */
@Composable
private fun NamePillDecoration(
    value: String,
    isFocused: Boolean,
    hint: String,
    onButtonClick: () -> Unit,
    innerTextField: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(AureoleDS.dimens.radiusFull)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = AureoleDS.dimens.minTouchTarget)
            .clip(shape)
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = NAME_PILL_BACKGROUND_ALPHA))
            .border(
                width = AureoleDS.dimens.borderMinimal,
                color = if (isFocused) AureoleDS.colors.onSurfaceHigh else AureoleDS.colors.outline,
                shape = shape
            )
            .padding(start = AureoleDS.dimens.medium, end = AureoleDS.dimens.xxSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (value.isEmpty()) {
                AureoleText(
                    text = hint,
                    color = AureoleDS.colors.onSurfaceLow,
                    format = AureoleTextFormatParams(
                        style = AureoleDS.typography.titleMedium,
                        textAlign = TextAlign.Start,
                        maxLines = 1
                    )
                )
            }
            innerTextField()
        }
        NamePillButton(isClear = isFocused && value.isNotEmpty(), onClick = onButtonClick)
    }
}

/** The round button at the end of the name pill: a pencil (edit) or, while typing, a "×" (clear). */
@Composable
private fun NamePillButton(isClear: Boolean, onClick: () -> Unit) {
    val label = stringResource(if (isClear) R.string.home_folder_name_clear else R.string.home_folder_name_edit)
    Box(
        modifier = Modifier
            .size(AureoleDS.dimens.xxLarge)
            .clip(CircleShape)
            .background(AureoleDS.colors.surfaceVariant)
            .clickable(role = Role.Button, onClickLabel = label, onClick = onClick)
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center
    ) {
        val iconModifier = Modifier.size(AureoleDS.dimens.iconSmall)
        if (isClear) {
            AureoleDS.icons.Close(iconModifier, AureoleDS.colors.onSurfaceHigh)
        } else {
            AureoleDS.icons.Edit(iconModifier, AureoleDS.colors.onSurfaceHigh)
        }
    }
}

@AureolePreview
@Composable
private fun FolderFormPreviewPreview() {
    AureoleLauncherTheme {
        FolderFormGlyphCard(draft = FolderFormDraft(name = "Bank"), label = "Bank", onNameChange = {})
    }
}

@AureolePreview
@Composable
private fun FolderNamePillEmptyPreview() {
    AureoleLauncherTheme {
        FolderNamePill(value = "", onValueChange = {})
    }
}
