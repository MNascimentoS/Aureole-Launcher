package dev.mnascimentos.aureole.feature.home.components.favorites.model

import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import dev.mnascimentos.aureole.core.data.model.AppInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppsListSectionsTest {

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
    private val clock = app("Clock")
    private val maps = app("Maps")
    private val phone = app("Phone")
    private val apps = listOf(camera, clock, maps, phone)

    private fun labels(list: List<AppInfo>) = list.map { it.label }

    @Test
    fun `without favorites and showing all, should list every app in order with no favorites part`() {
        val sections = appsListSections(apps, favorites = emptyList(), showAll = true)

        assertTrue(sections.favorites.isEmpty())
        assertEquals(labels(apps), labels(sections.others))
        assertFalse(sections.isEmpty)
        assertTrue(sections.showsAll)
    }

    @Test
    fun `with favorites and showing all, should put them on top in their order and not repeat them below`() {
        val sections = appsListSections(apps, favorites = listOf(phone.packageName, camera.packageName), showAll = true)

        assertEquals(listOf("Phone", "Camera"), labels(sections.favorites))
        assertEquals(listOf("Clock", "Maps"), labels(sections.others))
    }

    @Test
    fun `with the toggle off, should list only the favorites`() {
        val sections = appsListSections(apps, favorites = listOf(maps.packageName), showAll = false)

        assertEquals(listOf("Maps"), labels(sections.favorites))
        assertTrue(sections.others.isEmpty())
        assertFalse(sections.showsAll)
    }

    @Test
    fun `with the toggle off and no favorites, should be empty`() {
        val sections = appsListSections(apps, favorites = emptyList(), showAll = false)

        assertTrue(sections.isEmpty)
    }

    @Test
    fun `a favorite that is no longer installed should be skipped`() {
        val sections = appsListSections(
            apps,
            favorites = listOf("com.example.gone", clock.packageName),
            showAll = false
        )

        assertEquals(listOf("Clock"), labels(sections.favorites))
    }

    @Test
    fun `only uninstalled favorites with the toggle off should be empty`() {
        assertTrue(appsListSections(apps, favorites = listOf("com.example.gone"), showAll = false).isEmpty)
    }

    @Test
    fun `a favorite saved twice should show once`() {
        val sections = appsListSections(apps, favorites = listOf(clock.packageName, clock.packageName), showAll = true)

        assertEquals(listOf("Clock"), labels(sections.favorites))
    }

    @Test
    fun `the limit of a list inside a scroll view should apply only to the other apps`() {
        val sections = appsListSections(
            apps,
            favorites = listOf(phone.packageName, maps.packageName),
            showAll = true,
            maxOthers = 1
        )

        assertEquals(listOf("Phone", "Maps"), labels(sections.favorites))
        assertEquals(listOf("Camera"), labels(sections.others))
    }

    @Test
    fun `without installed apps and showing all, should be empty`() {
        assertTrue(appsListSections(emptyList(), favorites = listOf(phone.packageName), showAll = true).isEmpty)
    }
}
