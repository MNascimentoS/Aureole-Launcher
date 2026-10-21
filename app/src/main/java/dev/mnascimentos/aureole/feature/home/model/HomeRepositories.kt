package dev.mnascimentos.aureole.feature.home.model

import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository

data class HomeRepositories(
    val appRepository: AppRepository,
    val widgetRepository: WidgetRepository,
    val settingsRepository: SettingsRepository,
    val folderRepository: FolderRepository,
    val favoriteContainerRepository: FavoriteContainerRepository,
    val containerRepository: ContainerRepository,
    val gridRepository: GridRepository,
)
