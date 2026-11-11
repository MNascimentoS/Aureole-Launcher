package dev.mnascimentos.aureole.feature.settings.model

import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.model.GlassLevel
import kotlin.math.abs

private const val OPACITY_TOLERANCE = 0.01f
private const val RADIUS_FLAT_DP = 0
private const val RADIUS_COMPACT_DP = 8
private const val RADIUS_SMALL_DP = 12
private const val RADIUS_STANDARD_DP = 16
private const val RADIUS_LARGE_DP = 24
private const val OPACITY_LOW = 0.2f
private const val OPACITY_MEDIUM = 0.5f
private const val OPACITY_HIGH = 0.7f
private const val OPACITY_SOLID = 0.9f

/** Corner radii (dp) offered by the "Cards and glass" sheet; 0 is the square corner. */
val CardRadiusOptions: List<Int> = listOf(
    RADIUS_FLAT_DP,
    RADIUS_COMPACT_DP,
    RADIUS_SMALL_DP,
    RADIUS_STANDARD_DP,
    RADIUS_LARGE_DP
)

/** Opacity of the cards over the blur, from the lowest to the solid one. */
enum class BlurOpacity(val value: Float, @StringRes val labelRes: Int) {
    LOW(OPACITY_LOW, R.string.settings_appearance_blur_opacity_low),
    MEDIUM(OPACITY_MEDIUM, R.string.settings_appearance_blur_opacity_medium),
    HIGH(OPACITY_HIGH, R.string.settings_appearance_blur_opacity_high),
    SOLID(OPACITY_SOLID, R.string.settings_appearance_blur_opacity_solid)
}

/**
 * What the user is trying in the "Cards and glass" sheet before tapping Apply.
 *
 * @property radiusDp Corner radius of the cards, as stored (it may lie between two chips).
 * @property glass Glass level; [GlassLevel.NONE] means the blur is off.
 * @property opacity Opacity of the cards over the blur, as stored.
 */
data class CardsGlassDraft(
    val radiusDp: Int,
    val glass: GlassLevel,
    val opacity: Float
)

/**
 * Parameters of `CardsGlassBottomSheet`.
 *
 * @property initial Values applied when the sheet opened; kept as the "before" of the undo.
 * @property onChange Called with the draft on every tap: the change applies on the spot.
 * @property onDismissRequest Called when the sheet closes, whichever way it closed, with the values it opened with.
 * @property isLeftHanded Puts Done under the left thumb.
 */
data class CardsGlassParams(
    val initial: CardsGlassDraft,
    val onChange: (CardsGlassDraft) -> Unit,
    val onDismissRequest: (CardsGlassDraft) -> Unit,
    val isLeftHanded: Boolean = false
)

/** Draft that reflects what is applied now: the blur is [GlassLevel.NONE] when it is off. */
fun initialCardsGlassDraft(
    radiusDp: Int,
    isHazeEnabled: Boolean,
    hazeOpacity: Float,
    storedLevel: GlassLevel
): CardsGlassDraft = CardsGlassDraft(
    radiusDp = radiusDp,
    glass = if (isHazeEnabled) storedLevel else GlassLevel.NONE,
    opacity = hazeOpacity
)

/** The opacity chip that [opacity] matches, or null when the stored value lies between two chips. */
fun blurOpacityOf(opacity: Float): BlurOpacity? =
    BlurOpacity.entries.firstOrNull { abs(it.value - opacity) < OPACITY_TOLERANCE }

/** True when the blur is on in this draft. */
fun CardsGlassDraft.isHazeEnabled(): Boolean = glass != GlassLevel.NONE

/** Settings state after applying [draft]: the last chosen level is kept while the blur is off. */
fun SettingsUiState.withCardsGlass(draft: CardsGlassDraft): SettingsUiState = copy(
    cornerRadiusDp = draft.radiusDp,
    isHazeEnabled = draft.isHazeEnabled(),
    hazeOpacity = draft.opacity,
    hazeBlurLevel = if (draft.isHazeEnabled()) draft.glass else hazeBlurLevel
)

/** Draft of the settings screen: what the sheet starts from. */
fun SettingsUiState.toCardsGlassDraft(): CardsGlassDraft =
    initialCardsGlassDraft(cornerRadiusDp, isHazeEnabled, hazeOpacity, hazeBlurLevel)

/** Level shown in the "Glass effect" row of the appearance screen. */
fun SettingsUiState.shownGlassLevel(): GlassLevel = toCardsGlassDraft().glass
