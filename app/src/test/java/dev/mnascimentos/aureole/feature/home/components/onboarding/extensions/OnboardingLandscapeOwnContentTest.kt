package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import dev.mnascimentos.aureole.feature.home.grid.extensions.flattenWithChildren
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** The landscape copy of the onboarding gives its own, empty content to containers and widgets (RF-L1 to RF-L6). */
class OnboardingLandscapeOwnContentTest {

    private fun item(type: LauncherItemType, id: String = "p_${type.name}", col: Int = 1) = LauncherItemState(
        id = id,
        type = type,
        col = col,
        row = 2,
        colSpan = 3,
        rowSpan = 4,
        widgetId = 9,
        children = listOf(LauncherItemState(id = "child_$id", type = LauncherItemType.SINGLE_APP_WIDGET, widgetId = 5))
    )

    private fun counter(vararg ids: String): () -> String {
        val queue = ArrayDeque(ids.toList())
        var next = 0
        return { queue.removeFirstOrNull() ?: "new_${next++}" }
    }

    private fun LauncherItemState.box() = listOf(col, row, colSpan, rowSpan, minColSpan, minRowSpan)

    private val ownTypes = listOf(
        LauncherItemType.SHORTCUTS_CONTAINER,
        LauncherItemType.WIDGET_LIST,
        LauncherItemType.SCROLL_VIEW,
        LauncherItemType.SINGLE_APP_WIDGET
    )

    @Test
    fun `each of the four types should get a new id and come empty`() {
        ownTypes.forEach { type ->
            val portrait = listOf(item(type))

            val copy = portrait.withOwnLandscapeContent(portrait, counter()).single()

            assertEquals("$type", "new_0", copy.id)
            assertNull("$type", copy.widgetId)
            assertTrue("$type", copy.safeChildren.isEmpty())
        }
    }

    @Test
    fun `the clock, apps list and quick button should keep their id and content`() {
        val quick = item(LauncherItemType.QUICK_BUTTON).copy(quickButtonModel = QuickButtonModel())
        val portrait = listOf(item(LauncherItemType.CLOCK), item(LauncherItemType.APPS_LIST), quick)

        assertEquals(portrait, portrait.withOwnLandscapeContent(portrait, counter()))
    }

    @Test
    fun `a new id should never collide with the portrait, its children or the copy`() {
        val portrait = listOf(
            item(LauncherItemType.SHORTCUTS_CONTAINER, id = "a"),
            item(LauncherItemType.CLOCK, id = "b")
        )

        val copy = portrait.withOwnLandscapeContent(portrait, counter("a", "b", "child_a", "fresh"))

        assertEquals(listOf("fresh", "b"), copy.map { it.id })
    }

    @Test
    fun `two components of the same type should get different ids`() {
        val portrait = listOf(
            item(LauncherItemType.WIDGET_LIST, id = "w1"),
            item(LauncherItemType.WIDGET_LIST, id = "w2", col = 5)
        )

        val ids = portrait.withOwnLandscapeContent(portrait, counter("x", "x", "y")).map { it.id }

        assertEquals(listOf("x", "y"), ids)
    }

    @Test
    fun `position and size should stay exactly as given`() {
        val portrait = (ownTypes + LauncherItemType.CLOCK).mapIndexed { index, type -> item(type, col = index) }

        val copy = portrait.withOwnLandscapeContent(portrait, counter())

        assertEquals(portrait.map { it.box() }, copy.map { it.box() })
        assertEquals(portrait.map { it.safeType }, copy.map { it.safeType })
    }

    @Test
    fun `a mixed onboarding layout should keep the conversion geometry and share only the clock and apps`() {
        val portrait = listOf(
            item(LauncherItemType.CLOCK),
            item(LauncherItemType.APPS_LIST),
            item(LauncherItemType.WIDGET_LIST),
            item(LauncherItemType.SHORTCUTS_CONTAINER)
        )

        val layout = onboardingLandscapeLayout(portrait, isLeftHanded = false, newId = counter())

        // The same spots the conversion gave before the change: the default landscape layout.
        GridDefaults.getDefaultGridItems(isLandscape = true, isLeftHanded = false).forEach { slot ->
            assertEquals("${slot.safeType}", slot.box(), layout.single { it.safeType == slot.safeType }.box())
        }
        val portraitIds = portrait.flattenWithChildren().map { it.id }.toSet()
        layout.forEach { copy ->
            val isShared = copy.safeType == LauncherItemType.CLOCK || copy.safeType == LauncherItemType.APPS_LIST
            assertEquals("${copy.safeType}", isShared, copy.id in portraitIds)
        }
        layout.filter { it.safeType in ownTypes }.forEach { copy ->
            assertTrue(copy.safeChildren.isEmpty())
            assertNull(copy.widgetId)
        }
    }
}
