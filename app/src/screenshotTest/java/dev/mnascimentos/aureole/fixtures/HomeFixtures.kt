package dev.mnascimentos.aureole.fixtures

import android.appwidget.AppWidgetHost
import android.content.ComponentName
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDimens
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleThemeConfig
import dev.mnascimentos.aureole.feature.home.HomeScreen
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions

/** Dados de exemplo da home, compartilhados pelas telas (sem `@PreviewTest`). */
internal data class SampleHomeData(
    val apps: List<AppInfo>,
    val folders: List<AppFolder>,
    val uiState: MainUiState,
    val actions: HomeScreenActions
)

internal fun sampleApp(label: String, packageName: String, color: Long) = AppInfo(
    label = label,
    packageName = packageName,
    componentName = ComponentName(packageName, "MainActivity"),
    icon = ColorDrawable(color.toInt())
)

internal fun createSampleHomeData(): SampleHomeData {
    val camera = sampleApp("Camera", "com.example.camera", 0xFF1B65C0)
    val phone = sampleApp("Phone", "com.example.phone", 0xFF2E7D32)
    val messages = sampleApp("Messages", "com.example.messages", 0xFFF57C00)
    val browser = sampleApp("Browser", "com.example.browser", 0xFF0288D1)
    val settings = sampleApp("Settings", "com.example.settings", 0xFF5D4037)
    val apps = listOf(browser, camera, messages, phone, settings)
    val favorites = listOf(phone, messages, camera, browser)
    val folders = listOf(
        AppFolder(
            id = "folder_work",
            name = "Work",
            icon = "briefcase",
            appPackageNames = listOf(phone, messages, camera, browser).map { it.packageName }
        ),
        AppFolder(
            id = "folder_social",
            name = "Social",
            icon = "message",
            appPackageNames = listOf(browser.packageName, camera.packageName)
        )
    )
    val uiState = MainUiState(
        apps = apps,
        favoriteApps = favorites,
        favoriteAppPackages = favorites.map { it.packageName },
        folders = folders,
        gridItems = GridDefaults.getDefaultGridItems(),
        alphabet = listOf('B', 'C', 'M', 'P', 'S'),
        isLoading = false,
        isHazeEnabled = false,
        isContainerEnabled = true,
        showFolderLabels = true
    )
    return SampleHomeData(apps, folders, uiState, createDefaultHomeScreenActions())
}

/**
 * Tema do launcher com os locals da home (estado, mão e ações) que as sheets leem. Na prévia de paisagem, a
 * configuração e o estado ficam de paisagem (`OrientedCase`, `inLandscape`).
 */
@Composable
internal fun HomeCase(
    uiState: MainUiState,
    dimens: AureoleDimens? = null,
    content: @Composable () -> Unit
) {
    val state = if (isPreviewLandscape()) uiState.inLandscape() else uiState
    val locals: @Composable () -> Unit = {
        CompositionLocalProvider(
            LocalHomeUiState provides state,
            LocalLeftHanded provides state.isLeftHandedMode,
            LocalHomeActions provides createDefaultHomeScreenActions()
        ) {
            content()
        }
    }
    OrientedCase {
        if (dimens == null) {
            AureoleLauncherTheme(content = locals)
        } else {
            AureoleLauncherTheme(AureoleThemeConfig(darkTheme = isSystemInDarkTheme(), aureoleDimens = dimens), locals)
        }
    }
}

/** A home inteira com [uiState]. */
@Composable
internal fun HomeScreenCase(uiState: MainUiState, dimens: AureoleDimens? = null) {
    val context = LocalContext.current
    HomeCase(uiState, dimens) {
        HomeScreen(appWidgetHost = AppWidgetHost(context, HOST_ID))
    }
}

private const val HOST_ID = 1024
