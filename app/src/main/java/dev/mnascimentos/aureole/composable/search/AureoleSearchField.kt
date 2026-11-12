package dev.mnascimentos.aureole.composable.search

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.Search
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Single-line search field meant to sit above the keyboard. The keyboard action is Search and it calls
 * [onSearch], which the screens use to pick the best result.
 */
@Composable
fun AureoleSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { AureoleText(placeholder, color = AureoleDS.colors.onSurfaceLow) },
        leadingIcon = { AureoleDS.icons.Search(tint = AureoleDS.colors.onSurfaceMedium) },
        singleLine = true,
        shape = RoundedCornerShape(AureoleDS.dimens.radiusMedium),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AureoleDS.colors.onSurfaceHigh,
            unfocusedBorderColor = AureoleDS.colors.outline,
            focusedTextColor = AureoleDS.colors.onSurfaceHigh,
            unfocusedTextColor = AureoleDS.colors.onSurfaceHigh
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@AureolePreview
@Composable
private fun AureoleSearchFieldEmptyPreview() {
    AureoleLauncherTheme {
        AureoleSearchField(query = "", onQueryChange = {}, placeholder = "Search app", onSearch = {})
    }
}

@AureolePreview
@Composable
private fun AureoleSearchFieldFilledPreview() {
    AureoleLauncherTheme {
        AureoleSearchField(query = "cam", onQueryChange = {}, placeholder = "Search app", onSearch = {})
    }
}
