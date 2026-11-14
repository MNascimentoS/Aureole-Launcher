package dev.mnascimentos.aureole.core.designsystem.theme

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class AureoleDimensTokensTest {

    private val dimens = AureoleDimens()

    @Test
    fun `control and drag handle tokens keep the sizes the components had`() {
        assertEquals(44.dp, dimens.controlHeight)
        assertEquals(44.dp, dimens.dragHandleWidth)
        assertEquals(38.dp, dimens.dragHandleHeight)
    }

    @Test
    fun `drawing and layout tokens keep the sizes the components had`() {
        assertEquals(14.dp, dimens.clockLineHeightLarge)
        assertEquals(6.dp, dimens.dashLength)
        assertEquals(8.dp, dimens.floatingElevation)
        assertEquals(300.dp, dimens.previewListMaxHeight)
        assertEquals(92.dp, dimens.shortcutTileHeight)
    }

    @Test
    fun `the corner radius does not change the new tokens`() {
        val straight = AureoleDimens.forCornerRadius(0)

        assertEquals(dimens.controlHeight, straight.controlHeight)
        assertEquals(dimens.dashLength, straight.dashLength)
        assertEquals(dimens.shortcutTileHeight, straight.shortcutTileHeight)
    }
}
