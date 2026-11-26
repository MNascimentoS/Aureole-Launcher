package dev.mnascimentos.aureole.core.data.model

import dev.mnascimentos.aureole.core.data.repository.toContainerEntity
import dev.mnascimentos.aureole.core.data.repository.toContainerModel
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** The group colors as references (grupo-apps-pastas RF-E3, D-12) and the item spacing (RF-D3). */
class ContainerColorsTest {

    @Test
    fun `when the old background was off, should migrate to no background`() {
        val colors = migratedContainerColors(isBackgroundEnabled = false, legacyBackgroundArgb = LEGACY_ARGB)

        assertNull(colors.background)
        assertEquals(CONTAINER_ITEM_THEME_REF, colors.item)
    }

    @Test
    fun `when the old background was on, should migrate to the theme background`() {
        assertEquals(CONTAINER_BACKGROUND_THEME_REF, migratedContainerColors(true, null).background)
        assertEquals(CONTAINER_BACKGROUND_THEME_REF, migratedContainerColors(true, 0L).background)
    }

    @Test
    fun `when the old background had its own color, should keep it as a fixed color`() {
        val colors = migratedContainerColors(isBackgroundEnabled = true, legacyBackgroundArgb = LEGACY_ARGB)

        assertEquals(ThemeColorRef.Fixed(LEGACY_ARGB.toInt()), colors.background)
    }

    @Test
    fun `a new group should start with no background and the theme items`() {
        assertEquals(ContainerColorRefs(null, CONTAINER_ITEM_THEME_REF), newContainerColors())
    }

    @Test
    fun `the spacing should read its saved name, or Normal`() {
        assertEquals(ContainerItemSpacing.WIDE, containerItemSpacingOf("WIDE"))
        assertEquals(ContainerItemSpacing.NORMAL, containerItemSpacingOf("BIG"))
        assertEquals(ContainerItemSpacing.NORMAL, containerItemSpacingOf(null))
    }

    @Test
    fun `the colors and spacing should survive a save and a read`() {
        val model = ContainerModel(
            id = "p",
            isBackgroundEnabled = true,
            backgroundColorRef = ThemeColorRef.Fixed(LEGACY_ARGB.toInt()),
            itemColorRef = null,
            itemSpacing = ContainerItemSpacing.TIGHT
        )

        val read = model.toContainerEntity().toContainerModel(items = emptyList(), folders = emptyList())

        assertEquals(model.backgroundColorRef, read.backgroundColorRef)
        assertNull(read.itemColorRef)
        assertEquals(ContainerItemSpacing.TIGHT, read.itemSpacing)
    }

    private companion object {
        const val LEGACY_ARGB = 0xFF336699L
    }
}
