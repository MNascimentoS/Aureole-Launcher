package dev.mnascimentos.aureole.core.designsystem.palette

import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleColors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ThemeColorRefTest {

    private val dark = AureoleColors(
        onSurfaceHigh = Color(0xFFF0F0F0),
        onSurfaceMedium = Color(0xFF80CBC4),
        onSurfaceLow = Color(0xFF607D8B),
        outline = Color(0xFF333344),
        surfaceVariant = Color(0xFF222233),
        surface = Color(0xFF111122),
        background = Color(0xFF050510)
    )
    private val other = dark.copy(
        surfaceVariant = Color(0xFFAA0000),
        background = Color(0xFF00AA00)
    )

    @Test
    fun `a role resolves to its color of the theme`() {
        assertEquals(dark.background, ThemeColorRef.Role(ThemeColorRole.BACKGROUND).resolve(dark))
        assertEquals(dark.surface, ThemeColorRef.Role(ThemeColorRole.SURFACE).resolve(dark))
        assertEquals(dark.surfaceVariant, ThemeColorRef.Role(ThemeColorRole.VARIANT).resolve(dark))
        assertEquals(dark.outline, ThemeColorRef.Role(ThemeColorRole.OUTLINE).resolve(dark))
        assertEquals(dark.onSurfaceMedium, ThemeColorRef.Role(ThemeColorRole.ACCENT).resolve(dark))
    }

    @Test
    fun `a role follows the theme and a fixed color does not`() {
        val role = ThemeColorRef.Role(ThemeColorRole.VARIANT)
        val fixed = ThemeColorRef.Fixed(0xFF123456.toInt())

        assertEquals(other.surfaceVariant, role.resolve(other))
        assertEquals(Color(0xFF123456), fixed.resolve(dark))
        assertEquals(Color(0xFF123456), fixed.resolve(other))
    }

    @Test
    fun `every role survives the json round trip`() {
        ThemeColorRole.entries.forEach {
            val ref = ThemeColorRef.Role(it)

            assertEquals(ref, parseThemeColorRef(ref.toJson()))
        }
    }

    @Test
    fun `a fixed color survives the json round trip, alpha and sign included`() {
        listOf(0xFF123456.toInt(), 0x80FFFFFF.toInt(), 0, -1).forEach {
            val ref = ThemeColorRef.Fixed(it)

            assertEquals(ref, parseThemeColorRef(ref.toJson()))
        }
    }

    @Test
    fun `the json names the role or the color`() {
        assertEquals("""{"role":"ACCENT"}""", ThemeColorRef.Role(ThemeColorRole.ACCENT).toJson())
        assertEquals("""{"argb":-1}""", ThemeColorRef.Fixed(-1).toJson())
    }

    @Test
    fun `missing, blank, broken or unknown content reads as no color`() {
        assertNull(parseThemeColorRef(null))
        assertNull(parseThemeColorRef(""))
        assertNull(parseThemeColorRef("   "))
        assertNull(parseThemeColorRef("not json"))
        assertNull(parseThemeColorRef("[1,2]"))
        assertNull(parseThemeColorRef("{}"))
        assertNull(parseThemeColorRef("""{"role":"NOPE"}"""))
        assertNull(parseThemeColorRef("""{"role":{"a":1}}"""))
        assertNull(parseThemeColorRef("""{"argb":"red"}"""))
    }

    @Test
    fun `a role wins when the json carries both`() {
        assertEquals(
            ThemeColorRef.Role(ThemeColorRole.SURFACE),
            parseThemeColorRef("""{"role":"SURFACE","argb":5}""")
        )
    }
}
