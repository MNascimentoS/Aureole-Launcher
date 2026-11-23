package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.menu.AureoleSwitchRow
import dev.mnascimentos.aureole.composable.menu.model.SwitchRowParams
import dev.mnascimentos.aureole.composable.sheets.model.ComponentFontOptions
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.model.rememberAureoleEditOverlayState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.model.FONT_KEY_LAUNCHER
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * The font picker of a component that has its own font (the clock), opened over the component's own sheet: the
 * bold switch under the header, then "Launcher font" and the catalog, each tile writing the component's sample.
 * Every choice applies at once, with no draft: flipping the switch calls [ComponentFontOptions.onBoldChange] and
 * keeps the sheet open; tapping a font calls [onFontSelected] and closes it. [onDismissRequest] runs once the sheet
 * is gone, whichever way it closed.
 */
@Composable
internal fun ComponentFontSheet(
    selectedFontName: String,
    onFontSelected: (String) -> Unit,
    onDismissRequest: () -> Unit,
    options: ComponentFontOptions,
    modifier: Modifier = Modifier
) {
    val overlayState = rememberAureoleEditOverlayState(initialExpanded = options.startsExpanded)
    AureoleEditOverlay(
        modifier = modifier,
        state = overlayState,
        onDismissRequest = onDismissRequest,
        params = AureoleEditOverlayParams(
            config = AureoleEditOverlayConfig(
                title = options.title,
                subtitle = options.subtitle,
                isLeftHanded = LocalLeftHanded.current,
                showHeaderAction = false
            ),
            previewContent = options.preview,
            sheetContent = {
                AureoleSwitchRow(
                    SwitchRowParams(
                        title = stringResource(R.string.settings_appearance_font_bold),
                        checked = options.isBold,
                        onCheckedChange = options.onBoldChange
                    )
                )
                FontPickerGrid(
                    shownFont = selectedFontName,
                    onSelect = { fontName ->
                        onFontSelected(fontName)
                        overlayState.internalVisible = false
                    },
                    component = options
                )
            }
        )
    )
}

@AureolePreview
@Composable
private fun ComponentFontSheetPreview() {
    var isBold by remember { mutableStateOf(true) }
    AureoleLauncherTheme {
        ComponentFontSheet(
            selectedFontName = FONT_KEY_LAUNCHER,
            onFontSelected = {},
            onDismissRequest = {},
            options = ComponentFontOptions(
                title = "Clock font",
                subtitle = "Only for this clock",
                sectionLabel = "Clock font",
                sample = "16:16",
                isBold = isBold,
                onBoldChange = { isBold = it }
            )
        )
    }
}
