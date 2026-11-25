package dev.mnascimentos.aureole.feature.home.components.onboarding.model

import android.content.pm.ApplicationInfo
import dev.mnascimentos.aureole.core.data.model.LayoutPreset
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.core.data.model.PresetApp
import dev.mnascimentos.aureole.core.data.model.PresetCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LayoutPresetSelectionTest {

    private val social = ApplicationInfo.CATEGORY_SOCIAL
    private val productivity = ApplicationInfo.CATEGORY_PRODUCTIVITY
    private val audio = ApplicationInfo.CATEGORY_AUDIO
    private val none = ApplicationInfo.CATEGORY_UNDEFINED

    private fun app(pkg: String, label: String, category: Int = none) = PresetApp(pkg, label, category)

    private val names = mapOf(social to "Social", productivity to "Productivity", audio to "Audio")

    private val catalog = PresetCatalog(
        apps = listOf(
            app("z.notes", "Notes", productivity),
            app("a.chat", "Chat", social),
            app("p.phone", "Phone"),
            app("m.music", "Music", audio),
            app("b.mail", "Mail", productivity),
            app("c.photos", "Photos"),
            app("d.feed", "Feed", social),
            app("c.camera", "Camera")
        ),
        rolePackages = listOf("p.phone", null, "c.camera", "not.installed", "p.phone", null),
        categoryNames = names
    )

    @Test
    fun `ranking should put installed role apps first in role order, then categories, then the rest`() {
        assertEquals(
            listOf("p.phone", "c.camera", "a.chat", "d.feed", "b.mail", "z.notes", "m.music", "c.photos"),
            catalog.rankedPackages()
        )
    }

    @Test
    fun `ranking should be the same whatever the order the apps came in`() {
        val shuffled = catalog.copy(apps = catalog.apps.reversed())

        assertEquals(catalog.rankedPackages(), shuffled.rankedPackages())
    }

    @Test
    fun `an app with two launcher entries should count once`() {
        val twice = catalog.copy(apps = catalog.apps + app("a.chat", "Chat 2", social))

        assertEquals(catalog.rankedPackages(), twice.rankedPackages())
    }

    @Test
    fun `folders should come from categories with two apps or more, in category order and alphabetical`() {
        val folders = catalog.categoryFolders()

        assertEquals(listOf("Social", "Productivity"), folders.map { it.name })
        assertEquals(listOf("a.chat", "d.feed"), folders[0].packageNames)
        assertEquals(listOf("b.mail", "z.notes"), folders[1].packageNames)
    }

    @Test
    fun `a category with one app should not become a folder`() {
        assertTrue(catalog.categoryFolders().none { it.category == audio })
    }

    @Test
    fun `a folder should hold at most nine apps`() {
        val many = PresetCatalog(
            apps = (1..12).map { app("pkg.$it", "App ${it.toString().padStart(2, '0')}", social) },
            categoryNames = names
        )

        val folder = many.categoryFolders().single()

        assertEquals(PRESET_FOLDER_MAX_APPS, folder.packageNames.size)
        assertEquals("pkg.1", folder.packageNames.first())
    }

    @Test
    fun `a category with no name should not become a folder`() {
        assertTrue(catalog.copy(categoryNames = emptyMap()).categoryFolders().isEmpty())
    }

    @Test
    fun `minimal should take five favorites and no group`() {
        val plan = catalog.planFor(LayoutPreset.MINIMAL)

        assertEquals(listOf("p.phone", "c.camera", "a.chat", "d.feed", "b.mail"), plan.favorites)
        assertTrue(plan.folders.isEmpty() && plan.groupApps.isEmpty())
        assertEquals(PresetGroupKind.NONE, plan.groupKind)
    }

    @Test
    fun `productive should take four favorites, the folders and one app outside them`() {
        val plan = catalog.planFor(LayoutPreset.PRODUCTIVE)

        assertEquals(listOf("p.phone", "c.camera", "a.chat", "d.feed"), plan.favorites)
        assertEquals(2, plan.folders.size)
        assertEquals(listOf("m.music"), plan.groupApps)
        assertEquals(PresetGroupKind.SIDE, plan.groupKind)
    }

    @Test
    fun `widgets should take three favorites and a dock of the next four apps`() {
        val plan = catalog.planFor(LayoutPreset.WIDGETS)

        assertEquals(listOf("p.phone", "c.camera", "a.chat"), plan.favorites)
        assertEquals(listOf("d.feed", "b.mail", "z.notes", "m.music"), plan.groupApps)
        assertEquals(PresetGroupKind.DOCK, plan.groupKind)
    }

    @Test
    fun `with no apps, a preset should place no favorites and no group`() {
        val plan = PresetCatalog().planFor(LayoutPreset.PRODUCTIVE)

        assertTrue(plan.favorites.isEmpty())
        assertEquals(PresetGroupKind.NONE, plan.groupKind)
    }

    @Test
    fun `plan all should list the three presets in order`() {
        assertEquals(LayoutPreset.entries, catalog.planAll().map { it.preset })
    }

    @Test
    fun `minimal should complete the clock, favorites, group and widget it has none of`() {
        assertEquals(presetLayoutTasks, catalog.planFor(LayoutPreset.MINIMAL).completedTasks())
    }

    @Test
    fun `presets with a widget should leave the widget step to do`() {
        val tasks = catalog.planFor(LayoutPreset.PRODUCTIVE).completedTasks()

        assertEquals(presetLayoutTasks - OnboardingTask.ADD_WIDGET, tasks)
    }

    @Test
    fun `a group the device had no apps for should leave the group step to do`() {
        val tasks = PresetCatalog().planFor(LayoutPreset.WIDGETS).completedTasks()

        assertEquals(setOf(OnboardingTask.ADD_CLOCK), tasks)
    }
}
