package dev.mnascimentos.aureole.feature.home.components.widgets.model

import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WidgetDotsKeyTest {

    @Test
    fun `when portrait, should keep the plain id`() {
        assertEquals("widgets", widgetDotsKey("widgets", isLandscape = false))
    }

    @Test
    fun `when landscape, should use its own key for the same id`() {
        assertEquals("widgets_landscape", widgetDotsKey("widgets", isLandscape = true))
        assertNotEquals(widgetDotsKey("widgets", false), widgetDotsKey("widgets", true))
    }

    @Test
    fun `when the top row, should follow the same rule`() {
        assertEquals("top_widgets_landscape", widgetDotsKey("top_widgets", isLandscape = true))
    }

    @Test
    fun `when dots are off only in landscape, should read each orientation on its own`() {
        val dots = mapOf("widgets" to true, "widgets_landscape" to false)

        assertTrue(MainUiState(widgetStackDots = dots, isLandscape = false).isWidgetDotsEnabled("widgets"))
        assertFalse(MainUiState(widgetStackDots = dots, isLandscape = true).isWidgetDotsEnabled("widgets"))
    }

    @Test
    fun `when nothing is saved, should be on`() {
        assertTrue(MainUiState(isLandscape = true).isWidgetDotsEnabled("widgets"))
    }
}
