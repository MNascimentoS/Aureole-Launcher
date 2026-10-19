package dev.mnascimentos.aureole.core.lifecycle

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.model.HomeRepositories
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class MainLifecycleObserverTest {

    private lateinit var context: Context
    private lateinit var viewModel: HomeViewModel
    private lateinit var observer: MainLifecycleObserver
    private var wasResumedCalled = false

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        val settingsRepository = SettingsRepository(context)
        val appRepository = AppRepository(context)
        val widgetRepository = WidgetRepository(context)
        val containerRepository = ContainerRepository(context)
        val folderRepository = FolderRepository(context)
        val favoriteContainerRepository = FavoriteContainerRepository(context, settingsRepository)
        val gridRepository = GridRepository(context)

        viewModel = HomeViewModel(
            HomeRepositories(
                appRepository = appRepository,
                widgetRepository = widgetRepository,
                settingsRepository = settingsRepository,
                folderRepository = folderRepository,
                favoriteContainerRepository = favoriteContainerRepository,
                containerRepository = containerRepository,
                gridRepository = gridRepository,
            )
        )

        observer = MainLifecycleObserver(
            context = context,
            viewModel = viewModel,
            onResumed = { wasResumedCalled = true },
        )
    }

    @Test
    fun `when onResume triggered, should load data and invoke onResumed callback`() {
        val dummyOwner = object : LifecycleOwner {
            override val lifecycle: Lifecycle
                get() = error("Not needed")
        }

        observer.onResume(dummyOwner)

        assertTrue(wasResumedCalled)
    }
}
