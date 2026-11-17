package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.minihome.AureoleMiniHome
import dev.mnascimentos.aureole.composable.minihome.model.MiniHomeFavoritesLayout
import dev.mnascimentos.aureole.composable.minihome.sampleMiniHomeContent
import dev.mnascimentos.aureole.composable.mirror.LeftHandMirror
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.settings.model.BackgroundDraft
import dev.mnascimentos.aureole.feature.settings.model.BackgroundPhotoCard
import dev.mnascimentos.aureole.feature.settings.model.BackgroundType
import dev.mnascimentos.aureole.feature.settings.model.LockScreenDialog
import dev.mnascimentos.aureole.feature.settings.model.SetBackgroundParams
import dev.mnascimentos.aureole.feature.settings.model.lockScreenDialogFor

/**
 * The background choices (RF-02 of polimento-ui): TYPE with the two cards, the lock screen switch as a plain row
 * with its own margin, and for a solid color the swatch sections, each label with the same space above it as the
 * Appearance groups. Without a photo the custom image card opens the photo picker right away; with one, it shows
 * the photo with "Edit photo" and "Change photo". A lock screen question goes to [onLockDialog], to be drawn beside
 * the sheet.
 */
@Composable
internal fun BackgroundPickerContent(
    params: SetBackgroundParams,
    draft: BackgroundDraft,
    onDraftChange: (BackgroundDraft) -> Unit,
    onLockDialog: (LockScreenDialog?) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        SheetSectionHeading(text = stringResource(R.string.settings_appearance_background_type), isFirst = true)
        val photo = params.customImagePath?.takeIf { params.isCustomImageSet }?.let { path ->
            BackgroundPhotoCard(
                path = path,
                adjustments = params.adjustments,
                onEdit = {
                    onDraftChange(draft.copy(type = BackgroundType.CUSTOM_IMAGE))
                    params.onEditPhotoClick()
                },
                onChange = {
                    onDraftChange(draft.copy(type = BackgroundType.CUSTOM_IMAGE))
                    params.onCustomImageClick()
                }
            )
        }
        BackgroundTypeCards(
            selected = draft.type,
            onSelect = {
                onDraftChange(draft.copy(type = it))
                if (it == BackgroundType.CUSTOM_IMAGE && photo == null) params.onCustomImageClick()
            },
            isLeftHanded = params.isLeftHanded,
            photo = photo
        )
        BackgroundLockScreenRow(
            option = params.lockScreen,
            onToggle = { turnOn ->
                val dialog = lockScreenDialogFor(turnOn, params.lockScreen.isNoticeSeen)
                if (dialog == null) params.lockScreen.onTurnOn() else onLockDialog(dialog)
            },
            modifier = Modifier.padding(top = AureoleDS.dimens.small)
        )
        if (draft.type == BackgroundType.SOLID_COLOR) {
            BackgroundColorSections(
                selected = draft.color,
                onSelect = { onDraftChange(draft.copy(color = it)) }
            )
        }
        Spacer(modifier = Modifier.height(AureoleDS.dimens.large))
    }
}

/**
 * The mini-home on the background [draft]: its solid color, or the photo with its framing and dim when the custom
 * image is chosen and one is applied; with no draft (null), on the theme background.
 */
@Composable
internal fun BackgroundMiniHome(
    draft: BackgroundDraft?,
    params: SetBackgroundParams,
    modifier: Modifier = Modifier,
    themedIcons: Boolean = false
) {
    val photoPath = params.customImagePath
        ?.takeIf { draft?.type == BackgroundType.CUSTOM_IMAGE && params.isCustomImageSet && it.isNotEmpty() }
    AureoleMiniHome(
        content = sampleMiniHomeContent(),
        modifier = modifier,
        layout = MiniHomeFavoritesLayout(isLeftHanded = LocalLeftHanded.current, themedIcons = themedIcons),
        backgroundColor = draft?.takeIf { it.type == BackgroundType.SOLID_COLOR }?.let { Color(it.color) },
        backdrop = photoPath?.let { path -> { WallpaperPhotoLayer(path = path, adjustments = params.adjustments) } }
    )
}

/**
 * [SheetSectionLabel] spaced like the groups of the settings sheets (RF-15 of ajustes-redesign): `large` above every
 * label but the first ([isFirst]) and a fixed `xSmall` between the label and what it names.
 */
@Composable
internal fun SheetSectionHeading(text: String, modifier: Modifier = Modifier, isFirst: Boolean = false) {
    SheetSectionLabel(
        text = text,
        modifier = modifier
            .then(if (isFirst) Modifier else Modifier.padding(top = AureoleDS.dimens.large))
            .padding(start = AureoleDS.dimens.xxSmall, bottom = AureoleDS.dimens.xSmall)
    )
}

/** A small uppercase label above a group of rows; mirrored in the left-handed mode, like the rows below it. */
@Composable
internal fun SheetSectionLabel(text: String, modifier: Modifier = Modifier) {
    // The paragraph follows its own text (as in the radio rows), so its side is set explicitly.
    val align = if (LocalLeftHanded.current) TextAlign.End else TextAlign.Start
    LeftHandMirror {
        AureoleText(
            text = text.uppercase(),
            color = AureoleDS.colors.onSurfaceMedium,
            modifier = modifier.fillMaxWidth(),
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.labelSmall.copy(textDirection = TextDirection.Content),
                textAlign = align
            )
        )
    }
}
