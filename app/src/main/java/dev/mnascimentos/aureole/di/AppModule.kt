package dev.mnascimentos.aureole.di

import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.widgets.WidgetPickerViewModel
import dev.mnascimentos.aureole.feature.settings.SettingsViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::AppRepository)
    singleOf(::WidgetRepository)
    singleOf(::SettingsRepository)
    singleOf(::FolderRepository)
    singleOf(::FavoriteContainerRepository)
    singleOf(::ContainerRepository)
    singleOf(::GridRepository)

    viewModelOf(::HomeViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::WidgetPickerViewModel)
}
