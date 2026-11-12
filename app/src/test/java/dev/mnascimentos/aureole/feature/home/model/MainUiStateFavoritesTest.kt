package dev.mnascimentos.aureole.feature.home.model

import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MainUiStateFavoritesTest {

    private val state = MainUiState(
        favoriteAppPackages = listOf("global"),
        containerFavorites = mapOf("list_a" to listOf("a"), "empty" to emptyList())
    )

    @Test
    fun `a list with saved favorites returns its own`() {
        assertEquals(listOf("a"), state.favoritesOf("list_a"))
    }

    @Test
    fun `a saved empty list stays empty and does not fall back to the global favorites`() {
        assertEquals(emptyList<String>(), state.favoritesOf("empty"))
    }

    @Test
    fun `a new list with nothing saved starts empty instead of inheriting the global favorites`() {
        assertEquals(emptyList<String>(), state.favoritesOf("brand_new_uuid"))
    }

    @Test
    fun `the legacy list without a saved row keeps the global favorites`() {
        assertEquals(listOf("global"), state.favoritesOf(GridDefaults.APPS_LIST_ITEM_ID))
        assertEquals(listOf("global"), state.favoritesOf(FavoriteContainerRepository.DEFAULT_CONTAINER_ID))
        assertEquals(listOf("global"), state.favoritesOf(null))
    }

    @Test
    fun `the legacy list with a saved row uses the row`() {
        val saved = state.copy(containerFavorites = mapOf(GridDefaults.APPS_LIST_ITEM_ID to listOf("x")))

        assertEquals(listOf("x"), saved.favoritesOf(GridDefaults.APPS_LIST_ITEM_ID))
    }

    @Test
    fun `only the legacy ids and no id count as the legacy list`() {
        assertTrue(isLegacyFavoritesId(null))
        assertTrue(isLegacyFavoritesId(GridDefaults.APPS_LIST_ITEM_ID))
        assertFalse(isLegacyFavoritesId("some_uuid"))
    }
}
