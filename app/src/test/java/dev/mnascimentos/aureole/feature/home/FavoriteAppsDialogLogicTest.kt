package dev.mnascimentos.aureole.feature.home

import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import dev.mnascimentos.aureole.core.data.model.AppInfo
import org.junit.Assert.assertEquals
import org.junit.Test

class FavoriteAppsDialogLogicTest {

    private fun createApp(label: String, pkg: String) = AppInfo(
        label = label,
        packageName = pkg,
        componentName = ComponentName(pkg, "MainActivity"),
        icon = ColorDrawable(Color.BLACK)
    )

    @Test
    fun testActivateAllAddsRemainingAppsToFavorites() {
        val app1 = createApp("App 1", "com.example.app1")
        val app2 = createApp("App 2", "com.example.app2")
        val app3 = createApp("App 3", "com.example.app3")

        val allApps = listOf(app1, app2, app3)
        val initialFavs = listOf("com.example.app1")

        val remainingApps = allApps.filter { it.packageName !in initialFavs }
        val toAdd = remainingApps.map { it.packageName }
        val updatedFavs = (initialFavs + toAdd).distinct()

        assertEquals(3, updatedFavs.size)
        assertEquals(listOf("com.example.app1", "com.example.app2", "com.example.app3"), updatedFavs)
    }

    @Test
    fun testDeactivateAllRemovesFavorites() {
        val initialFavs = listOf("com.example.app1", "com.example.app2")
        val filteredFavorites = listOf(
            createApp("App 1", "com.example.app1"),
            createApp("App 2", "com.example.app2")
        )

        val toRemove = filteredFavorites.map { it.packageName }.toSet()
        val updatedFavs = initialFavs.filter { it !in toRemove }

        assertEquals(0, updatedFavs.size)
    }

    @Test
    fun testActivateAndDeactivateWithSearchFilter() {
        val app1 = createApp("Alpha", "com.example.alpha")
        val app2 = createApp("Beta", "com.example.beta")
        val app3 = createApp("Gamma", "com.example.gamma")

        val allApps = listOf(app1, app2, app3)
        val initialFavs = listOf("com.example.alpha")

        // Search query "Beta"
        val filteredRemaining = allApps.filter {
            it.packageName !in initialFavs && it.label.contains("Beta", ignoreCase = true)
        }
        val toAdd = filteredRemaining.map { it.packageName }
        val updatedFavs = (initialFavs + toAdd).distinct()

        assertEquals(listOf("com.example.alpha", "com.example.beta"), updatedFavs)

        // Deactivate filtered favorites matching "Alpha"
        val filteredFavs = listOf(app1)
        val toRemove = filteredFavs.map { it.packageName }.toSet()
        val finalFavs = updatedFavs.filter { it !in toRemove }

        assertEquals(listOf("com.example.beta"), finalFavs)
    }
}
