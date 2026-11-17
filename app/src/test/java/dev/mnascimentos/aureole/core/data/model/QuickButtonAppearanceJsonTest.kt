package dev.mnascimentos.aureole.core.data.model

import com.google.gson.Gson
import dev.mnascimentos.aureole.core.designsystem.icons.AureoleIconCatalog
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class QuickButtonAppearanceJsonTest {
    private val gson = Gson()

    private fun read(json: String) = gson.fromJson(json, QuickButtonModel::class.java).appearance

    @Test
    fun `when reading the old json, should keep the icon and turn argb colors into fixed ones`() {
        val appearance = read(
            """{"id":"a","appearance":{"backgroundColor":-14575885,"strokeEnabled":true,"strokeColor":-1,""" +
                """"icon":"STAR","iconColor":-3355444}}"""
        )

        assertEquals(AureoleIconCatalog.actionKey(QuickButtonIcon.STAR), appearance.icon)
        assertEquals(QuickButtonIcon.STAR.iconRes, appearance.iconRes)
        assertEquals(ThemeColorRef.Fixed(-14575885), appearance.backgroundColor)
        assertEquals(ThemeColorRef.Fixed(-1), appearance.strokeColor)
        assertEquals(ThemeColorRef.Fixed(-3355444), appearance.iconColor)
        assertEquals(true, appearance.strokeEnabled)
        assertNull(appearance.label)
        assertFalse(appearance.showLabel)
    }

    @Test
    fun `when reading an old background that meant default, should follow the theme variant surface`() {
        val variant = ThemeColorRef.Role(ThemeColorRole.VARIANT)

        assertEquals(variant, read("""{"appearance":{"backgroundColor":0}}""").backgroundColor)
        assertEquals(variant, read("""{"appearance":{"backgroundColor":${0xFF455A64.toInt()}}}""").backgroundColor)
    }

    @Test
    fun `when reading every old icon name, should resolve to a drawable of the same icon`() {
        QuickButtonIcon.entries.forEach { icon ->
            val appearance = read("""{"appearance":{"icon":"${icon.name}"}}""")

            assertEquals(icon.name, icon.iconRes, appearance.iconRes)
        }
    }

    @Test
    fun `when a field is missing, should use the defaults of the old look`() {
        val appearance = read("""{"id":"a","appearance":{}}""")

        assertEquals(QuickButtonAppearance(), appearance)
        assertEquals(ThemeColorRef.Fixed(0xFFFFFFFF.toInt()), appearance.iconColor)
        assertEquals(QuickButtonIcon.ADD.iconRes, appearance.iconRes)
    }

    @Test
    fun `when the icon is unknown, should fall back to the default icon`() {
        assertEquals(DEFAULT_QUICK_BUTTON_ICON_KEY, read("""{"appearance":{"icon":"NOPE"}}""").icon)
        assertEquals(DEFAULT_QUICK_BUTTON_ICON_KEY, read("""{"appearance":{"icon":5}}""").icon)
    }

    @Test
    fun `when saving and reading, should keep theme roles, fixed colors, the name and the catalog icon`() {
        val appearance = QuickButtonAppearance(
            backgroundColor = ThemeColorRef.Role(ThemeColorRole.ACCENT),
            strokeEnabled = true,
            strokeColor = ThemeColorRef.Fixed(0xFF112233.toInt()),
            icon = "location_on",
            iconColor = ThemeColorRef.Role(ThemeColorRole.SURFACE),
            label = "Mapa",
            showLabel = true
        )

        val model = gson.fromJson(gson.toJson(QuickButtonModel(id = "q", appearance = appearance)), QuickButtonModel::class.java)

        assertEquals(appearance, model.appearance)
    }

    @Test
    fun `when an old save is written again, should stay readable and equal`() {
        val old = read("""{"appearance":{"backgroundColor":-14575885,"icon":"DATE_RANGE","iconColor":-1}}""")

        val again = gson.fromJson(gson.toJson(QuickButtonModel(appearance = old)), QuickButtonModel::class.java)

        assertEquals(old, again.appearance)
    }

    @Test
    fun `when a color is malformed, should use the fallback instead of failing`() {
        val appearance = read("""{"appearance":{"iconColor":"nope","strokeColor":{"x":1},"backgroundColor":true}}""")

        assertEquals(ThemeColorRef.Fixed(0xFFFFFFFF.toInt()), appearance.iconColor)
        assertEquals(ThemeColorRef.Fixed(0xFFFFFFFF.toInt()), appearance.strokeColor)
        assertEquals(ThemeColorRef.Role(ThemeColorRole.VARIANT), appearance.backgroundColor)
    }
}
