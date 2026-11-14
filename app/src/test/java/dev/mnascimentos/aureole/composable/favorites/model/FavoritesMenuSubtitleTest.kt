package dev.mnascimentos.aureole.composable.favorites.model

import org.junit.Assert.assertEquals
import org.junit.Test

/** The subtitle of "Choose favorites" follows the list's "Show all apps" switch (RF-10). */
class FavoritesMenuSubtitleTest {

    @Test
    fun `with the switch on, favorites sit on top of all apps, and none means all apps`() {
        assertEquals(FavoritesMenuSubtitle.COUNT_ON_TOP, favoritesMenuSubtitle(favoriteCount = 3, showAllApps = true))
        assertEquals(FavoritesMenuSubtitle.ALL_SHOWN, favoritesMenuSubtitle(favoriteCount = 0, showAllApps = true))
    }

    @Test
    fun `with the switch off, the rest stays hidden, and none means nothing is selected`() {
        assertEquals(FavoritesMenuSubtitle.COUNT, favoritesMenuSubtitle(favoriteCount = 2, showAllApps = false))
        assertEquals(FavoritesMenuSubtitle.NONE_SELECTED, favoritesMenuSubtitle(favoriteCount = 0, showAllApps = false))
    }

    @Test
    fun `without a switch (Settings), the old wording stays`() {
        assertEquals(FavoritesMenuSubtitle.COUNT, favoritesMenuSubtitle(favoriteCount = 1, showAllApps = null))
        assertEquals(FavoritesMenuSubtitle.ALL_SHOWN, favoritesMenuSubtitle(favoriteCount = 0, showAllApps = null))
    }
}
