package dev.mnascimentos.aureole.feature.home.extensions

import android.content.Context
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.model.HomeRepositories
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class HomeViewModelUiExtTest {

    private lateinit var context: Context
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        settingsRepository = SettingsRepository(context)

        viewModel = HomeViewModel(
            HomeRepositories(
                appRepository = AppRepository(context),
                widgetRepository = WidgetRepository(context),
                settingsRepository = settingsRepository,
                folderRepository = FolderRepository(context),
                favoriteContainerRepository = FavoriteContainerRepository(context, settingsRepository),
                containerRepository = ContainerRepository(context),
                gridRepository = GridRepository(context)
            )
        )
    }

    @Test
    fun `when toggleShowAllAppsOnHome is called, should toggle state and save to repo`() {
        val initial = viewModel.uiState.value.showAllAppsOnHome

        viewModel.toggleShowAllAppsOnHome()

        assertEquals(!initial, viewModel.uiState.value.showAllAppsOnHome)
        assertEquals(!initial, settingsRepository.showAllAppsOnHome)
    }

    @Test
    fun `when toggleLeftHandedMode is called, should toggle state and save to repo`() {
        val initial = viewModel.uiState.value.isLeftHandedMode

        viewModel.toggleLeftHandedMode()

        assertEquals(!initial, viewModel.uiState.value.isLeftHandedMode)
        assertEquals(!initial, settingsRepository.isLeftHandedMode)
    }

    @Test
    fun `when toggleContainer is called, should toggle state and save to repo`() {
        val initial = viewModel.uiState.value.isContainerEnabled

        viewModel.toggleContainer()

        assertEquals(!initial, viewModel.uiState.value.isContainerEnabled)
        assertEquals(!initial, settingsRepository.isContainerEnabled)
    }
}
