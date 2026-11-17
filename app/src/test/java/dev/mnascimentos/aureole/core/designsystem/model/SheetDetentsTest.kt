package dev.mnascimentos.aureole.core.designsystem.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

private const val REST = 600f
private const val EXPANDED = 900f
private const val STRONG_UP = -1200f
private const val STRONG_DOWN = 1200f
private const val SLOW = 100f

class SheetDetentsTest {

    @Test
    fun `when released slowly near rest, should settle at rest`() {
        assertEquals(
            SheetDetentTarget.Rest,
            resolveDetent(height = 640f, rest = REST, expanded = EXPANDED, velocity = SLOW)
        )
    }

    @Test
    fun `when released slowly near expanded, should settle expanded`() {
        assertEquals(
            SheetDetentTarget.Expanded,
            resolveDetent(height = 820f, rest = REST, expanded = EXPANDED, velocity = -SLOW)
        )
    }

    @Test
    fun `when released exactly halfway, should prefer rest`() {
        assertEquals(
            SheetDetentTarget.Rest,
            resolveDetent(height = 750f, rest = REST, expanded = EXPANDED, velocity = 0f)
        )
    }

    @Test
    fun `when released slightly below rest, should settle at rest`() {
        assertEquals(
            SheetDetentTarget.Rest,
            resolveDetent(height = 450f, rest = REST, expanded = EXPANDED, velocity = 0f)
        )
    }

    @Test
    fun `when released below seventy percent of rest, should dismiss`() {
        assertEquals(
            SheetDetentTarget.Dismiss,
            resolveDetent(height = 419f, rest = REST, expanded = EXPANDED, velocity = 0f)
        )
    }

    @Test
    fun `when released at seventy percent of rest, should still settle at rest`() {
        assertEquals(
            SheetDetentTarget.Rest,
            resolveDetent(height = 420f, rest = REST, expanded = EXPANDED, velocity = 0f)
        )
    }

    @Test
    fun `when flung up from rest, should expand`() {
        assertEquals(
            SheetDetentTarget.Expanded,
            resolveDetent(height = 610f, rest = REST, expanded = EXPANDED, velocity = STRONG_UP)
        )
    }

    @Test
    fun `when flung up from low height, should expand instead of dismiss`() {
        assertEquals(
            SheetDetentTarget.Expanded,
            resolveDetent(height = 200f, rest = REST, expanded = EXPANDED, velocity = STRONG_UP)
        )
    }

    @Test
    fun `when flung down from expanded, should collapse to rest`() {
        assertEquals(
            SheetDetentTarget.Rest,
            resolveDetent(height = 880f, rest = REST, expanded = EXPANDED, velocity = STRONG_DOWN)
        )
    }

    @Test
    fun `when flung down already at rest, should dismiss`() {
        assertEquals(
            SheetDetentTarget.Dismiss,
            resolveDetent(height = 590f, rest = REST, expanded = EXPANDED, velocity = STRONG_DOWN)
        )
    }

    @Test
    fun `when flung down exactly at rest, should dismiss`() {
        assertEquals(
            SheetDetentTarget.Dismiss,
            resolveDetent(height = REST, rest = REST, expanded = EXPANDED, velocity = STRONG_DOWN)
        )
    }

    @Test
    fun `when moving up through rest, should cross detent`() {
        assertTrue(crossedDetent(previous = 590f, current = 610f, detents = listOf(REST, EXPANDED)))
    }

    @Test
    fun `when moving down through expanded, should cross detent`() {
        assertTrue(crossedDetent(previous = 910f, current = 890f, detents = listOf(REST, EXPANDED)))
    }

    @Test
    fun `when landing exactly on a detent, should cross it`() {
        assertTrue(crossedDetent(previous = 880f, current = EXPANDED, detents = listOf(REST, EXPANDED)))
    }

    @Test
    fun `when leaving the detent the sheet sits on, should not cross`() {
        assertFalse(crossedDetent(previous = REST, current = 620f, detents = listOf(REST, EXPANDED)))
        assertFalse(crossedDetent(previous = REST, current = 580f, detents = listOf(REST, EXPANDED)))
    }

    @Test
    fun `when moving between detents, should not cross`() {
        assertFalse(crossedDetent(previous = 650f, current = 700f, detents = listOf(REST, EXPANDED)))
    }

    @Test
    fun `when there are no detents, should not cross`() {
        assertFalse(crossedDetent(previous = 0f, current = 1000f, detents = emptyList()))
    }

    @Test
    fun `when anchoring preview, should sit gap above sheet top`() {
        assertEquals(1000 - 40 - 300, anchoredPreviewTop(sheetTop = 1000, gap = 40, previewHeight = 300))
    }

    @Test
    fun `when sheet is expanded over the preview, should keep the preview at the min top`() {
        assertEquals(0, anchoredPreviewTop(sheetTop = 100, gap = 40, previewHeight = 300))
        assertEquals(64, anchoredPreviewTop(sheetTop = 100, gap = 40, previewHeight = 300, minTop = 64))
    }

    @Test
    fun `when there is room above the sheet, should ignore the min top`() {
        assertEquals(660, anchoredPreviewTop(sheetTop = 1000, gap = 40, previewHeight = 300, minTop = 64))
    }

    @Test
    fun `when the content fits at rest, should not expand`() {
        assertNull(expandTarget(rest = REST, expanded = EXPANDED, contentFull = REST))
    }

    @Test
    fun `when the content is within the slack of rest, should still count as fitting`() {
        assertNull(expandTarget(rest = REST, expanded = EXPANDED, contentFull = REST + 6f, slack = 8f))
    }

    @Test
    fun `when a little content is below, should expand only to the content`() {
        assertEquals(700f, expandTarget(rest = REST, expanded = EXPANDED, contentFull = 700f, slack = 8f))
    }

    @Test
    fun `when much content is below, should expand to the full expanded height`() {
        assertEquals(EXPANDED, expandTarget(rest = REST, expanded = EXPANDED, contentFull = 3000f, slack = 8f))
    }

    @Test
    fun `when the content height is unknown, should keep the full expanded height`() {
        assertEquals(EXPANDED, expandTarget(rest = REST, expanded = EXPANDED, contentFull = null, slack = 8f))
    }

    @Test
    fun `when the sheet cannot expand, a fling up should settle at rest`() {
        assertEquals(
            SheetDetentTarget.Rest,
            resolveDetent(height = 650f, rest = REST, expanded = REST, velocity = STRONG_UP, canExpand = false)
        )
    }

    @Test
    fun `when the sheet cannot expand, a release up high should settle at rest`() {
        assertEquals(
            SheetDetentTarget.Rest,
            resolveDetent(height = 890f, rest = REST, expanded = EXPANDED, velocity = 0f, canExpand = false)
        )
    }

    @Test
    fun `when the sheet cannot expand, a release low should still dismiss`() {
        assertEquals(
            SheetDetentTarget.Dismiss,
            resolveDetent(height = 100f, rest = REST, expanded = REST, velocity = 0f, canExpand = false)
        )
    }

    @Test
    fun `when flung up, should count as a fling up only past the threshold`() {
        assertTrue(isFlingUp(STRONG_UP))
        assertFalse(isFlingUp(-SLOW))
    }
}
