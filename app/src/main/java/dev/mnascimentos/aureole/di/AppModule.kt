package dev.mnascimentos.aureole.di

import dev.mnascimentos.aureole.core.data.db.AppDatabase
import dev.mnascimentos.aureole.core.data.db.ContainerDao
import dev.mnascimentos.aureole.core.data.db.FavoriteContainerDao
import dev.mnascimentos.aureole.core.data.db.FolderDao
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.feature.home.HomeRepositories
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.widgets.WidgetPickerViewModel
import dev.mnascimentos.aureole.feature.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { AppDatabase.getInstance(androidContext()) }
    single { get<AppDatabase>().appInfoDao() }
    single { get<AppDatabase>().containerDao() }
    single { get<AppDatabase>().favoriteContainerDao() }
    single { get<AppDatabase>().folderDao() }

    singleOf(::AppRepository)
    singleOf(::WidgetRepository)
    singleOf(::SettingsRepository)
    single<FolderRepository> { FolderRepository(get(), androidContext()) }
    single<FavoriteContainerRepository> {
        FavoriteContainerRepository(
            get<FavoriteContainerDao>(),
            get<SettingsRepository>()
        )
    }
    single<ContainerRepository> { ContainerRepository(get<ContainerDao>(), get<FolderDao>()) }
    singleOf(::GridRepository)
    factoryOf(::HomeRepositories)

    viewModelOf(::HomeViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::WidgetPickerViewModel)
}
