package dev.mnascimentos.aureole.core.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class IconCacheNamingTest {

    @Test
    fun `full bleed icons are stored with the fb suffix and legacy ones with plain png`() {
        assertEquals("com.app.fb.png", IconCacheNaming.fileName("com.app", fullBleed = true))
        assertEquals("com.app.png", IconCacheNaming.fileName("com.app", fullBleed = false))
    }

    @Test
    fun `the current cache folder differs from the one that held masked icons`() {
        assertNotEquals(IconCacheNaming.LEGACY_DIR_NAME, IconCacheNaming.DIR_NAME)
    }

    @Test
    fun `full bleed layers are scaled 1_5x and centered on the square`() {
        assertEquals(LayerBounds(-48, -48, 240, 240), AppIconHelper.fullBleedLayerBounds(192))
        assertEquals(LayerBounds(-12, -12, 60, 60), AppIconHelper.fullBleedLayerBounds(48))
    }
}
