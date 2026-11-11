package dev.mnascimentos.aureole.feature.settings.model

import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsPositionModelsTest {

    @Test
    fun `button and search offer left and right`() {
        val expected = listOf("Left", "Right")
        assertEquals(expected, PositionKind.SETTINGS_BUTTON.options().map { it.value })
        assertEquals(expected, PositionKind.SEARCH_ICON.options().map { it.value })
    }

    @Test
    fun `container offers the five alignments in order`() {
        assertEquals(
            listOf("Top", "Center", "Bottom", "Space Evenly", "Space Between"),
            PositionKind.CONTAINER.options().map { it.value }
        )
    }

    @Test
    fun `positionOf reads the field of each setting`() {
        val state = SettingsUiState(
            settingsButtonPosition = "Left",
            searchIconPosition = "Right",
            containerPosition = "Top"
        )
        assertEquals("Left", state.positionOf(PositionKind.SETTINGS_BUTTON))
        assertEquals("Right", state.positionOf(PositionKind.SEARCH_ICON))
        assertEquals("Top", state.positionOf(PositionKind.CONTAINER))
    }

    @Test
    fun `toSettingValue builds the matching value`() {
        assertEquals(SettingValue.SettingsButtonPosition("Left"), PositionKind.SETTINGS_BUTTON.toSettingValue("Left"))
        assertEquals(SettingValue.SearchIconPosition("Right"), PositionKind.SEARCH_ICON.toSettingValue("Right"))
        assertEquals(SettingValue.ContainerPosition("Top"), PositionKind.CONTAINER.toSettingValue("Top"))
    }
}
