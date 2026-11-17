package dev.mnascimentos.aureole.core.data.model

import androidx.annotation.Keep
import com.google.gson.JsonParser
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import dev.mnascimentos.aureole.core.designsystem.icons.AureoleIconCatalog
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRole
import dev.mnascimentos.aureole.core.designsystem.palette.parseThemeColorRef

/** Background ARGB the old editor saved for "no color picked": it drew the theme variant surface. */
internal const val LEGACY_DEFAULT_BACKGROUND_ARGB = 0xFF455A64.toInt()

/**
 * Reads and writes a [ThemeColorRef] field of a saved quick button. Old saves hold a bare ARGB number: it becomes a
 * [ThemeColorRef.Fixed] color, so the button looks as before. New saves hold the [ThemeColorRef.toJson] object.
 */
@Keep
internal open class ThemeColorRefAdapter(private val fallback: ThemeColorRef) : TypeAdapter<ThemeColorRef>() {

    /** What an old ARGB number means; the default keeps it as a fixed color. */
    protected open fun fromLegacy(argb: Int): ThemeColorRef = ThemeColorRef.Fixed(argb)

    override fun write(out: JsonWriter, value: ThemeColorRef?) {
        if (value == null) out.nullValue() else out.jsonValue(value.toJson())
    }

    override fun read(reader: JsonReader): ThemeColorRef = when (reader.peek()) {
        JsonToken.NUMBER -> fromLegacy(reader.nextLong().toInt())
        JsonToken.STRING -> parseThemeColorRef(reader.nextString()) ?: fallback
        JsonToken.BEGIN_OBJECT -> parseThemeColorRef(JsonParser.parseReader(reader).toString()) ?: fallback
        else -> {
            reader.skipValue()
            fallback
        }
    }
}

/** The icon color and the stroke color: white unless saved otherwise. */
@Keep
internal class WhiteColorRefAdapter : ThemeColorRefAdapter(WHITE)

/** The background: 0 and the old default blue-grey were "default", the theme variant surface. */
@Keep
internal class BackgroundColorRefAdapter : ThemeColorRefAdapter(DEFAULT_BACKGROUND) {
    override fun fromLegacy(argb: Int): ThemeColorRef =
        if (argb == 0 || argb == LEGACY_DEFAULT_BACKGROUND_ARGB) DEFAULT_BACKGROUND else ThemeColorRef.Fixed(argb)
}

/**
 * Reads the icon of a saved quick button as a catalog key. Old saves hold the [QuickButtonIcon] name (`STAR`), which
 * resolves to the same icon; an unknown value falls back to the default icon.
 */
@Keep
internal class QuickButtonIconKeyAdapter : TypeAdapter<String>() {
    override fun write(out: JsonWriter, value: String?) {
        if (value == null) out.nullValue() else out.value(value)
    }

    override fun read(reader: JsonReader): String {
        if (reader.peek() != JsonToken.STRING) {
            reader.skipValue()
            return DEFAULT_QUICK_BUTTON_ICON_KEY
        }
        return AureoleIconCatalog.resolveKey(reader.nextString()) ?: DEFAULT_QUICK_BUTTON_ICON_KEY
    }
}

private val WHITE = ThemeColorRef.Fixed(0xFFFFFFFF.toInt())
private val DEFAULT_BACKGROUND = ThemeColorRef.Role(ThemeColorRole.VARIANT)
