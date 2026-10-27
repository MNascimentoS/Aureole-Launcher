package dev.mnascimentos.aureole.feature.home.components.quickbutton

import android.content.Context
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.QuickButtonAppearance
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.QuickButtonShortcuts
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.quickbutton.extensions.onCloseQuickButtonAppearance
import dev.mnascimentos.aureole.feature.home.components.quickbutton.extensions.onCloseQuickButtonSettings
import dev.mnascimentos.aureole.feature.home.components.quickbutton.extensions.onCloseShortcutSelection
import dev.mnascimentos.aureole.feature.home.components.quickbutton.extensions.onOpenQuickButtonAppearance
import dev.mnascimentos.aureole.feature.home.components.quickbutton.extensions.onOpenQuickButtonSettings
import dev.mnascimentos.aureole.feature.home.components.quickbutton.extensions.onOpenShortcutSelection
import dev.mnascimentos.aureole.feature.home.components.quickbutton.extensions.onUpdateQuickButtonAppearance
import dev.mnascimentos.aureole.feature.home.components.quickbutton.extensions.onUpdateQuickButtonShortcuts
import dev.mnascimentos.aureole.feature.home.model.HomeRepositories
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class HomeViewModelQuickButtonExtTest {

    private lateinit var context: Context
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        val settingsRepo = SettingsRepository(context)
        viewModel = HomeViewModel(
            HomeRepositories(
                appRepository = AppRepository(context),
                widgetRepository = WidgetRepository(context),
                settingsRepository = settingsRepo,
                gridRepository = GridRepository(context),
                folderRepository = FolderRepository(context),
                containerRepository = ContainerRepository(context),
                favoriteContainerRepository = FavoriteContainerRepository(context, settingsRepo)
            )
        )
    }

    @Test
    fun `when opening and closing quick button settings, should update ui state flags accordingly`() {
        // Given
        val itemModel = QuickButtonModel(id = "qb_item_1")
        val parentItem = LauncherItemState(
            id = "parent",
            children = listOf(
                LauncherItemState(id = "qb_item_1", quickButtonModel = itemModel)
            )
        )
        viewModel.updateUiState { it.copy(gridItems = listOf(parentItem)) }

        // When open
        viewModel.onOpenQuickButtonSettings("qb_item_1")

        // Then
        var state = viewModel.uiState.value
        assertTrue(state.showQuickButtonSettingsBottomSheet)
        assertEquals("qb_item_1", state.activeQuickButtonId)
        assertEquals(itemModel, state.activeQuickButtonModel)

        // When close
        viewModel.onCloseQuickButtonSettings()

        // Then
        state = viewModel.uiState.value
        assertFalse(state.showQuickButtonSettingsBottomSheet)
        assertNull(state.activeQuickButtonId)
        assertNull(state.activeQuickButtonModel)
    }

    @Test
    fun `when opening settings for non existent item id, should do nothing`() {
        // Given
        viewModel.updateUiState { it.copy(gridItems = emptyList()) }

        // When
        viewModel.onOpenQuickButtonSettings("non_existent")

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.showQuickButtonSettingsBottomSheet)
    }

    @Test
    fun `when toggling appearance and shortcut selection sheets, should update state flags`() {
        // When opening appearance
        viewModel.onOpenQuickButtonAppearance()
        var state = viewModel.uiState.value
        assertTrue(state.showQuickButtonAppearanceBottomSheet)
        assertFalse(state.showQuickButtonSettingsBottomSheet)

        // When closing appearance
        viewModel.onCloseQuickButtonAppearance()
        state = viewModel.uiState.value
        assertFalse(state.showQuickButtonAppearanceBottomSheet)
        assertTrue(state.showQuickButtonSettingsBottomSheet)

        // When opening shortcut selection
        viewModel.onOpenShortcutSelection("click")
        state = viewModel.uiState.value
        assertTrue(state.showShortcutSelectionBottomSheet)
        assertEquals("click", state.editingShortcutType)
        assertFalse(state.showQuickButtonSettingsBottomSheet)

        // When closing shortcut selection
        viewModel.onCloseShortcutSelection()
        state = viewModel.uiState.value
        assertFalse(state.showShortcutSelectionBottomSheet)
        assertNull(state.editingShortcutType)
        assertTrue(state.showQuickButtonSettingsBottomSheet)
    }

    @Test
    fun `when updating appearance and shortcuts for active model, should save and update grid items`() {
        // Given
        val activeModel = QuickButtonModel(id = "qb_active")
        val gridItem = LauncherItemState(id = "qb_active", quickButtonModel = activeModel)
        viewModel.updateUiState {
            it.copy(
                activeQuickButtonId = "qb_active",
                activeQuickButtonModel = activeModel,
                gridItems = listOf(gridItem),
                portraitGridItems = listOf(gridItem),
                landscapeGridItems = listOf(gridItem)
            )
        }

        // When updating appearance
        val newAppearance = QuickButtonAppearance(strokeEnabled = true, strokeColor = 0xFF123456.toInt())
        viewModel.onUpdateQuickButtonAppearance(newAppearance)

        // Then
        var state = viewModel.uiState.value
        assertEquals(newAppearance, state.activeQuickButtonModel?.appearance)
        assertEquals(newAppearance, state.gridItems.first().quickButtonModel?.appearance)

        // When updating shortcuts
        val newShortcuts = QuickButtonShortcuts(click = ShortcutAction.Settings)
        viewModel.onUpdateQuickButtonShortcuts(newShortcuts)

        // Then
        state = viewModel.uiState.value
        assertEquals(newShortcuts, state.activeQuickButtonModel?.shortcuts)
        assertEquals(newShortcuts, state.gridItems.first().quickButtonModel?.shortcuts)
    }

    @Test
    fun `when opening settings for quick button inside container, should find model and open sheet`() {
        // Given
        val qbModel = QuickButtonModel(id = "qb_in_container")
        val containerItem = ContainerItemEntity(
            id = "c_item_1",
            panelId = "panel_1",
            itemType = ContainerItemType.QUICK_BUTTON,
            quickButtonId = "qb_in_container",
            orderIndex = 0
        ).also { it.quickButtonModel = qbModel }
        val container = ContainerModel(
            id = "panel_1",
            items = listOf(containerItem),
            quickButtons = listOf(qbModel)
        )
        viewModel.updateUiState { it.copy(containers = mapOf("panel_1" to container)) }

        // When
        viewModel.onOpenQuickButtonSettings("qb_in_container")

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.showQuickButtonSettingsBottomSheet)
        assertEquals("qb_in_container", state.activeQuickButtonId)
        assertEquals(qbModel, state.activeQuickButtonModel)
    }

    @Test
    fun `when updating shortcuts for quick button inside container, should update container`() {
        // Given
        val qbModel = QuickButtonModel(id = "qb_in_container")
        val containerItem = ContainerItemEntity(
            id = "c_item_1",
            panelId = "panel_1",
            itemType = ContainerItemType.QUICK_BUTTON,
            quickButtonId = "qb_in_container",
            orderIndex = 0
        ).also { it.quickButtonModel = qbModel }
        val container = ContainerModel(
            id = "panel_1",
            items = listOf(containerItem),
            quickButtons = listOf(qbModel)
        )
        viewModel.updateUiState {
            it.copy(
                activeQuickButtonId = "qb_in_container",
                activeQuickButtonModel = qbModel,
                containers = mapOf("panel_1" to container)
            )
        }

        // When updating shortcuts
        val newShortcuts = QuickButtonShortcuts(click = ShortcutAction.Settings)
        viewModel.onUpdateQuickButtonShortcuts(newShortcuts)

        // Then
        val state = viewModel.uiState.value
        val updatedContainer = state.containers["panel_1"]
        val updatedQb = updatedContainer?.quickButtons?.first()
        assertEquals(newShortcuts, updatedQb?.shortcuts)
    }
}
