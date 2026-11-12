package dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions

import android.content.ComponentName
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.core.data.model.AppInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FolderAppPickerExtTest {

    private fun app(label: String, pkg: String) = AppInfo(
        label = label,
        packageName = pkg,
        componentName = ComponentName(pkg, "Main"),
        icon = 0xFF2E7D32.toInt().toDrawable()
    )

    private val apps = listOf(app("Galeria", "g"), app("Câmera", "c"), app("Camcorder", "cc"), app("Relógio", "r"))

    @Test
    fun `search ranks starts-with first, then alphabetical`() {
        assertEquals(listOf("cc", "c"), searchFolderApps(apps, "cam").map { it.packageName })
    }

    @Test
    fun `search ignores accents and case`() {
        assertEquals(listOf("r"), searchFolderApps(apps, "RELOGIO").map { it.packageName })
    }

    @Test
    fun `blank query or no match gives no results`() {
        assertTrue(searchFolderApps(apps, "").isEmpty())
        assertTrue(searchFolderApps(apps, "zzz").isEmpty())
    }

    @Test
    fun `enter marks the best result when it is not selected`() {
        val selected = mutableListOf("g")
        val toggled = toggleBestSearchResult(selected, searchFolderApps(apps, "cam"))
        assertEquals("cc", toggled)
        assertEquals(listOf("g", "cc"), selected)
    }

    @Test
    fun `enter unmarks the best result when it is selected`() {
        val selected = mutableListOf("cc", "g")
        toggleBestSearchResult(selected, searchFolderApps(apps, "cam"))
        assertEquals(listOf("g"), selected)
    }

    @Test
    fun `enter does nothing without results`() {
        val selected = mutableListOf("g")
        assertNull(toggleBestSearchResult(selected, emptyList()))
        assertEquals(listOf("g"), selected)
    }

    @Test
    fun `tray keeps selection order and skips missing packages`() {
        val byPackage = apps.associateBy { it.packageName }
        assertEquals(listOf("r", "g"), selectedTrayApps(listOf("r", "gone", "g"), byPackage).map { it.packageName })
    }

    @Test
    fun `togglePackageSelection adds and removes`() {
        val selected = mutableListOf<String>()
        togglePackageSelection(selected, "a")
        assertEquals(listOf("a"), selected)
        togglePackageSelection(selected, "a")
        assertTrue(selected.isEmpty())
    }

    @Test
    fun `moveUp and moveDown swap neighbours and ignore the edges`() {
        val list = mutableListOf("a", "b", "c")
        list.moveUp(2)
        assertEquals(listOf("a", "c", "b"), list)
        list.moveUp(0)
        list.moveDown(2)
        list.moveDown(9)
        assertEquals(listOf("a", "c", "b"), list)
        list.moveDown(0)
        assertEquals(listOf("c", "a", "b"), list)
    }
}
