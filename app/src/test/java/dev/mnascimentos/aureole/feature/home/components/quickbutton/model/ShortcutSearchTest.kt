package dev.mnascimentos.aureole.feature.home.components.quickbutton.model

import android.content.ComponentName
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ShortcutSearchTest {

    private fun app(label: String, pkg: String) = AppInfo(
        label = label,
        packageName = pkg,
        componentName = ComponentName(pkg, "Main"),
        icon = 0xFF2E7D32.toInt().toDrawable()
    )

    private val defaults = listOf(
        ShortcutAction.AllApps,
        ShortcutAction.Settings,
        ShortcutAction.systemIntent("Camera"),
        ShortcutAction.systemIntent("Calendar")
    )
    private val apps = listOf(app("Câmera Pro", "com.pro"), app("Maps", "com.maps"))

    @Test
    fun `candidates include defaults and apps`() {
        val candidates = buildShortcutSearchCandidates(defaults, apps, onlyApps = false)
        assertEquals(defaults.size + apps.size, candidates.size)
        assertNull(candidates.first().app)
        assertEquals(ShortcutAction.openApp("com.pro"), candidates[defaults.size].action)
    }

    @Test
    fun `candidates are only apps while picking an app`() {
        val candidates = buildShortcutSearchCandidates(defaults, apps, onlyApps = true)
        assertEquals(apps.map { it.label }, candidates.map { it.label })
    }

    @Test
    fun `search ranks starts-with first and ignores accents`() {
        val candidates = buildShortcutSearchCandidates(defaults, apps, onlyApps = false)
        val results = searchShortcuts(candidates, "CAM")
        assertEquals(listOf("Camera", "Câmera Pro"), results.map { it.label })
    }

    @Test
    fun `blank query gives no results`() {
        val candidates = buildShortcutSearchCandidates(defaults, apps, onlyApps = false)
        assertTrue(searchShortcuts(candidates, "  ").isEmpty())
    }

    @Test
    fun `best action is the first result or null`() {
        val candidates = buildShortcutSearchCandidates(defaults, apps, onlyApps = false)
        assertEquals(ShortcutAction.systemIntent("Camera"), bestShortcutAction(searchShortcuts(candidates, "cam")))
        assertEquals(ShortcutAction.openApp("com.maps"), bestShortcutAction(searchShortcuts(candidates, "maps")))
        assertNull(bestShortcutAction(searchShortcuts(candidates, "zzz")))
    }
}
