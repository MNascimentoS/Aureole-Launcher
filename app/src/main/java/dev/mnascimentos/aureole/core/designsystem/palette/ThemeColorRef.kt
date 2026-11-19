package dev.mnascimentos.aureole.core.designsystem.palette

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleColors

/** Roles of the current theme a [ThemeColorRef] can point to. */
enum class ThemeColorRole { BACKGROUND, SURFACE, VARIANT, OUTLINE, ACCENT }

/**
 * A color picked for something that can follow the theme: either a [Role] of the current theme, which changes
 * when the theme changes (pasta-form D-03), or a [Fixed] ARGB color taken from another palette, which does not.
 * Saved as a small JSON text ([toJson] / [parseThemeColorRef]).
 */
sealed interface ThemeColorRef {
    data class Role(val role: ThemeColorRole) : ThemeColorRef

    data class Fixed(val argb: Int) : ThemeColorRef

    /** The color this reference has under [colors] (the current theme). */
    fun resolve(colors: AureoleColors): Color = when (this) {
        is Fixed -> Color(argb)
        is Role -> when (role) {
            ThemeColorRole.BACKGROUND -> colors.background
            ThemeColorRole.SURFACE -> colors.surface
            ThemeColorRole.VARIANT -> colors.surfaceVariant
            ThemeColorRole.OUTLINE -> colors.outline
            ThemeColorRole.ACCENT -> colors.onSurfaceMedium
        }
    }

    fun toJson(): String = JsonObject().also {
        when (this) {
            is Role -> it.addProperty(KEY_ROLE, role.name)
            is Fixed -> it.addProperty(KEY_ARGB, argb)
        }
    }.toString()
}

private const val KEY_ROLE = "role"
private const val KEY_ARGB = "argb"
private const val OPAQUE_MASK = 0xFF000000.toInt()

/** Reads what [ThemeColorRef.toJson] wrote; null for null, blank, malformed or unknown content. */
fun parseThemeColorRef(json: String?): ThemeColorRef? {
    if (json.isNullOrBlank()) return null
    val obj = runCatching { JsonParser.parseString(json).asJsonObject }.getOrNull() ?: return null
    val role = obj.get(KEY_ROLE)?.takeIf { it.isJsonPrimitive }?.asString
        ?.let { name -> ThemeColorRole.entries.firstOrNull { it.name == name } }
    val argb = runCatching { obj.get(KEY_ARGB)?.takeIf { it.isJsonPrimitive }?.asInt }.getOrNull()
    return when {
        role != null -> ThemeColorRef.Role(role)
        argb != null -> ThemeColorRef.Fixed(argb)
        else -> null
    }
}

/**
 * Color for the icon or letter drawn over [background]: the high emphasis text or the background of [colors],
 * whichever contrasts more, so a pale fixed color in a dark theme (or the reverse) stays readable.
 */
fun readableOn(background: Color, colors: AureoleColors): Color {
    val bg = background.toArgb() or OPAQUE_MASK
    val onHigh = colors.onSurfaceHigh
    val alternative = colors.background
    return if (ColorUtils.calculateContrast(onHigh.toArgb(), bg) >=
        ColorUtils.calculateContrast(alternative.toArgb(), bg)
    ) {
        onHigh
    } else {
        alternative
    }
}
