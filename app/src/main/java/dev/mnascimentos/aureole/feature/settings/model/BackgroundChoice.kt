package dev.mnascimentos.aureole.feature.settings.model

import dev.mnascimentos.aureole.core.data.model.WallpaperAdjustments
import dev.mnascimentos.aureole.core.designsystem.model.OverlayHostSpec

/** What the home background is made of. */
enum class BackgroundType { SOLID_COLOR, CUSTOM_IMAGE }

/**
 * Pending choice of the background sheet, kept until the sheet closes.
 *
 * @property type Solid color or custom image.
 * @property color Color picked for [BackgroundType.SOLID_COLOR].
 */
data class BackgroundDraft(
    val type: BackgroundType,
    val color: Int
)

/** The sheet opens on the image type when the user already has a custom image, on the solid color otherwise. */
fun initialBackgroundDraft(isCustomImageSet: Boolean, currentColor: Int): BackgroundDraft =
    BackgroundDraft(
        type = if (isCustomImageSet) BackgroundType.CUSTOM_IMAGE else BackgroundType.SOLID_COLOR,
        color = currentColor
    )

/**
 * The color closing the sheet saves, or null when there is nothing to save. The custom image saves itself when the
 * photo is picked, so only the solid color type has something left to commit: when its color differs from the one
 * the sheet opened with, or when it replaces a custom image.
 */
fun backgroundColorToSave(initial: BackgroundDraft, current: BackgroundDraft): Int? = when {
    current.type != BackgroundType.SOLID_COLOR -> null
    initial.type == BackgroundType.SOLID_COLOR && initial.color == current.color -> null
    else -> current.color
}

/**
 * Inputs of the background sheet.
 *
 * @property currentColor Color the sheet opens on.
 * @property isCustomImageSet Whether a custom image is the current background.
 * @property onSolidColorSelected Called with the color when the sheet closes on a changed solid color.
 * @property onCustomImageClick Opens the photo picker: the custom-image card without a photo yet, or "Change photo".
 * @property onDismissRequest Called when the sheet has closed, whichever way it closed.
 * @property isLeftHanded Puts the first card under the left thumb.
 * @property host What the settings host decides for its sheets.
 * @property customImagePath The photo applied, for the thumbnail of its card; null shows the plain card.
 * @property adjustments Saved adjustments of that photo, for the thumbnail.
 * @property onEditPhotoClick "Edit photo": opens the editor with the current adjustments.
 */
data class SetBackgroundParams(
    val currentColor: Int,
    val isCustomImageSet: Boolean,
    val onSolidColorSelected: (Int) -> Unit,
    val onCustomImageClick: () -> Unit,
    val onDismissRequest: () -> Unit,
    val isLeftHanded: Boolean = false,
    val host: OverlayHostSpec = OverlayHostSpec(),
    val customImagePath: String? = null,
    val adjustments: WallpaperAdjustments = WallpaperAdjustments.DEFAULT,
    val onEditPhotoClick: () -> Unit = {}
)
