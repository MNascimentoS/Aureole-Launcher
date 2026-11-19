package dev.mnascimentos.aureole.feature.home.components.containers.folder.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * RF-16/RF-17, in px at density 1 (1 px = 1 dp): a 800 dp tall home, 24 dp status bar, 16 dp margin and the
 * 400 dp cap of the list folder; 48 dp for 3-button navigation and 16 dp for the gesture pill.
 */
class FolderPopupPlacementTest {

    private fun bounds(navInsetPx: Float, screenHeightPx: Float = 800f) = FolderPopupBounds(
        screenHeightPx = screenHeightPx,
        statusInsetPx = STATUS,
        navInsetPx = navInsetPx,
        marginPx = MARGIN,
        heightCapPx = CAP
    )

    @Test
    fun `when the folder is near the top, should open below it with its own height`() {
        val placement = folderPopupPlacement(anchorTopPx = 100f, popupHeightPx = 300f, bounds = bounds(BUTTONS))

        assertEquals(100f, placement.topPx, 0f)
        assertEquals(300f, placement.heightPx, 0f)
    }

    @Test
    fun `when the folder is in the middle and the popup still fits, should stay lined up with it`() {
        // Bottom limit with 3 buttons: 800 - 48 - 16 = 736; 400 + 300 = 700 fits.
        val placement = folderPopupPlacement(anchorTopPx = 400f, popupHeightPx = 300f, bounds = bounds(BUTTONS))

        assertEquals(400f, placement.topPx, 0f)
    }

    @Test
    fun `when the folder is at the end of the screen, should move up until it sits above the 3-button bar`() {
        val placement = folderPopupPlacement(anchorTopPx = 650f, popupHeightPx = 400f, bounds = bounds(BUTTONS))

        assertEquals(736f - 400f, placement.topPx, 0f)
        assertEquals(736f, placement.topPx + placement.heightPx, 0f)
    }

    @Test
    fun `when the folder is at the end with gesture navigation, should end above the pill margin`() {
        val placement = folderPopupPlacement(anchorTopPx = 700f, popupHeightPx = 300f, bounds = bounds(GESTURES))

        // 800 - 16 - 16 = 768.
        assertEquals(768f, placement.topPx + placement.heightPx, 0f)
    }

    @Test
    fun `when the content is taller than the cap, should be capped at 400`() {
        val placement = folderPopupPlacement(anchorTopPx = 100f, popupHeightPx = 900f, bounds = bounds(BUTTONS))

        assertEquals(CAP, placement.heightPx, 0f)
    }

    @Test
    fun `when even the cap does not fit, should take the usable height under the status bar`() {
        // Landscape-like: 360 tall -> usable 360 - 24 - 48 - 16 = 272.
        val short = bounds(BUTTONS, screenHeightPx = 360f)
        val placement = folderPopupPlacement(anchorTopPx = 300f, popupHeightPx = 900f, bounds = short)

        assertEquals(272f, short.maxHeightPx, 0f)
        assertEquals(272f, placement.heightPx, 0f)
        assertEquals(STATUS, placement.topPx, 0f)
    }

    @Test
    fun `when the anchor is above the status bar, should start right below it`() {
        val placement = folderPopupPlacement(anchorTopPx = -10f, popupHeightPx = 200f, bounds = bounds(BUTTONS))

        assertEquals(STATUS, placement.topPx, 0f)
    }

    @Test
    fun `the popup should never reach the navigation bar`() {
        listOf(BUTTONS, GESTURES).forEach { nav ->
            listOf(0f, 200f, 500f, 790f).forEach { anchor ->
                val b = bounds(nav)
                val placement = folderPopupPlacement(anchor, popupHeightPx = 380f, bounds = b)
                assertTrue(placement.topPx + placement.heightPx <= 800f - nav - MARGIN)
                assertTrue(placement.topPx >= STATUS)
            }
        }
    }

    @Test
    fun `grid folder should be centered in the usable height`() {
        val b = bounds(BUTTONS)
        val placement = gridFolderPopupPlacement(popupHeightPx = 312f, bounds = b)

        // Usable 24..736 (712 tall): (712 - 312) / 2 = 200 below the status bar.
        assertEquals(STATUS + 200f, placement.topPx, 0f)
    }

    @Test
    fun `when the screen is shorter than the bars, should keep a zero usable height`() {
        val tiny = bounds(BUTTONS, screenHeightPx = 50f)

        assertEquals(0f, tiny.usableHeightPx, 0f)
        assertEquals(0f, tiny.maxHeightPx, 0f)
    }

    private companion object {
        const val STATUS = 24f
        const val MARGIN = 16f
        const val CAP = 400f
        const val BUTTONS = 48f
        const val GESTURES = 16f
    }
}
