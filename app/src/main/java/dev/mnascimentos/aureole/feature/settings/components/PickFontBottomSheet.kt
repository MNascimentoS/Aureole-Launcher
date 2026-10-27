package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.sheets.SettingsBottomSheet
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Check
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AvailableFonts
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@Composable
fun PickFontBottomSheet(
    selectedFontName: String,
    onFontSelected: (String) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsBottomSheet(
        title = stringResource(R.string.settings_appearance_pick_font),
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = AvailableFonts.keys.toList(),
                key = { it },
                contentType = { "font_option" }
            ) { fontName ->
                FontOptionItem(
                    fontName = fontName,
                    isSelected = fontName == selectedFontName,
                    onFontSelected = onFontSelected
                )
            }
        }
    }
}

@Composable
private fun FontOptionItem(
    fontName: String,
    isSelected: Boolean,
    onFontSelected: (String) -> Unit
) {
    val font = AvailableFonts[fontName]
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onFontSelected(fontName) }
            .padding(vertical = AureoleDS.dimens.small, horizontal = AureoleDS.dimens.xSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleText(
            text = fontName,
            color = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.weight(1f),
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyLarge, fontFamily = font)
        )
        if (isSelected) {
            AureoleDS.icons.Check(
                tint = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.size(AureoleDS.dimens.iconLarge)
            )
        }
    }
}

@AureolePreview
@Composable
private fun PickFontBottomSheetPreview() {
    AureoleLauncherTheme {
        PickFontBottomSheet(
            selectedFontName = "Istok Web",
            onFontSelected = {},
            onDismissRequest = {}
        )
    }
}
