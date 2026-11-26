package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.color.AureoleColorPickerDialog
import dev.mnascimentos.aureole.composable.color.model.isLightBackground
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorOptions
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * The solid-color picker of the background sheet (relogio-seletor-tela RF-02): the card-grid picker of the "Card
 * color" reference, opened by the "Solid color" card, with the [selected] color marked and each choice sent to
 * [onSelect] at once.
 */
@Composable
internal fun BackgroundColorPicker(selected: Int, onSelect: (Int) -> Unit, onDismissRequest: () -> Unit) {
    val themeBackground = AureoleDS.colors.background.toArgb()
    AureoleColorPickerDialog(
        options = ComponentColorOptions(
            role = ComponentColorRole.CARD,
            title = stringResource(R.string.settings_background_color),
            subtitle = null,
            selected = ThemeColorRef.Fixed(selected),
            themeColor = AureoleDS.colors.background,
            onSelect = { onSelect((it as? ThemeColorRef.Fixed)?.argb ?: themeBackground) }
        ),
        onDismissRequest = onDismissRequest
    )
}

/** The short note under the type cards when the chosen [color] makes a light theme; nothing for a dark one. */
@Composable
internal fun BackgroundLightWarning(color: Int, modifier: Modifier = Modifier) {
    if (!isLightBackground(color)) return
    AureoleText(
        text = stringResource(R.string.settings_background_light_warning),
        color = AureoleDS.colors.onSurfaceLow,
        format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall),
        modifier = modifier.padding(top = AureoleDS.dimens.small)
    )
}

private const val PREVIEW_LIGHT_COLOR = 0xFFEDEDED.toInt()

@AureolePreview
@Composable
private fun BackgroundLightWarningPreview() {
    AureoleLauncherTheme {
        BackgroundLightWarning(color = PREVIEW_LIGHT_COLOR)
    }
}
