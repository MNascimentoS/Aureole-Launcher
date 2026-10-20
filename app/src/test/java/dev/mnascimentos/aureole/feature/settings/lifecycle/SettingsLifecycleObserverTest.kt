package dev.mnascimentos.aureole.feature.settings.lifecycle

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.feature.settings.SettingsViewModel
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class SettingsLifecycleObserverTest {

    private lateinit var context: Context
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var appRepository: AppRepository
    private lateinit var folderRepository: FolderRepository
    private lateinit var gridRepository: GridRepository
    private lateinit var viewModel: SettingsViewModel
    private lateinit var observer: SettingsLifecycleObserver

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        settingsRepository = SettingsRepository(context)
        appRepository = AppRepository(context)
        folderRepository = FolderRepository(context)
        gridRepository = GridRepository(context)

        viewModel = SettingsViewModel(
            settingsRepository = settingsRepository,
            appRepository = appRepository,
            folderRepository = folderRepository,
            gridRepository = gridRepository,
        )
        observer = SettingsLifecycleObserver(viewModel)
    }

    @Test
    fun `when onResume triggered, should reload settings`() {
        settingsRepository.isLeftHandedMode = true
        val dummyOwner = object : LifecycleOwner {
            override val lifecycle: Lifecycle
                get() = error("Not needed")
        }

        observer.onResume(dummyOwner)

        assertTrue(viewModel.uiState.value.isLeftHandedMode)
    }
}
