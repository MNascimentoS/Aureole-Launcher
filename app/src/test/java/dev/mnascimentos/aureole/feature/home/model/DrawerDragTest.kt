package dev.mnascimentos.aureole.feature.home.model

import org.junit.Assert.assertEquals
import org.junit.Test

class DrawerDragTest {

    @Test
    fun `progress should follow the finger up to the travel and stay in 0 to 1`() {
        assertEquals(0f, drawerProgressOf(-50f, 1000f), 0f)
        assertEquals(0.5f, drawerProgressOf(300f, 1000f), 0.0001f)
        assertEquals(1f, drawerProgressOf(5000f, 1000f), 0f)
        assertEquals(0f, drawerProgressOf(100f, 0f), 0f)
    }

    @Test
    fun `when released past the limit or flung up, should open`() {
        assertEquals(DrawerSettle.OPEN, drawerSettleOf(DRAWER_OPEN_PROGRESS, 0f))
        assertEquals(DrawerSettle.OPEN, drawerSettleOf(0.05f, DRAWER_OPEN_FLING_DP_PER_S))
    }

    @Test
    fun `when released short and slow, or flung down, should come back`() {
        assertEquals(DrawerSettle.CLOSE, drawerSettleOf(0.29f, 200f))
        assertEquals(DrawerSettle.CLOSE, drawerSettleOf(0.9f, -DRAWER_OPEN_FLING_DP_PER_S))
    }

    @Test
    fun `closing by drag should mirror the opening limits`() {
        assertEquals(DrawerSettle.CLOSE, drawerCloseSettleOf(closedProgress = 0.3f, downDpPerSec = 0f))
        assertEquals(DrawerSettle.CLOSE, drawerCloseSettleOf(closedProgress = 0.05f, downDpPerSec = 1200f))
        assertEquals(DrawerSettle.OPEN, drawerCloseSettleOf(closedProgress = 0.1f, downDpPerSec = 100f))
        assertEquals(DrawerSettle.OPEN, drawerCloseSettleOf(closedProgress = 0.8f, downDpPerSec = -1200f))
    }

    @Test
    fun `the home should dim to 40 percent and shrink to 0,94 at most`() {
        assertEquals(0f, homeDimAlpha(0f), 0f)
        assertEquals(HOME_DIM_MAX, homeDimAlpha(2f), 0f)
        assertEquals(1f, homeScale(0f), 0f)
        assertEquals(HOME_SCALE_MIN, homeScale(1f), 0.0001f)
    }
}
