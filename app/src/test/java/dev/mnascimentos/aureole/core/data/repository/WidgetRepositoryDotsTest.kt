package dev.mnascimentos.aureole.core.data.repository

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class WidgetRepositoryDotsTest {

    private lateinit var repository: WidgetRepository

    @Before
    fun setUp() {
        repository = WidgetRepository(RuntimeEnvironment.getApplication())
    }

    @Test
    fun `when nothing is saved, should be on`() {
        assertTrue(repository.getWidgetDotsState("stack_new"))
    }

    @Test
    fun `when the landscape key was never saved, should inherit the portrait value`() {
        repository.setWidgetDotsState("stack_a", false)

        assertFalse(repository.getWidgetDotsState("stack_a_landscape", fallbackKey = "stack_a"))
    }

    @Test
    fun `when landscape is toggled, should not change portrait and the other way around`() {
        repository.setWidgetDotsState("stack_b", true)
        repository.setWidgetDotsState("stack_b_landscape", false)

        assertTrue(repository.getWidgetDotsState("stack_b"))
        assertFalse(repository.getWidgetDotsState("stack_b_landscape", fallbackKey = "stack_b"))

        repository.setWidgetDotsState("stack_b", false)
        repository.setWidgetDotsState("stack_b_landscape", true)

        assertFalse(repository.getWidgetDotsState("stack_b"))
        assertTrue(repository.getWidgetDotsState("stack_b_landscape", fallbackKey = "stack_b"))
    }

    @Test
    fun `when top widgets is saved in landscape, should keep portrait untouched`() {
        repository.setWidgetDotsState("top_widgets_landscape", false)

        assertTrue(repository.getWidgetDotsState("top_widgets"))
    }
}
