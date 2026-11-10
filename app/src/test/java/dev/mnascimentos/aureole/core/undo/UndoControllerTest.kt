package dev.mnascimentos.aureole.core.undo

import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class UndoControllerTest {

    private val controller = UndoController()

    @Test
    fun `show publishes a state with the message and the default 4 s duration`() {
        controller.show(TextResource.fromString("A")) {}

        val state = controller.state.value
        assertNotNull(state)
        assertEquals(TextResource.fromString("A"), state?.message)
        assertEquals(4000L, state?.durationMs)
    }

    @Test
    fun `show replaces the previous offer and drops its undo`() {
        var firstUndone = false
        var secondUndone = false
        controller.show(TextResource.fromString("A")) { firstUndone = true }
        controller.show(TextResource.fromString("B")) { secondUndone = true }

        assertEquals(TextResource.fromString("B"), controller.state.value?.message)
        controller.undo()

        assertEquals(false, firstUndone)
        assertEquals(true, secondUndone)
    }

    @Test
    fun `show with a new offer gets a different id`() {
        controller.show(TextResource.fromString("A")) {}
        val firstId = controller.state.value?.id
        controller.show(TextResource.fromString("A")) {}

        assertNotEquals(firstId, controller.state.value?.id)
    }

    @Test
    fun `undo runs the pending action once and clears the state`() {
        var count = 0
        controller.show(TextResource.fromString("A")) { count++ }

        controller.undo()
        controller.undo()

        assertEquals(1, count)
        assertNull(controller.state.value)
    }

    @Test
    fun `undo without a pending offer does nothing`() {
        controller.undo()

        assertNull(controller.state.value)
    }

    @Test
    fun `expire with the current id clears the offer and its undo`() {
        var count = 0
        controller.show(TextResource.fromString("A")) { count++ }
        val id = controller.state.value!!.id

        controller.expire(id)
        controller.undo()

        assertNull(controller.state.value)
        assertEquals(0, count)
    }

    @Test
    fun `expire with a stale id keeps the current offer`() {
        controller.show(TextResource.fromString("A")) {}
        val staleId = controller.state.value!!.id
        controller.show(TextResource.fromString("B")) {}

        controller.expire(staleId)

        assertEquals(TextResource.fromString("B"), controller.state.value?.message)
    }
}
