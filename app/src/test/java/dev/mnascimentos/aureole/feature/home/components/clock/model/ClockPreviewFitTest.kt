package dev.mnascimentos.aureole.feature.home.components.clock.model

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.HEADER_CLOCK_SIZE_KEY
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.clockSizeOf
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.editingClockSize
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.withClockSize
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ClockPreviewFitTest {

    @Test
    fun `when the clock fits above the sheet, should draw it at its real size`() {
        assertEquals(1f, clockPreviewScale(300f, 120f, 360f, 400f))
    }

    @Test
    fun `when the clock is wider or taller than the room, should shrink it just enough`() {
        assertEquals(0.5f, clockPreviewScale(400f, 100f, 200f, 400f))
        assertEquals(0.75f, clockPreviewScale(200f, 200f, 360f, 150f))
    }

    @Test
    fun `when the room has no bound, should not limit the clock`() {
        assertEquals(1f, clockPreviewScale(300f, 120f, 360f, Float.POSITIVE_INFINITY))
        assertEquals(0.5f, clockPreviewScale(400f, 120f, 200f, Float.POSITIVE_INFINITY))
    }

    @Test
    fun `when the measure is empty, should keep the real size`() {
        assertEquals(1f, clockPreviewScale(0f, 0f, 200f, 200f))
    }

    @Test
    fun `the editor reads the size measured for the clock it edits`() {
        val gridSize = DpSize(160.dp, 90.dp)
        val headerSize = DpSize(330.dp, 120.dp)
        val state = MainUiState()
            .withClockSize("clock-1", gridSize)
            .withClockSize(null, headerSize)

        assertEquals(gridSize, state.copy(editingClockId = "clock-1").editingClockSize)
        assertEquals(headerSize, state.editingClockSize)
        assertEquals(headerSize, state.clockSizes[HEADER_CLOCK_SIZE_KEY])
        assertNull(state.clockSizeOf("clock-2"))
    }

    @Test
    fun `a new measure replaces the old one`() {
        val state = MainUiState()
            .withClockSize("clock-1", DpSize(160.dp, 90.dp))
            .withClockSize("clock-1", DpSize(160.dp, 60.dp))

        assertEquals(DpSize(160.dp, 60.dp), state.clockSizeOf("clock-1"))
        assertEquals(1, state.clockSizes.size)
    }
}
