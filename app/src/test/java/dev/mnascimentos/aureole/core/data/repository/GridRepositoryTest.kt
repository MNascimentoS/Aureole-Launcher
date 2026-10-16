package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class GridRepositoryTest {

    private lateinit var context: Context
    private lateinit var repository: GridRepository

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        repository = GridRepository(context)
    }

    @Test
    fun `when json is null or blank, should return default grid items`() {
        repository.resetToDefault()
        val items = repository.getGridItems(isLandscape = false)
        val defaultItems = GridDefaults.getDefaultGridItems(isLandscape = false)
        assertEquals(defaultItems.size, items.size)
    }

    @Test
    fun `when json is malformed or invalid, should gracefully fallback to default grid items`() {
        val prefs = context.getSharedPreferences("aureole_grid_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("grid_items_portrait_json", "invalid_json_content").commit()

        val items = repository.getGridItems(isLandscape = false)
        val defaultItems = GridDefaults.getDefaultGridItems(isLandscape = false)
        assertEquals(defaultItems.size, items.size)
    }

    @Test
    fun `when json contains null elements or missing fields, should sanitize and handle gracefully`() {
        val prefs = context.getSharedPreferences("aureole_grid_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString(
            "grid_items_portrait_json",
            "[null, {\"id\":\"custom\",\"col\":0,\"row\":0,\"colSpan\":1,\"rowSpan\":1}]"
        ).commit()

        val items = repository.getGridItems(isLandscape = false)
        assertEquals(1, items.size)
        assertEquals("custom", items[0].id)
    }
}
