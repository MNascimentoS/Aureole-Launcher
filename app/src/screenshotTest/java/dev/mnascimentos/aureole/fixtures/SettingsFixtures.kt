package dev.mnascimentos.aureole.fixtures

import androidx.compose.runtime.Composable
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.settings.components.SettingsSheetHost
import dev.mnascimentos.aureole.feature.settings.model.SettingsArea
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsSheet
import dev.mnascimentos.aureole.feature.settings.model.SettingsSheetHostParams
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.feature.settings.screens.SettingsRootScreen

/** A raiz dos ajustes sozinha, com [uiState]. */
@Composable
internal fun SettingsRootCase(uiState: SettingsUiState = SettingsUiState()) = OrientedCase {
    AureoleLauncherTheme {
        SettingsRootScreen(
            uiState = uiState,
            onNavigateToBehavior = {},
            onNavigateToAppearance = {},
            onNavigateToAdvanced = {}
        )
    }
}

/** A raiz dos ajustes com [sheet] aberta por cima, como o usuário vê (sem `@PreviewTest`). */
@Composable
internal fun SettingsSheetCase(
    sheet: SettingsSheet,
    uiState: SettingsUiState = SettingsUiState(),
    scroll: Int = 0
) = OrientedCase {
    AureoleLauncherTheme {
        SettingsSheetHost(
            params = SettingsSheetHostParams(
                uiState = uiState,
                actions = SettingsScreenActions(),
                onNavigateToLicenses = {},
                initialSheet = sheet,
                initialScroll = scroll
            )
        ) { onOpenArea ->
            SettingsRootScreen(
                uiState = uiState,
                onNavigateToBehavior = { onOpenArea(SettingsArea.BEHAVIOR) },
                onNavigateToAppearance = { onOpenArea(SettingsArea.APPEARANCE) },
                onNavigateToAdvanced = { onOpenArea(SettingsArea.ADVANCED) }
            )
        }
    }
}
