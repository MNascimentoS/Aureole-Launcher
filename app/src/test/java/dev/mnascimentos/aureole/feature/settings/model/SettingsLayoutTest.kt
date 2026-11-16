package dev.mnascimentos.aureole.feature.settings.model

import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsLayoutTest {

    @Test
    fun `portrait area uses a single column`() {
        assertEquals(SettingsLayout.SINGLE_COLUMN, settingsLayoutFor(widthDp = 411f, heightDp = 891f))
    }

    @Test
    fun `landscape area uses two panes`() {
        assertEquals(SettingsLayout.TWO_PANES, settingsLayoutFor(widthDp = 891f, heightDp = 411f))
    }

    @Test
    fun `square area keeps the single column`() {
        assertEquals(SettingsLayout.SINGLE_COLUMN, settingsLayoutFor(widthDp = 500f, heightDp = 500f))
    }
}
