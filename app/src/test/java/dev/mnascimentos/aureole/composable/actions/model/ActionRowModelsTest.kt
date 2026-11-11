package dev.mnascimentos.aureole.composable.actions.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ActionRowModelsTest {
    @Test
    fun `right handed puts the secondary button on the left`() {
        assertEquals(listOf(ActionSlot.SECONDARY, ActionSlot.PRIMARY), actionRowOrder(isLeftHanded = false))
    }

    @Test
    fun `left handed mirrors the order so the primary sits under the thumb`() {
        assertEquals(listOf(ActionSlot.PRIMARY, ActionSlot.SECONDARY), actionRowOrder(isLeftHanded = true))
    }

    @Test
    fun `primary button is wider than the secondary one`() {
        assertEquals(1.4f, ACTION_PRIMARY_WEIGHT, 0f)
        assertEquals(1f, ACTION_SECONDARY_WEIGHT, 0f)
    }
}
