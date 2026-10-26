package dev.mnascimentos.aureole.feature.home.model

import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import org.junit.Assert.assertNotNull
import org.junit.Test

class HomeModelDefaultsTest {

    @Test
    fun testModelDefaults() {
        val actions = HomeScreenActions(
            onWidgetRowHeightChanged = {},
            onAppClick = {},
            onExpandNotificationShade = {},
            onFolderIntent = {},
            onSetAddAppToFolderDialogVisible = {},
            onSetRenameFolderDialogVisible = {},
            onSearchQueryChanged = {},
            onSettingsClick = {},
            onAllAppsDrawerClose = {},
            onAllAppsDrawerOpen = {}
        )
        assertNotNull(actions)

        val mainUiState = MainUiState()
        assertNotNull(mainUiState)

        val settingsActions = SettingsScreenActions()
        assertNotNull(settingsActions)

        val settingsUiState = SettingsUiState()
        assertNotNull(settingsUiState)
    }
}
