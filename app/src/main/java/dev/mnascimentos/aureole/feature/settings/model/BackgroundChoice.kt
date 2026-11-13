package dev.mnascimentos.aureole.feature.settings.model

/** What the home background is made of. */
enum class BackgroundType { SOLID_COLOR, CUSTOM_IMAGE }

/**
 * Pending choice of the background sheet, kept until Apply.
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

/** What Apply has to do for [draft]: save the solid color, or open the photo picker for a custom image. */
sealed interface BackgroundApplyAction {
    data class SaveColor(val color: Int) : BackgroundApplyAction
    data object PickImage : BackgroundApplyAction
}

fun BackgroundDraft.applyAction(): BackgroundApplyAction = when (type) {
    BackgroundType.SOLID_COLOR -> BackgroundApplyAction.SaveColor(color)
    BackgroundType.CUSTOM_IMAGE -> BackgroundApplyAction.PickImage
}

/**
 * Inputs of the background sheet.
 *
 * @property currentColor Color the sheet opens on.
 * @property isCustomImageSet Whether a custom image is the current background.
 * @property onSolidColorSelected Called with the color when Apply is tapped on the solid-color type.
 * @property onCustomImageClick Called when Apply is tapped on the custom-image type (opens the photo picker).
 * @property onDismissRequest Called when the sheet has closed, whichever way it closed.
 * @property isLeftHanded Puts Apply and the first card under the left thumb.
 */
data class SetBackgroundParams(
    val currentColor: Int,
    val isCustomImageSet: Boolean,
    val onSolidColorSelected: (Int) -> Unit,
    val onCustomImageClick: () -> Unit,
    val onDismissRequest: () -> Unit,
    val isLeftHanded: Boolean = false
)
