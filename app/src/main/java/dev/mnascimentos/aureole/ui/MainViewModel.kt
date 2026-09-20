package dev.mnascimentos.aureole.ui

import android.app.Application
import android.content.ComponentName
import android.os.Build
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.data.model.AppFolder
import dev.mnascimentos.aureole.data.model.AppInfo
import dev.mnascimentos.aureole.data.repository.AppRepository
import dev.mnascimentos.aureole.data.repository.FolderRepository
import dev.mnascimentos.aureole.data.repository.SettingsRepository
import dev.mnascimentos.aureole.data.repository.WidgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface FolderViewIntent {
    object OpenCreateFolderDialog : FolderViewIntent
    data class SubmitFolderName(val name: String) : FolderViewIntent
    data class OpenFolder(val folderId: String, val topYPx: Float = 0f) : FolderViewIntent
    object CloseFolder : FolderViewIntent
    data class LaunchApp(val packageName: String) : FolderViewIntent
    data class AddAppToFolder(val folderId: String) : FolderViewIntent
    data class SaveFolderApps(val folderId: String, val selectedPackageNames: List<String>) : FolderViewIntent
    data class RenameFolder(val folderId: String, val newName: String, val icon: String? = null) : FolderViewIntent
    data class DeleteFolder(val folderId: String) : FolderViewIntent
}

data class MainUiState(
    val apps: List<AppInfo> = emptyList(),
    val filteredApps: List<AppInfo> = emptyList(),
    val alphabet: List<Char> = emptyList(),
    val letterIndexMap: Map<Char, Int> = emptyMap(),
    val isLoading: Boolean = true,

    // Widgets
    val topWidgetIds: List<Int> = emptyList(),
    val widgetRowHeight: Dp = 160.dp,
    val isWidgetRowEnabled: Boolean = true,
    val showWidgetPicker: Boolean = false,
    val pendingWidgetId: Int = -1,

    // Settings
    val isLeftHandedMode: Boolean = false,
    val isSidePanelEnabled: Boolean = true,
    val sidePanelPosition: String = "Center",
    val showFolderLabels: Boolean = false,
    val homeButtonOpensAllApps: Boolean = true,
    val showAllAppsOnHome: Boolean = true,
    val isCustomWallpaperSet: Boolean = false,
    val customWallpaperPath: String? = null,
    val isDynamicWallpaperEnabled: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
    val manualSeedColor: Int = SettingsRepository.DEFAULT_SEED_COLOR,

    // Favorites & Folders
    val favoriteAppPackages: List<String> = emptyList(),
    val folders: List<AppFolder> = emptyList(),
    val favoriteApps: List<AppInfo> = emptyList(),

    // UI State for Dialogs/Drawer & Folders
    val openedFolderId: String? = null,
    val activeFolder: AppFolder? = null,
    val activeFolderTopYPx: Float = 0f,
    val isCreateFolderDialogVisible: Boolean = false,
    val isAddAppToFolderDialogVisible: Boolean = false,
    val isRenameFolderDialogVisible: Boolean = false,
    val searchQuery: String = "",
    val showSettingsDialog: Boolean = false,
    val showFavoritePickerDialog: Boolean = false,
    val isAllAppsDrawerOpen: Boolean = false,
)

@Suppress("TooManyFunctions")
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val appRepository = AppRepository(application)
    private val widgetRepository = WidgetRepository(application)
    private val settingsRepository = SettingsRepository(application)
    private val folderRepository = FolderRepository(application)

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    val pendingWidgetId: Int
        get() = _uiState.value.pendingWidgetId

    init {
        loadSettings()
        observeAppsFlow()
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

    fun launchApp(componentName: ComponentName) {
        appRepository.launchApp(componentName)
    }

    private fun loadWidgetSettings() {
        val savedIds = widgetRepository.getSavedWidgetIds()
        val savedHeight = widgetRepository.getSavedWidgetRowHeight()
        _uiState.update {
            it.copy(
                topWidgetIds = savedIds,
                widgetRowHeight = savedHeight.dp,
            )
        }
    }

    fun loadSettings() {
        viewModelScope.launch {
            val isLeftHanded = settingsRepository.isLeftHandedMode
            val isSidePanelEnabled = settingsRepository.isSidePanelEnabled
            val sidePanelPosition = settingsRepository.sidePanelPosition
            val showFolderLabels = settingsRepository.showFolderLabels
            val homeOpensAllApps = settingsRepository.homeButtonOpensAllApps
            val showAllAppsHome = settingsRepository.showAllAppsOnHome
            val isWidgetRowEnabled = settingsRepository.isWidgetRowEnabled
            val favoritePackages = settingsRepository.favoriteAppPackages
            val savedFolders = folderRepository.getFolders()
            val isCustomWallpaperSet = settingsRepository.isCustomWallpaperSet
            val customWallpaperPath = settingsRepository.customWallpaperPath
            val isDynamicWallpaperEnabled = settingsRepository.isDynamicWallpaperEnabled
            val manualSeedColor = settingsRepository.manualSeedColor

            _uiState.update {
                it.copy(
                    isLeftHandedMode = isLeftHanded,
                    isSidePanelEnabled = isSidePanelEnabled,
                    sidePanelPosition = sidePanelPosition,
                    showFolderLabels = showFolderLabels,
                    homeButtonOpensAllApps = homeOpensAllApps,
                    showAllAppsOnHome = showAllAppsHome,
                    isWidgetRowEnabled = isWidgetRowEnabled,
                    favoriteAppPackages = favoritePackages,
                    folders = savedFolders,
                    isCustomWallpaperSet = isCustomWallpaperSet,
                    customWallpaperPath = customWallpaperPath,
                    isDynamicWallpaperEnabled = isDynamicWallpaperEnabled,
                    manualSeedColor = manualSeedColor
                )
            }
        }
    }

    // --- MVI Folder ViewIntents Handler ---

    fun onFolderIntent(intent: FolderViewIntent) {
        viewModelScope.launch {
            handleFolderIntent(intent)
        }
    }

    private suspend fun handleFolderIntent(intent: FolderViewIntent) {
        when (intent) {
            is FolderViewIntent.OpenCreateFolderDialog -> {
                _uiState.update { it.copy(isCreateFolderDialogVisible = true) }
            }
            is FolderViewIntent.SubmitFolderName -> createNewFolder(intent.name)
            is FolderViewIntent.OpenFolder -> openFolder(intent)
            is FolderViewIntent.CloseFolder -> closeFolder()
            is FolderViewIntent.LaunchApp -> launchAppFromFolder(intent.packageName)
            is FolderViewIntent.AddAppToFolder -> {
                _uiState.update { it.copy(isAddAppToFolderDialogVisible = true) }
            }
            is FolderViewIntent.SaveFolderApps -> saveFolderApps(intent)
            is FolderViewIntent.RenameFolder -> renameFolder(intent)
            is FolderViewIntent.DeleteFolder -> deleteFolder(intent.folderId)
        }
    }

    private suspend fun createNewFolder(name: String) {
        val newFolder = AppFolder(name = name)
        folderRepository.addFolder(newFolder)
        refreshFoldersAndOpen(newFolder.id)
        _uiState.update { it.copy(isCreateFolderDialogVisible = false) }
    }

    private fun openFolder(intent: FolderViewIntent.OpenFolder) {
        val targetFolder = _uiState.value.folders.find { it.id == intent.folderId }
        _uiState.update {
            it.copy(
                openedFolderId = intent.folderId,
                activeFolder = targetFolder,
                activeFolderTopYPx = intent.topYPx
            )
        }
    }

    private fun closeFolder() {
        _uiState.update {
            it.copy(
                openedFolderId = null,
                activeFolder = null,
                isAddAppToFolderDialogVisible = false,
                isRenameFolderDialogVisible = false
            )
        }
    }

    private fun launchAppFromFolder(packageName: String) {
        val appInfo = _uiState.value.apps.find { it.packageName == packageName }
        appInfo?.let { launchApp(it.componentName) }
        closeFolder()
    }

    private suspend fun saveFolderApps(intent: FolderViewIntent.SaveFolderApps) {
        folderRepository.updateFolderApps(intent.folderId, intent.selectedPackageNames)
        refreshFoldersAndOpen(intent.folderId)
        _uiState.update { it.copy(isAddAppToFolderDialogVisible = false) }
    }

    private suspend fun renameFolder(intent: FolderViewIntent.RenameFolder) {
        folderRepository.updateFolderDetails(intent.folderId, intent.newName, intent.icon)
        refreshFoldersAndOpen(intent.folderId)
        _uiState.update { it.copy(isRenameFolderDialogVisible = false) }
    }

    private suspend fun deleteFolder(folderId: String) {
        folderRepository.deleteFolder(folderId)
        refreshFolders()
        closeFolder()
    }

    private suspend fun refreshFoldersAndOpen(folderId: String) {
        val updatedFolders = folderRepository.getFolders()
        val active = updatedFolders.find { it.id == folderId }
        _uiState.update {
            it.copy(
                folders = updatedFolders,
                openedFolderId = active?.id,
                activeFolder = active
            )
        }
    }

    private suspend fun refreshFolders() {
        val updatedFolders = folderRepository.getFolders()
        _uiState.update { it.copy(folders = updatedFolders) }
    }

    fun setRenameFolderDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(isRenameFolderDialogVisible = visible) }
    }

    fun setAddAppToFolderDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(isAddAppToFolderDialogVisible = visible) }
    }

    fun setWidgetRowHeight(height: Dp) {
        _uiState.update { it.copy(widgetRowHeight = height) }
        widgetRepository.saveWidgetRowHeight(height.value)
    }

    fun setShowWidgetPicker(show: Boolean) {
        _uiState.update { it.copy(showWidgetPicker = show) }
    }

    fun setPendingWidgetId(id: Int) {
        _uiState.update { it.copy(pendingWidgetId = id) }
    }

    fun addWidgetId(widgetId: Int) {
        val currentList = _uiState.value.topWidgetIds
        if (!currentList.contains(widgetId)) {
            val newList = currentList + widgetId
            _uiState.update { it.copy(topWidgetIds = newList) }
            widgetRepository.saveWidgetIds(newList)
        }
    }

    fun removeWidgetId(widgetId: Int) {
        val currentList = _uiState.value.topWidgetIds
        val newList = currentList - widgetId
        _uiState.update { it.copy(topWidgetIds = newList) }
        widgetRepository.saveWidgetIds(newList)
    }

    // --- Settings & Side Panel Actions ---

    fun toggleLeftHandedMode() {
        val newValue = !_uiState.value.isLeftHandedMode
        settingsRepository.isLeftHandedMode = newValue
        _uiState.update { it.copy(isLeftHandedMode = newValue) }
    }

    fun toggleSidePanel() {
        val newValue = !_uiState.value.isSidePanelEnabled
        settingsRepository.isSidePanelEnabled = newValue
        _uiState.update { it.copy(isSidePanelEnabled = newValue) }
    }

    fun setSidePanelPosition(position: String) {
        settingsRepository.sidePanelPosition = position
        _uiState.update { it.copy(sidePanelPosition = position) }
    }

    fun toggleHomeOpensAllApps() {
        val newValue = !_uiState.value.homeButtonOpensAllApps
        settingsRepository.homeButtonOpensAllApps = newValue
        _uiState.update { it.copy(homeButtonOpensAllApps = newValue) }
    }

    fun toggleShowAllAppsOnHome() {
        val newValue = !_uiState.value.showAllAppsOnHome
        settingsRepository.showAllAppsOnHome = newValue
        _uiState.update { it.copy(showAllAppsOnHome = newValue) }
    }

    fun setShowSettingsDialog(show: Boolean) {
        _uiState.update { it.copy(showSettingsDialog = show) }
    }

    fun setShowFavoritePicker(show: Boolean) {
        _uiState.update { it.copy(showFavoritePickerDialog = show) }
    }

    fun setAllAppsDrawerOpen(open: Boolean) {
        _uiState.update { it.copy(isAllAppsDrawerOpen = open) }
        if (open) {
            _uiState.update { it.copy(searchQuery = "") }
            applySearchFilter("")
        }
    }

    // --- Favorites Actions ---

    fun toggleFavorite(packageName: String) {
        val currentFavs = _uiState.value.favoriteAppPackages.toMutableList()
        if (currentFavs.contains(packageName)) {
            currentFavs.remove(packageName)
        } else {
            currentFavs.add(packageName)
        }
        settingsRepository.favoriteAppPackages = currentFavs
        _uiState.update { it.copy(favoriteAppPackages = currentFavs) }
        updateAppsState(_uiState.value.apps)
    }

    // --- Search Actions ---

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applySearchFilter(query)
    }

    private fun applySearchFilter(query: String) {
        val allApps = _uiState.value.apps
        if (query.isBlank()) {
            val (alphabet, indexMap) = computeAlphabetAndIndexMap(allApps)
            _uiState.update {
                it.copy(
                    filteredApps = allApps,
                    alphabet = alphabet,
                    letterIndexMap = indexMap
                )
            }
        } else {
            val filtered = allApps.filter { it.label.contains(query, ignoreCase = true) }
            _uiState.update {
                it.copy(
                    filteredApps = filtered,
                    alphabet = emptyList(),
                    letterIndexMap = emptyMap()
                )
            }
        }
    }

    private fun updateAppsState(apps: List<AppInfo>) {
        val (alphabet, indexMap) = computeAlphabetAndIndexMap(apps)

        val favoritePackages = _uiState.value.favoriteAppPackages
        val favApps = apps.filter { favoritePackages.contains(it.packageName) }
            .sortedBy { favoritePackages.indexOf(it.packageName) }

        val shouldKeepLoading = apps.isEmpty() && _uiState.value.isLoading

        _uiState.update {
            it.copy(
                apps = apps,
                filteredApps = apps,
                favoriteApps = favApps,
                alphabet = alphabet,
                letterIndexMap = indexMap,
                isLoading = shouldKeepLoading,
            )
        }
    }

    private fun computeAlphabetAndIndexMap(apps: List<AppInfo>): Pair<List<Char>, Map<Char, Int>> {
        val fullAlphabet = listOf('☆', '#') + ('A'..'Z').toList()

        val firstOccurrenceMap = mutableMapOf<Char, Int>()
        apps.forEachIndexed { index, app ->
            val letter = app.firstLetter.uppercaseChar()
            firstOccurrenceMap.putIfAbsent(letter, index)
        }

        val indexMap = fullAlphabet.associateWith { char ->
            getAlphabetCharIndex(char, apps, firstOccurrenceMap)
        }

        return Pair(fullAlphabet, indexMap)
    }

    private fun getAlphabetCharIndex(
        char: Char,
        apps: List<AppInfo>,
        firstOccurrenceMap: Map<Char, Int>
    ): Int {
        val exactIndex = if (char == '☆' || char == '#') 0 else firstOccurrenceMap[char]
        return exactIndex ?: run {
            val nextIndex = apps.indexOfFirst { it.firstLetter.uppercaseChar() > char }
            if (nextIndex != -1) nextIndex else (apps.size - 1).coerceAtLeast(0)
        }
    }
}
