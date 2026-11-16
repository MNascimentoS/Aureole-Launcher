package dev.mnascimentos.aureole.feature.home.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AllAppsOpenSourceTest {

    @Test
    fun `when opened from the bottom, should slide up for either hand`() {
        assertEquals(DrawerMotion.FROM_BOTTOM, drawerMotionFor(AllAppsOpenSource.BOTTOM, isLeftHanded = false))
        assertEquals(DrawerMotion.FROM_BOTTOM, drawerMotionFor(AllAppsOpenSource.BOTTOM, isLeftHanded = true))
    }

    @Test
    fun `when opened from the edge, should come from the pulled side`() {
        assertEquals(DrawerMotion.FROM_RIGHT, drawerMotionFor(AllAppsOpenSource.EDGE, isLeftHanded = false))
        assertEquals(DrawerMotion.FROM_LEFT, drawerMotionFor(AllAppsOpenSource.EDGE, isLeftHanded = true))
    }

    @Test
    fun `when swipe up, home button or quick button open the list, should record the bottom`() {
        val edgeBefore = MainUiState(allAppsOpenSource = AllAppsOpenSource.EDGE)

        val opened = edgeBefore.withAllAppsDrawer(open = true)

        assertTrue(opened.isAllAppsDrawerOpen)
        assertEquals(AllAppsOpenSource.BOTTOM, opened.allAppsOpenSource)
    }

    @Test
    fun `when the edge scrub opens the list, should record the edge`() {
        val opened = MainUiState().withAllAppsDrawer(open = true, source = AllAppsOpenSource.EDGE)

        assertEquals(AllAppsOpenSource.EDGE, opened.allAppsOpenSource)
    }

    @Test
    fun `when the list closes, should keep the source so it leaves the same way`() {
        val open = MainUiState().withAllAppsDrawer(open = true, source = AllAppsOpenSource.EDGE)

        val closed = open.withAllAppsDrawer(open = false)

        assertFalse(closed.isAllAppsDrawerOpen)
        assertEquals(AllAppsOpenSource.EDGE, closed.allAppsOpenSource)
    }

    @Test
    fun `when already open, a second open should not change the source`() {
        val open = MainUiState().withAllAppsDrawer(open = true, source = AllAppsOpenSource.EDGE)

        val again = open.withAllAppsDrawer(open = true, source = AllAppsOpenSource.BOTTOM)

        assertEquals(AllAppsOpenSource.EDGE, again.allAppsOpenSource)
    }

    @Test
    fun `when nothing opened the list yet, should default to the bottom`() {
        assertEquals(AllAppsOpenSource.BOTTOM, MainUiState().allAppsOpenSource)
    }
}
