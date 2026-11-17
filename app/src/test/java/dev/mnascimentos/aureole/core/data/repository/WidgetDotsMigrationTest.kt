package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WidgetDotsMigrationTest {

    private lateinit var context: Context
    private lateinit var settings: SettingsRepository
    private lateinit var widgets: WidgetRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences("aureole_settings_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        context.getSharedPreferences("aureole_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        settings = SettingsRepository(context)
        widgets = WidgetRepository(context)
    }

    private fun saveLegacyGlobal(value: Boolean) {
        context.getSharedPreferences("aureole_settings_prefs", Context.MODE_PRIVATE)
            .edit().putBoolean("show_widget_dots", value).commit()
    }

    @Test
    fun `when the old global switch was off, answers true once and erases the key`() {
        saveLegacyGlobal(false)

        assertTrue(settings.consumeLegacyWidgetDotsOff())
        assertFalse(settings.consumeLegacyWidgetDotsOff())
    }

    @Test
    fun `when the old global switch was on or never saved, nothing to migrate`() {
        assertFalse(settings.consumeLegacyWidgetDotsOff())

        saveLegacyGlobal(true)
        assertFalse(settings.consumeLegacyWidgetDotsOff())
        assertFalse(
            context.getSharedPreferences("aureole_settings_prefs", Context.MODE_PRIVATE).contains("show_widget_dots")
        )
    }

    @Test
    fun `turnDotsOff saves portrait and landscape off for every group`() {
        widgets.turnDotsOff(listOf("top_widgets", "stack_a"))

        listOf("top_widgets", "stack_a").forEach { id ->
            assertFalse(widgets.getWidgetDotsState(id))
            assertFalse(widgets.getWidgetDotsState("${id}_landscape"))
        }
        assertTrue(widgets.getWidgetDotsState("stack_other"))
    }
}
