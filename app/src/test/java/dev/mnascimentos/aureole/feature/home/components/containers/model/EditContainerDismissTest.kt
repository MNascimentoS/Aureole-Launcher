package dev.mnascimentos.aureole.feature.home.components.containers.model

import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EditContainerDismissTest {

    private val log = mutableListOf<String>()
    private val closer = EditContainerCloser(save = { log += "save" }, close = { log += "close" })

    @Test
    fun `when dismissed on any screen, should save and close once`() {
        EditContainerScreenMode.entries.forEach { _ ->
            val modeCloser = EditContainerCloser(save = { log += "save" }, close = { log += "close" })
            modeCloser.closeOnce()
        }

        assertEquals(List(EditContainerScreenMode.entries.size) { listOf("save", "close") }.flatten(), log)
    }

    @Test
    fun `when closed twice, should save and close only once`() {
        closer.closeOnce()
        closer.closeOnce()

        assertEquals(listOf("save", "close"), log)
        assertTrue(closer.isClosing)
    }

    @Test
    fun `when the save fails, should still close and rethrow`() {
        val failing = EditContainerCloser(save = { error("boom") }, close = { log += "close" })

        val thrown = runCatching { failing.closeOnce() }.exceptionOrNull()

        assertTrue(thrown is IllegalStateException)
        assertEquals(listOf("close"), log)
        assertTrue(failing.isClosing)
    }

    @Test
    fun `when back from a sub screen, should go to the main screen without closing`() {
        listOf(
            EditContainerScreenMode.APPEARANCE,
            EditContainerScreenMode.BEHAVIOR,
            EditContainerScreenMode.MANAGE_ITEMS
        ).forEach { mode ->
            var target: EditContainerScreenMode? = null

            mode.handleBack(closer) { target = it }

            assertEquals(EditContainerScreenMode.MAIN, target)
        }
        assertTrue(log.isEmpty())
        assertFalse(closer.isClosing)
    }

    @Test
    fun `when back from the main screen, should save and close`() {
        var target: EditContainerScreenMode? = null

        EditContainerScreenMode.MAIN.handleBack(closer) { target = it }

        assertNull(target)
        assertEquals(listOf("save", "close"), log)
    }

    @Test
    fun `when opened, should show the editor for the panel and bump the open count`() {
        val opened = MainUiState().withEditContainerOpened("p1")

        assertTrue(opened.isEditContainerDialogVisible)
        assertEquals("p1", opened.editingContainerId)
        assertEquals(1, opened.editContainerOpenCount)
    }

    @Test
    fun `when dismissed from a sub screen and opened again, should open with a new open count`() {
        val dismissed = MainUiState().withEditContainerOpened("p1").withEditContainerClosed("p1")
        assertFalse(dismissed.isEditContainerDialogVisible)
        assertNull(dismissed.editingContainerId)

        val reopened = dismissed.withEditContainerOpened("p1")

        assertTrue(reopened.isEditContainerDialogVisible)
        assertEquals("p1", reopened.editingContainerId)
        assertEquals(2, reopened.editContainerOpenCount)
    }

    @Test
    fun `when opened twice without closing, should still change the open count`() {
        val state = MainUiState().withEditContainerOpened("p1").withEditContainerOpened("p1")

        assertEquals(2, state.editContainerOpenCount)
    }

    @Test
    fun `when a late close targets another panel, should keep the reopened editor`() {
        val reopened = MainUiState().withEditContainerOpened("p2")

        val after = reopened.withEditContainerClosed("p1")

        assertEquals(reopened, after)
    }

    @Test
    fun `when closed without a panel id, should close whatever is open`() {
        val after = MainUiState().withEditContainerOpened("p2").withEditContainerClosed(null)

        assertFalse(after.isEditContainerDialogVisible)
        assertNull(after.editingContainerId)
    }
}
