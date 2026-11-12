package dev.mnascimentos.aureole.feature.home

import android.appwidget.AppWidgetHost
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState

@Composable
private fun LandscapeHome(isLeftHanded: Boolean) {
    val context = LocalContext.current
    val sampleData = createSampleHomeData()
    val items = GridDefaults.getDefaultGridItems(isLandscape = true, isLeftHanded = isLeftHanded)
    val uiState = sampleData.uiState.copy(
        isLandscape = true,
        isLeftHandedMode = isLeftHanded,
        gridItems = items,
        landscapeGridItems = items
    )
    val landscape = Configuration(LocalConfiguration.current).apply { orientation = Configuration.ORIENTATION_LANDSCAPE }

    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalConfiguration provides landscape,
            LocalHomeUiState provides uiState,
            LocalLeftHanded provides isLeftHanded,
            LocalHomeActions provides sampleData.actions
        ) {
            HomeScreen(appWidgetHost = AppWidgetHost(context, 1024))
        }
    }
}

@PreviewTest
@AureoleLandscapePreview
@Composable
private fun HomeScreenLandscapeRightHandedScreenshotTest() {
    LandscapeHome(isLeftHanded = false)
}

@PreviewTest
@AureoleLandscapePreview
@Composable
private fun HomeScreenLandscapeLeftHandedScreenshotTest() {
    LandscapeHome(isLeftHanded = true)
}
