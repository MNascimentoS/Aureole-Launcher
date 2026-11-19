package dev.mnascimentos.aureole.core.designsystem.palette

import dev.mnascimentos.aureole.composable.color.model.RecommendedColors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** A background saved from the removed "Vivid" swatches goes back to the default palette (RF-03, D-04/D-11). */
class VividBackgroundMigrationTest {

    @Test
    fun `a Custom theme seeded by a Vivid color becomes the default palette`() {
        RecommendedColors.forEach { color ->
            assertEquals(frostbitePalette.name, vividBackgroundReplacement("Custom", color, RecommendedColors))
        }
        assertEquals(
            frostbitePalette.name,
            vividBackgroundReplacement("Custom Light", RecommendedColors[0], RecommendedColors)
        )
    }

    @Test
    fun `any other color, or a named palette, stays`() {
        assertNull(vividBackgroundReplacement("Custom", 0xFF112233.toInt(), RecommendedColors))
        assertNull(vividBackgroundReplacement(allPalettes[1].name, RecommendedColors[1], RecommendedColors))
        assertNull(vividBackgroundReplacement("Frostbite", RecommendedColors[0], RecommendedColors))
    }
}
