package dev.mnascimentos.aureole.core.data.model

/** Smallest zoom: the photo just covers the screen. */
const val WALLPAPER_MIN_ZOOM = 1f

/** Largest zoom over the "cover the screen" size. */
const val WALLPAPER_MAX_ZOOM = 5f

/** Strongest dim layer over the photo. */
const val WALLPAPER_MAX_DIM = 0.7f

/** Strongest blur, in dp. */
const val WALLPAPER_MAX_BLUR_DP = 25f
private const val CENTER = 0.5f
private const val DEFAULT_DIM = 0.3f
private const val FULL_TURN = 360
private const val QUARTER_TURN = 90
private val NUMBER = "-?[0-9]+(?:\\.[0-9]+)?(?:[eE][-+]?[0-9]+)?"

/**
 * How the custom photo is framed and tinted, kept apart from the original photo so editing again loses nothing.
 *
 * @property zoom Magnification on top of the "cover the screen" size: 1 covers the screen, 5 is the maximum.
 * @property focusX Horizontal center of the visible part, as a 0..1 fraction of the (rotated) photo.
 * @property focusY Vertical center of the visible part, as a 0..1 fraction of the (rotated) photo.
 * @property rotation Clockwise rotation in steps of 90 degrees: 0, 90, 180 or 270.
 * @property dim Black layer opacity over the photo, 0..0.7 (0.3 is what the launcher always used).
 * @property blurDp Blur radius in dp, 0..25.
 */
data class WallpaperAdjustments(
    val zoom: Float = WALLPAPER_MIN_ZOOM,
    val focusX: Float = CENTER,
    val focusY: Float = CENTER,
    val rotation: Int = 0,
    val dim: Float = DEFAULT_DIM,
    val blurDp: Float = 0f
) {
    /** Same values forced into their valid ranges; a rotation that is not a quarter turn snaps to the nearest one. */
    fun coerced(): WallpaperAdjustments = WallpaperAdjustments(
        zoom = zoom.coerceIn(WALLPAPER_MIN_ZOOM, WALLPAPER_MAX_ZOOM),
        focusX = focusX.coerceIn(0f, 1f),
        focusY = focusY.coerceIn(0f, 1f),
        rotation = Math.floorMod(Math.round(rotation / QUARTER_TURN.toFloat()) * QUARTER_TURN, FULL_TURN),
        dim = dim.coerceIn(0f, WALLPAPER_MAX_DIM),
        blurDp = blurDp.coerceIn(0f, WALLPAPER_MAX_BLUR_DP)
    )

    /** True when nothing differs from the launcher's classic look (cover, centered, 30% dim, no blur). */
    val isDefault: Boolean get() = this == DEFAULT

    /** True when zoom, position or rotation differ from the plain "cover and center" framing. */
    val hasFraming: Boolean
        get() = zoom != WALLPAPER_MIN_ZOOM || focusX != CENTER || focusY != CENTER || rotation != 0

    /** Same dim and blur with the framing back to the default. */
    fun withoutFraming(): WallpaperAdjustments =
        copy(zoom = WALLPAPER_MIN_ZOOM, focusX = CENTER, focusY = CENTER, rotation = 0)

    /** A small JSON object; read back by [fromJson]. */
    fun toJson(): String =
        "{\"zoom\":$zoom,\"focusX\":$focusX,\"focusY\":$focusY,\"rotation\":$rotation,\"dim\":$dim,\"blurDp\":$blurDp}"

    companion object {
        val DEFAULT = WallpaperAdjustments()

        /** Reads [json] written by [toJson]; null, blank or broken text gives [DEFAULT], missing keys their default. */
        fun fromJson(json: String?): WallpaperAdjustments {
            if (json.isNullOrBlank()) return DEFAULT
            fun number(key: String): Float? =
                Regex("\"$key\"\\s*:\\s*($NUMBER)").find(json)?.groupValues?.get(1)?.toFloatOrNull()
            return WallpaperAdjustments(
                zoom = number("zoom") ?: DEFAULT.zoom,
                focusX = number("focusX") ?: DEFAULT.focusX,
                focusY = number("focusY") ?: DEFAULT.focusY,
                rotation = number("rotation")?.toInt() ?: DEFAULT.rotation,
                dim = number("dim") ?: DEFAULT.dim,
                blurDp = number("blurDp") ?: DEFAULT.blurDp
            ).coerced()
        }
    }
}
