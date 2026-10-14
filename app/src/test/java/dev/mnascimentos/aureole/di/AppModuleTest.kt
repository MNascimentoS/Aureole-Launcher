package dev.mnascimentos.aureole.di

import android.app.Application
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.settings.SettingsViewModel
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class AppModuleTest : KoinTest {

    private lateinit var application: Application

    @Before
    fun setUp() {
        application = RuntimeEnvironment.getApplication()
        if (GlobalContext.getOrNull() == null) {
            startKoin {
                androidContext(application)
                modules(appModule)
            }
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun testRepositoriesAreResolved() {
        assertNotNull(get<AppRepository>())
        assertNotNull(get<WidgetRepository>())
        assertNotNull(get<SettingsRepository>())
        assertNotNull(get<FolderRepository>())
        assertNotNull(get<FavoriteContainerRepository>())
        assertNotNull(get<ContainerRepository>())
        assertNotNull(get<GridRepository>())
    }

    @Test
    fun testViewModelsAreResolved() {
        assertNotNull(get<HomeViewModel>())
        assertNotNull(get<SettingsViewModel>())
    }
}
