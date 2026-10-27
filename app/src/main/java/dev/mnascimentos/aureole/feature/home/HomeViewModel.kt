package dev.mnascimentos.aureole.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.feature.home.components.appsdrawer.extensions.updateAppsState
import dev.mnascimentos.aureole.feature.home.components.containers.extensions.loadContainers
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.OnboardingReducer
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.toProgress
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.loadWidgetSettings
import dev.mnascimentos.aureole.feature.home.grid.extensions.loadGridItems
import dev.mnascimentos.aureole.feature.home.model.HomeRepositories
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    repositories: HomeRepositories,
) : ViewModel() {
    internal val appRepository: AppRepository = repositories.appRepository
    internal val widgetRepository: WidgetRepository = repositories.widgetRepository
    internal val settingsRepository: SettingsRepository = repositories.settingsRepository
    internal val folderRepository: FolderRepository = repositories.folderRepository
    internal val favoriteContainerRepository: FavoriteContainerRepository = repositories.favoriteContainerRepository
    internal val containerRepository: ContainerRepository = repositories.containerRepository
    internal val gridRepository: GridRepository = repositories.gridRepository

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    internal fun updateUiState(transform: (MainUiState) -> MainUiState) {
        val before = _uiState.value.onboarding.toProgress()
        _uiState.update { current -> OnboardingReducer.reduce(previous = current, next = transform(current)) }
        val after = _uiState.value.onboarding.toProgress()
        // Persisted outside update {}, whose lambda may run more than once.
        if (after != before) settingsRepository.onboarding.write(after)
    }

    val pendingWidgetId: Int
        get() = _uiState.value.pendingWidgetId

    internal var activeVersionCode: Int = 0
    internal var searchJob: Job? = null

    var isUpdateInProgressOrDismissed: Boolean = false
        internal set

    init {
        loadSettings()
        observeAppsFlow()
        loadGridItems()
        loadContainers()
        loadWidgetSettings()
        syncApps()
    }

    private fun observeAppsFlow() {
        viewModelScope.launch {
            appRepository.appsFlow.collect { appsList ->
                updateAppsState(appsList)
            }
        }
    }

    fun loadApps() {
        syncApps()
    }

    fun syncApps() {
        viewModelScope.launch {
            appRepository.syncApps()
        }
    }

    fun launchApp(packageName: String, activityName: String) {
        appRepository.launchApp(packageName, activityName)
    }

    fun loadSettings() {
        viewModelScope.launch {
            val containerFavsMap = favoriteContainerRepository.getAllContainerFavorites()
            val savedFolders = folderRepository.getFolders()
            _uiState.update {
                it.copy(
                    isLeftHandedMode = settingsRepository.isLeftHandedMode,
                    isContainerEnabled = settingsRepository.isContainerEnabled,
                    isContainerBackgroundEnabled = settingsRepository.isContainerBackgroundEnabled,
                    isContainerExpandCell = settingsRepository.isContainerExpandCell,
                    isClockBackgroundEnabled = settingsRepository.isClockBackgroundEnabled,
                    clockStyle = settingsRepository.clockStyle,
                    clockCustomGreeting = settingsRepository.clockCustomGreeting,
                    clockAlignment = settingsRepository.clockAlignment,
                    clockFontFamily = settingsRepository.clockFontFamily,
                    clockTimeFormat = settingsRepository.clockTimeFormat,
                    clockDateFormat = settingsRepository.clockDateFormat,
                    clockTextColor = settingsRepository.clockTextColor,
                    clockBackgroundColor = settingsRepository.clockBackgroundColor,
                    showContainerAddFolderButton = settingsRepository.showContainerAddFolderButton,
                    containerPosition = settingsRepository.containerPosition,
                    showFolderLabels = settingsRepository.showFolderLabels,
                    homeButtonOpensAllApps = settingsRepository.homeButtonOpensAllApps,
                    showAllAppsOnHome = settingsRepository.showAllAppsOnHome,
                    isWidgetRowEnabled = settingsRepository.isWidgetRowEnabled,
                    showWidgetDots = settingsRepository.showWidgetDots,
                    favoriteAppPackages = settingsRepository.favoriteAppPackages,
                    containerFavorites = containerFavsMap,
                    folders = savedFolders,
                    isCustomWallpaperSet = settingsRepository.isCustomWallpaperSet,
                    customWallpaperPath = settingsRepository.customWallpaperPath,
                    wallpaperScaleType = settingsRepository.wallpaperScaleType,
                    isDynamicWallpaperEnabled = settingsRepository.isDynamicWallpaperEnabled,
                    manualSeedColor = settingsRepository.manualSeedColor,
                    selectedThemeName = settingsRepository.selectedThemeName,
                    selectedFontName = settingsRepository.selectedFontName,
                    isHazeEnabled = settingsRepository.isHazeEnabled,
                    isHazeSupported = settingsRepository.isHazeSupported,
                    hazeOpacity = settingsRepository.hazeOpacity,
                    isInAppUpdateEnabled = settingsRepository.isInAppUpdateEnabled,
                    isThemedAppIconsEnabled = settingsRepository.isThemedAppIconsEnabled,
                    isAlphabetScrubberDisabled = settingsRepository.isAlphabetScrubberDisabled,
                    showSettingsButtonInAllApps = settingsRepository.showSettingsButtonInAllApps,
                    settingsButtonPosition = settingsRepository.settingsButtonPosition,
                    showSearchBarInAllApps = settingsRepository.showSearchBarInAllApps,
                    searchIconPosition = settingsRepository.searchIconPosition,
                    headerOffsetPercent = settingsRepository.headerOffsetPercent,
                    cornerRadiusDp = settingsRepository.cornerRadiusDp
                )
            }
        }
    }

    fun setShowUpdateAvailableDialog(visible: Boolean) {
        _uiState.update { it.copy(showUpdateAvailableDialog = visible) }
    }

    fun setShowUpdateDownloadedDialog(visible: Boolean) {
        _uiState.update { it.copy(showUpdateDownloadedDialog = visible) }
    }
}
