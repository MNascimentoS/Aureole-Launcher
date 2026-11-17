package dev.mnascimentos.aureole.composable.favorites.model

import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import dev.mnascimentos.aureole.core.data.model.AppInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** The list of "Choose favorites": its two parts, the search (RF-04) and the notice without favorites (RF-03). */
class FavoriteAppsListRulesTest {

    private fun app(label: String): AppInfo {
        val packageName = "com.example.${label.lowercase()}"
        return AppInfo(
            label = label,
            packageName = packageName,
            componentName = ComponentName(packageName, "MainActivity"),
            icon = ColorDrawable(Color.BLACK)
        )
    }

    private val camera = app("Camera")
    private val clock = app("clock")
    private val maps = app("Maps")
    private val phone = app("Phone")
    private val apps = listOf(phone, maps, clock, camera)

    private fun labels(list: List<AppInfo>) = list.map { it.label }

    @Test
    fun `favorites keep their order, missing ones drop out and the rest is sorted ignoring case`() {
        val lists = favoriteAppsLists(apps, listOf(maps.packageName, "com.example.gone", phone.packageName))

        assertEquals(listOf("Maps", "Phone"), labels(lists.favorites))
        assertEquals(listOf("Camera", "clock"), labels(lists.remaining))
    }

    @Test
    fun `a blank query keeps every app`() {
        assertEquals(apps, filterAppsByQuery(apps, ""))
        assertEquals(apps, filterAppsByQuery(apps, "   "))
    }

    @Test
    fun `the query matches part of the name ignoring case`() {
        assertEquals(listOf("Camera", "clock"), labels(filterAppsByQuery(listOf(camera, clock, maps), "C")))
        assertEquals(listOf("Maps"), labels(filterAppsByQuery(apps, "aP")))
        assertEquals(emptyList<String>(), labels(filterAppsByQuery(apps, "xyz")))
    }

    @Test
    fun `the search filters favorites and the other apps alike`() {
        val lists = favoriteAppsLists(apps, listOf(phone.packageName, camera.packageName))

        val filtered = lists.filteredBy("o")

        assertEquals(listOf("Phone"), labels(filtered.favorites))
        assertEquals(listOf("clock"), labels(filtered.remaining))
    }

    @Test
    fun `the notice shows only without favorites and outside a search`() {
        assertNull(favoritesEmptyNotice(favoriteCount = 1, showAllApps = true, isSearching = false))
        assertNull(favoritesEmptyNotice(favoriteCount = 3, showAllApps = false, isSearching = false))
        assertNull(favoritesEmptyNotice(favoriteCount = 0, showAllApps = true, isSearching = true))
    }

    @Test
    fun `without favorites the notice follows Show all apps`() {
        assertEquals(
            FavoritesEmptyNotice.HOME_SHOWS_ALL,
            favoritesEmptyNotice(favoriteCount = 0, showAllApps = true, isSearching = false)
        )
        assertEquals(
            FavoritesEmptyNotice.HOME_STAYS_EMPTY,
            favoritesEmptyNotice(favoriteCount = 0, showAllApps = false, isSearching = false)
        )
    }

    @Test
    fun `without the switch, as in Settings, the Home falls back to every app`() {
        assertEquals(
            FavoritesEmptyNotice.HOME_SHOWS_ALL,
            favoritesEmptyNotice(favoriteCount = 0, showAllApps = null, isSearching = false)
        )
    }
}
