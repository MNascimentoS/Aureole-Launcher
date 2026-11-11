package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.color.AureoleColorSwatches
import dev.mnascimentos.aureole.composable.color.model.DefaultPickerColor
import dev.mnascimentos.aureole.composable.color.model.RecommendedColors
import dev.mnascimentos.aureole.composable.minihome.AureoleMiniHome
import dev.mnascimentos.aureole.composable.minihome.sampleMiniHomeContent
import dev.mnascimentos.aureole.composable.mirror.LeftHandMirror
import dev.mnascimentos.aureole.composable.sheets.AureoleConfirmOverlay
import dev.mnascimentos.aureole.composable.sheets.model.ConfirmOverlayParams
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.model.BackgroundApplyAction
import dev.mnascimentos.aureole.feature.settings.model.BackgroundDraft
import dev.mnascimentos.aureole.feature.settings.model.BackgroundType
import dev.mnascimentos.aureole.feature.settings.model.SetBackgroundParams
import dev.mnascimentos.aureole.feature.settings.model.applyAction
import dev.mnascimentos.aureole.feature.settings.model.initialBackgroundDraft

/**
 * Background editor in an edit overlay: the type (solid color or custom image) and, for a solid color, the
 * swatches, with the mini-home anchored above the sheet on the chosen color. Nothing changes until Apply, which
 * saves the color or opens the photo picker; Cancel only closes.
 */
@Composable
fun SetBackgroundBottomSheet(
    params: SetBackgroundParams,
    modifier: Modifier = Modifier
) {
    var draft by remember { mutableStateOf(initialBackgroundDraft(params.isCustomImageSet, params.currentColor)) }

    AureoleConfirmOverlay(
        params = ConfirmOverlayParams(
            title = stringResource(R.string.settings_appearance_set_background_title),
            primaryLabel = stringResource(R.string.settings_appearance_apply),
            onPrimary = {
                when (val action = draft.applyAction()) {
                    is BackgroundApplyAction.SaveColor -> params.onSolidColorSelected(action.color)
                    BackgroundApplyAction.PickImage -> params.onCustomImageClick()
                }
            },
            onDismissRequest = params.onDismissRequest,
            isLeftHanded = params.isLeftHanded
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
                onSelect = { draft = draft.copy(type = it) },
                isLeftHanded = params.isLeftHanded
            )
            if (draft.type == BackgroundType.SOLID_COLOR) {
                SheetSectionLabel(
                    text = stringResource(R.string.settings_appearance_background_color)
                )
                LeftHandMirror(isLeftHanded = params.isLeftHanded) {
                    AureoleColorSwatches(
                        colors = RecommendedColors,
                        selected = draft.color,
                        onSelect = { draft = draft.copy(color = it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BackgroundPreview(draft: BackgroundDraft, modifier: Modifier = Modifier) {
    val fill = if (draft.type == BackgroundType.SOLID_COLOR) Color(draft.color) else null
    AureoleMiniHome(content = sampleMiniHomeContent(), modifier = modifier, backgroundColor = fill)
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
