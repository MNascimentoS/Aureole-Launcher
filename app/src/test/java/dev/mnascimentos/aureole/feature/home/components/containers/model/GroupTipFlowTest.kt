package dev.mnascimentos.aureole.feature.home.components.containers.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** The three tips of the first group (grupo-apps-pastas RF-F1, RF-F2, D-07). */
class GroupTipFlowTest {

    private var finished = 0

    private fun flow(initial: GroupTip? = GroupTip.FIRST_APP) = GroupTipFlow(initial) { finished++ }

    @Test
    fun `the tips should show only on a new group while never seen`() {
        assertEquals(GroupTip.FIRST_APP, firstGroupTip(isNew = true, areTipsSeen = false))
        assertNull(firstGroupTip(isNew = true, areTipsSeen = true))
        assertNull(firstGroupTip(isNew = false, areTipsSeen = false))
    }

    @Test
    fun `the tips should go from the first app to the position and end there`() {
        val tips = flow()

        tips.onAppsPageClosed(itemCount = 0)
        assertEquals(GroupTip.FIRST_APP, tips.tip)

        tips.onAppsPageClosed(itemCount = 1)
        assertEquals(GroupTip.CHOOSE_POSITION, tips.tip)
        assertEquals(GroupTab.ITEMS, tips.tip?.tab)

        tips.onTabSelected(GroupTab.APPEARANCE)
        assertEquals(GroupTip.CHOOSE_POSITION, tips.tip)

        tips.onTabSelected(GroupTab.LAYOUT)
        assertEquals(GroupTip.PICK_POSITION, tips.tip)
        assertEquals(GroupTab.LAYOUT, tips.tip?.tab)
        assertEquals(0, finished)

        tips.onPositionPicked()
        assertNull(tips.tip)
        assertEquals(1, finished)
    }

    @Test
    fun `skipping should end the tips and save it once`() {
        val tips = flow()

        tips.skip()
        tips.skip()
        tips.onPositionPicked()

        assertNull(tips.tip)
        assertEquals(1, finished)
    }

    @Test
    fun `with no tip, nothing should change and nothing be saved`() {
        val tips = flow(initial = null)

        tips.onAppsPageClosed(itemCount = 3)
        tips.onTabSelected(GroupTab.LAYOUT)
        tips.onPositionPicked()

        assertNull(tips.tip)
        assertEquals(0, finished)
    }

    @Test
    fun `picking a position before tip 3 should not end the tips`() {
        val tips = flow()

        tips.onPositionPicked()

        assertEquals(GroupTip.FIRST_APP, tips.tip)
        assertEquals(0, finished)
    }
}
