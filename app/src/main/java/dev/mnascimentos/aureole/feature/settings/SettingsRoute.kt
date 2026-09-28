package dev.mnascimentos.aureole.feature.settings

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.feature.settings.screens.SettingsAdvancedScreen
import dev.mnascimentos.aureole.feature.settings.screens.SettingsAppearanceScreen
import dev.mnascimentos.aureole.feature.settings.screens.SettingsBehaviorScreen
import dev.mnascimentos.aureole.feature.settings.screens.SettingsContributorsScreen
import dev.mnascimentos.aureole.feature.settings.screens.SettingsRootScreen

sealed class SettingsRoute(val route: String) {
    object Root : SettingsRoute("root")
    object Behavior : SettingsRoute("behavior")
    object Appearance : SettingsRoute("appearance")
    object Advanced : SettingsRoute("advanced")
    object Contributors : SettingsRoute("contributors")
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
            SettingsRootScreen(
                uiState = uiState,
                actions = actions,
                onNavigateToBehavior = { navController.navigate(SettingsRoute.Behavior.route) },
                onNavigateToAppearance = { navController.navigate(SettingsRoute.Appearance.route) },
                onNavigateToAdvanced = { navController.navigate(SettingsRoute.Advanced.route) }
            )
        }

        composable(SettingsRoute.Behavior.route) {
            SettingsBehaviorScreen(
                uiState = uiState,
                actions = actions,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(SettingsRoute.Appearance.route) {
            SettingsAppearanceScreen(
                uiState = uiState,
                actions = actions,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(SettingsRoute.Advanced.route) {
            SettingsAdvancedScreen(
                uiState = uiState,
                actions = actions,
                onNavigateToContributors = { navController.navigate(SettingsRoute.Contributors.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(SettingsRoute.Contributors.route) {
            SettingsContributorsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
