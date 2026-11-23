package dev.mnascimentos.aureole.core.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import dev.mnascimentos.aureole.core.designsystem.theme.model.FONT_KEY_LAUNCHER

/**
 * The look of one clock on the home grid, saved per grid item id. Every field has a default (the factory clock), so
 * Gson keeps the default for a field a saved JSON does not have. The glass (blur and opacity) is not here: it is the
 * launcher's, shared by every container. [fontFamily] is a catalog key or `FONT_KEY_LAUNCHER`; a key saved by an
 * older version reads as its catalog font ([withMigratedFont]). [isFontBold] draws the time in bold. The clock has a
 * card when it has a card color ([backgroundColorRef]); none is "None" (D-09). [legacyBackgroundEnabled] is the
 * removed card switch: read from an older JSON and folded into the card color by [withMigratedCard], then null, which
 * Gson leaves out, so it is never written again.
 */
@Keep
data class ClockConfig(
    val style: String = DEFAULT_STYLE,
    val customGreeting: String = "",
    val message: String = DEFAULT_MESSAGE,
    val alignment: String = DEFAULT_ALIGNMENT,
    val fontFamily: String = DEFAULT_FONT_FAMILY,
    val timeFormat: String = DEFAULT_TIME_FORMAT,
    val dateFormat: String = DEFAULT_DATE_FORMAT,
    val textColor: Int = 0,
    val backgroundColor: Int = 0,
    val textColorRef: String? = null,
    val backgroundColorRef: String? = null,
    @SerializedName("isBackgroundEnabled") val legacyBackgroundEnabled: Boolean? = null,
    val isFontBold: Boolean = DEFAULT_FONT_BOLD
) {
    /** Whether the clock draws its card: it has a card color (an old ARGB one counts too). */
    val isBackgroundEnabled: Boolean
        get() = backgroundColorRef != null || backgroundColor != 0

    companion object {
        /** A [ClockLayout] key; older installs may still hold an old style key ([withMigratedLayout]). */
        const val DEFAULT_STYLE = "DATE_ON_TOP"
        const val DEFAULT_MESSAGE = "GREETING"
        const val DEFAULT_ALIGNMENT = "CENTER"
        const val DEFAULT_FONT_FAMILY = FONT_KEY_LAUNCHER
        const val DEFAULT_FONT_BOLD = true
        const val DEFAULT_TIME_FORMAT = "SYSTEM"
        const val DEFAULT_DATE_FORMAT = "DEFAULT"
    }
}
