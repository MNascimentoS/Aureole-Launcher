package dev.mnascimentos.aureole.feature.home.components.widgets

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.addWidgetId
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.setTargetWidgetStackId
import dev.mnascimentos.aureole.feature.home.model.HomeRepositories
import dev.mnascimentos.aureole.feature.home.model.getWidgetIdsForStack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class WidgetHostManagerTest {

    private lateinit var context: Context
    private lateinit var viewModel: HomeViewModel
    private lateinit var appWidgetHost: AppWidgetHost
    private lateinit var appWidgetManager: AppWidgetManager

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        val settingsRepository = SettingsRepository(context)

        viewModel = HomeViewModel(
            HomeRepositories(
                appRepository = AppRepository(context),
                widgetRepository = WidgetRepository(context),
                settingsRepository = settingsRepository,
                folderRepository = FolderRepository(context),
                favoriteContainerRepository = FavoriteContainerRepository(context, settingsRepository),
                containerRepository = ContainerRepository(context),
                gridRepository = GridRepository(context),
            )
        )
        appWidgetHost = object : AppWidgetHost(context, 1024) {
            override fun allocateAppWidgetId(): Int = 1234
        }
        appWidgetManager = AppWidgetManager.getInstance(context)
    }

    @Test
    fun `when handleWidgetSelected with null provider, should do nothing`() {
        val manager = WidgetHostManager(
            viewModel = viewModel,
            appWidgetHost = appWidgetHost,
            appWidgetManager = appWidgetManager
        )

        manager.handleWidgetSelected(null)

        assertEquals(-1, viewModel.uiState.value.pendingWidgetId)
    }

    @Test
    fun `when handleWidgetSelected is called with valid provider, should allocate id and launch bind intent`() {
        var bindIntent: Intent? = null
        val manager = WidgetHostManager(
            viewModel = viewModel,
            appWidgetHost = appWidgetHost,
            appWidgetManager = appWidgetManager,
            launchWidgetBind = { bindIntent = it }
        )

        val provider = AppWidgetProviderInfo().apply {
            provider = ComponentName("com.example.app", "com.example.app.WidgetProvider")
            minWidth = 100
            minHeight = 100
        }

        manager.handleWidgetSelected(provider)

        assertNotNull(bindIntent)
        assertEquals(AppWidgetManager.ACTION_APPWIDGET_BIND, bindIntent?.action)
        assertEquals(1234, viewModel.uiState.value.pendingWidgetId)
    }

    @Test
    fun `when completeWidgetConfiguration is called with valid id, should add widget to viewModel`() {
        val manager = WidgetHostManager(
            viewModel = viewModel,
            appWidgetHost = appWidgetHost,
            appWidgetManager = appWidgetManager
        )

        val provider = AppWidgetProviderInfo().apply {
            provider = ComponentName("com.example.app", "com.example.app.WidgetProvider")
            minWidth = 100
            minHeight = 100
        }

        manager.completeWidgetConfiguration(777, provider)

        assertTrue(viewModel.uiState.value.getWidgetIdsForStack("top_widgets").contains(777))
    }

    @Test
    fun `when completeWidgetConfiguration is called with null provider, should still add widget to viewModel`() {
        val manager = WidgetHostManager(
            viewModel = viewModel,
            appWidgetHost = appWidgetHost,
            appWidgetManager = appWidgetManager
        )

        manager.completeWidgetConfiguration(888, null)

        assertTrue(viewModel.uiState.value.getWidgetIdsForStack("top_widgets").contains(888))
        assertEquals(-1, viewModel.uiState.value.pendingWidgetId)
    }

    @Test
    fun `when completeWidgetConfiguration is called with INVALID_APPWIDGET_ID, should handle gracefully`() {
        val manager = WidgetHostManager(
            viewModel = viewModel,
            appWidgetHost = appWidgetHost,
            appWidgetManager = appWidgetManager
        )

        val provider = AppWidgetProviderInfo().apply {
            provider = ComponentName("com.example.app", "com.example.app.WidgetProvider")
        }

        manager.completeWidgetConfiguration(AppWidgetManager.INVALID_APPWIDGET_ID, provider)

        assertEquals(-1, viewModel.uiState.value.pendingWidgetId)
    }

    @Test
    fun `when launchWidgetBind throws ActivityNotFoundException, should reset pending widget id`() {
        val manager = WidgetHostManager(
            viewModel = viewModel,
            appWidgetHost = appWidgetHost,
            appWidgetManager = appWidgetManager,
            launchWidgetBind = { throw ActivityNotFoundException() }
        )

        val provider = AppWidgetProviderInfo().apply {
            provider = ComponentName("com.example.app", "com.example.app.WidgetProvider")
        }

        manager.handleWidgetSelected(provider)

        assertEquals(-1, viewModel.uiState.value.pendingWidgetId)
    }

    @Test
    fun `when launchWidgetBind throws SecurityException, should reset pending widget id`() {
        val manager = WidgetHostManager(
            viewModel = viewModel,
            appWidgetHost = appWidgetHost,
            appWidgetManager = appWidgetManager,
            launchWidgetBind = { throw SecurityException() }
        )

        val provider = AppWidgetProviderInfo().apply {
            provider = ComponentName("com.example.app", "com.example.app.WidgetProvider")
        }

        manager.handleWidgetSelected(provider)

        assertEquals(-1, viewModel.uiState.value.pendingWidgetId)
    }

    @Test
    fun `when removeWidget is called, should remove widget from viewModel`() {
        val manager = WidgetHostManager(
            viewModel = viewModel,
            appWidgetHost = appWidgetHost,
            appWidgetManager = appWidgetManager
        )

        val widgetId = 600
        viewModel.setTargetWidgetStackId("top_widgets")
        viewModel.addWidgetId(widgetId)

        manager.removeWidget(widgetId, "top_widgets")

        assertTrue(!viewModel.uiState.value.getWidgetIdsForStack("top_widgets").contains(widgetId))
    }
}
