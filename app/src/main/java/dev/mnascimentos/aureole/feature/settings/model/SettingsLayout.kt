package dev.mnascimentos.aureole.feature.settings.model

/** How the settings screens arrange their content for the available area. */
enum class SettingsLayout { SINGLE_COLUMN, TWO_PANES }

/** Landscape (wider than tall) uses two panes; portrait and square areas keep the single column. */
fun settingsLayoutFor(widthDp: Float, heightDp: Float): SettingsLayout =
    if (widthDp > heightDp) SettingsLayout.TWO_PANES else SettingsLayout.SINGLE_COLUMN

/** Callbacks of the three entries on the settings root. */
data class SettingsNavigationActions(
    val onNavigateToBehavior: () -> Unit,
    val onNavigateToAppearance: () -> Unit,
    val onNavigateToAdvanced: () -> Unit
)
