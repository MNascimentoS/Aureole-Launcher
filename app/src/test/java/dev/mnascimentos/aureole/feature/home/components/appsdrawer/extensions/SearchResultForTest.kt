package dev.mnascimentos.aureole.feature.home.components.appsdrawer.extensions

import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import dev.mnascimentos.aureole.core.data.model.AppInfo
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchResultForTest {

    private fun app(label: String, pkg: String) = AppInfo(
        label = label,
        packageName = pkg,
        componentName = ComponentName(pkg, "MainActivity"),
        icon = ColorDrawable(Color.BLACK)
    )

    private val apps = listOf(app("Alpha", "com.a"), app("Beta", "com.b"), app("Alphabet", "com.c"))

    @Test
    fun `should apply the query together with the apps it filtered`() {
        val result = searchResultFor(apps, "alpha")

        assertEquals("alpha", result.appliedQuery)
        assertEquals(listOf("Alpha", "Alphabet"), result.filteredApps.map { it.label })
        assertEquals(emptyList<Char>(), result.alphabet)
    }

    @Test
    fun `should return all apps and the alphabet index for a blank query`() {
        val result = searchResultFor(apps, "  ")

        assertEquals("", result.appliedQuery)
        assertEquals(apps, result.filteredApps)
        assertEquals(listOf('A', 'B'), result.alphabet)
        assertEquals(mapOf('A' to 0, 'B' to 1), result.letterIndexMap)
    }

    @Test
    fun `should keep only the last query when two are applied in sequence`() {
        val first = searchResultFor(apps, "a")
        val last = searchResultFor(apps, "ab")

        assertEquals("a", first.appliedQuery)
        assertEquals("ab", last.appliedQuery)
        assertEquals(listOf("Alphabet"), last.filteredApps.map { it.label })
    }
}
