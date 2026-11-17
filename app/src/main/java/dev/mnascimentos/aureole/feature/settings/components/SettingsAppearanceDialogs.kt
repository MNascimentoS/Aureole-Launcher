package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.color.model.DefaultPickerColor
import dev.mnascimentos.aureole.composable.minihome.AureoleMiniHome
import dev.mnascimentos.aureole.composable.minihome.sampleMiniHomeContent
import dev.mnascimentos.aureole.composable.sheets.AureoleAutoSaveOverlay
import dev.mnascimentos.aureole.composable.sheets.model.AutoSaveOverlayParams
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.model.BackgroundDraft
import dev.mnascimentos.aureole.feature.settings.model.BackgroundType
import dev.mnascimentos.aureole.feature.settings.model.SetBackgroundParams
import dev.mnascimentos.aureole.feature.settings.model.backgroundColorToSave
import dev.mnascimentos.aureole.feature.settings.model.initialBackgroundDraft

/**
 * Background editor in an edit overlay: the type (solid color or custom image) and, for a solid color, the
 * swatches, with the mini-home anchored above the sheet on the chosen color. Closing the sheet saves the color
 * when it changed; the custom image card opens the photo picker right away.
 */
@Composable
fun SetBackgroundBottomSheet(
    params: SetBackgroundParams,
    modifier: Modifier = Modifier
) {
    val initial = remember { initialBackgroundDraft(params.isCustomImageSet, params.currentColor) }
    var draft by remember { mutableStateOf(initial) }

    AureoleAutoSaveOverlay(
        params = AutoSaveOverlayParams(
            title = stringResource(R.string.settings_appearance_set_background_title),
            isChanged = { backgroundColorToSave(initial, draft) != null },
            onCommit = { backgroundColorToSave(initial, draft)?.let(params.onSolidColorSelected) },
            onDismissRequest = params.onDismissRequest,
            isLeftHanded = params.isLeftHanded,
            host = params.host
        ),
        modifier = modifier,
        preview = { BackgroundPreview(draft = draft, modifier = Modifier.align(Alignment.Center)) }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small),
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
        ) {
            SheetSectionLabel(
                text = stringResource(R.string.settings_appearance_background_type)
            )
            BackgroundTypeCards(
                selected = draft.type,
                onSelect = {
                    draft = draft.copy(type = it)
                    if (it == BackgroundType.CUSTOM_IMAGE) params.onCustomImageClick()
                },
                isLeftHanded = params.isLeftHanded
            )
            if (draft.type == BackgroundType.SOLID_COLOR) {
                BackgroundColorSections(
                    selected = draft.color,
                    onSelect = { draft = draft.copy(color = it) },
                    isLeftHanded = params.isLeftHanded
                )
            }
        }
    }
}

@Composable
private fun BackgroundPreview(draft: BackgroundDraft, modifier: Modifier = Modifier) {
    val fill = if (draft.type == BackgroundType.SOLID_COLOR) Color(draft.color) else null
    // Same preview for both hands: a mirrored layout direction must not move the favorites.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        AureoleMiniHome(content = sampleMiniHomeContent(), modifier = modifier, backgroundColor = fill)
    }
}

@Composable
internal fun SheetSectionLabel(text: String, modifier: Modifier = Modifier) {
    AureoleText(
        text = text.uppercase(),
        color = AureoleDS.colors.onSurfaceMedium,
        modifier = modifier.fillMaxWidth(),
        format = AureoleTextFormatParams(style = AureoleDS.typography.labelSmall)
    )
}

@AureolePreview
@Composable
private fun SetBackgroundBottomSheetPreview() {
    AureoleLauncherTheme {
        SetBackgroundBottomSheet(
            params = SetBackgroundParams(
                currentColor = DefaultPickerColor,
                isCustomImageSet = false,
                onSolidColorSelected = {},
                onCustomImageClick = {},
                onDismissRequest = {}
            )
        )
    }
}
