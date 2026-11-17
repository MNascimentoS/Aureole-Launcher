package dev.mnascimentos.aureole.feature.settings

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.mnascimentos.aureole.feature.settings.components.SettingsSheetHost
import dev.mnascimentos.aureole.feature.settings.model.SettingsArea
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.feature.settings.screens.SettingsLicensesScreen
import dev.mnascimentos.aureole.feature.settings.screens.SettingsRootScreen

/** Full screens of the settings: the root (its areas open as sheets on top) and the open source licenses. */
sealed class SettingsRoute(val route: String) {
    object Root : SettingsRoute("root")
    object Licenses : SettingsRoute("licenses")
}

@Composable
fun SettingsNavGraph(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = SettingsRoute.Root.route
    ) {
        composable(SettingsRoute.Root.route) {
            SettingsSheetHost(
                uiState = uiState,
                actions = actions,
                onNavigateToLicenses = { navController.navigate(SettingsRoute.Licenses.route) }
            ) { onOpenArea ->
                SettingsRootScreen(
                    uiState = uiState,
                    onNavigateToBehavior = { onOpenArea(SettingsArea.BEHAVIOR) },
                    onNavigateToAppearance = { onOpenArea(SettingsArea.APPEARANCE) },
                    onNavigateToAdvanced = { onOpenArea(SettingsArea.ADVANCED) }
                )
            }
        }

        composable(SettingsRoute.Licenses.route) {
            SettingsLicensesScreen(
                uiState = uiState,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
