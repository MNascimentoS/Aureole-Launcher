package dev.mnascimentos.aureole.feature.home.components.favorites.model

import dev.mnascimentos.aureole.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The favorite toggle and its undo, on the plain JVM. Replaces the two favorite cases of HomeViewModelUndoTest, which
 * raced the ViewModel's initial load from Room and failed on slower CI machines.
 */
class FavoriteToggleTest {

    @Test
    fun `adding a favorite appends it and offers the added message`() {
        val toggle = toggleFavoriteIn(listOf("a"), "b")

        assertEquals(listOf("a", "b"), toggle.favorites)
        assertFalse(toggle.wasFavorite)
        assertEquals(R.string.home_undo_favorite_added, toggle.undoMessageRes)
    }

    @Test
    fun `removing a favorite takes it out and offers the removed message`() {
        val toggle = toggleFavoriteIn(listOf("a", "b"), "b")

        assertEquals(listOf("a"), toggle.favorites)
        assertTrue(toggle.wasFavorite)
        assertEquals(R.string.home_undo_favorite_removed, toggle.undoMessageRes)
    }

    @Test
    fun `removing from the middle keeps the order of the others`() {
        val toggle = toggleFavoriteIn(listOf("a", "b", "c"), "b")

        assertEquals(listOf("a", "c"), toggle.favorites)
    }

    @Test
    fun `the first favorite of an empty list is added`() {
        val toggle = toggleFavoriteIn(emptyList(), "a")

        assertEquals(listOf("a"), toggle.favorites)
        assertFalse(toggle.wasFavorite)
    }

    @Test
    fun `the previous list is not changed, so undo can restore it as it was`() {
        // toggleFavorite's undo saves this same previous list back, order and position included.
        val previous = mutableListOf("a", "b", "c")

        toggleFavoriteIn(previous, "b")

        assertEquals(listOf("a", "b", "c"), previous)
    }
}
