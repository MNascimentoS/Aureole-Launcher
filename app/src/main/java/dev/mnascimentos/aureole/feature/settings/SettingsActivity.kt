package dev.mnascimentos.aureole.feature.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.palette.getPaletteByName
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDimens
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleThemeConfig
import dev.mnascimentos.aureole.core.lifecycle.SettingsLifecycleObserver
import dev.mnascimentos.aureole.feature.settings.screens.SettingsRoute
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : ComponentActivity() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycle.addObserver(SettingsLifecycleObserver(viewModel))

        setContent {
            val uiState by viewModel.uiState.collectAsState()

            val isDarkTheme = isSystemInDarkTheme()
            val palette = getPaletteByName(uiState.selectedThemeName, isDarkTheme, uiState.manualSeedColor)
            AureoleLauncherTheme(
                config = AureoleThemeConfig(
                    darkTheme = isDarkTheme,
                    isDynamicWallpaperEnabled = uiState.isDynamicWallpaperEnabled,
                    aureoleColors = palette.colors,
                    aureoleFontName = uiState.selectedFontName,
                    aureoleDimens = AureoleDimens(radiusLarge = uiState.cornerRadiusDp.dp)
                )
            ) {
                LaunchedEffect(uiState.shouldFinishActivity) {
                    if (uiState.shouldFinishActivity) {
                        viewModel.onActivityFinishedHandled()
                        finish()
                    }
                }

                SettingsRoute(
                    uiState = uiState,
                    viewModel = viewModel,
                    onFinish = { finish() }
                )
            }
        }
    }
}
