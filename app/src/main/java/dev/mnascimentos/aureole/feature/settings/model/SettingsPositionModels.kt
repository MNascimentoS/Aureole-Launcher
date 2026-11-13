package dev.mnascimentos.aureole.feature.settings.model

import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R

/** The settings that are picked from a list of positions. */
enum class PositionKind {
    SETTINGS_BUTTON,
    SEARCH_ICON,
    CONTAINER
}

/** One position that can be picked: the stored [value] and the string shown on its row. */
data class PositionOption(
    val value: String,
    @StringRes val labelRes: Int
)

private val SIDE_OPTIONS = listOf(
    PositionOption("Left", R.string.settings_position_option_left),
    PositionOption("Right", R.string.settings_position_option_right)
)

private val CONTAINER_OPTIONS = listOf(
    PositionOption("Top", R.string.settings_position_option_top),
    PositionOption("Center", R.string.settings_position_option_center),
    PositionOption("Bottom", R.string.settings_position_option_bottom),
    PositionOption("Space Evenly", R.string.settings_position_option_space_evenly),
    PositionOption("Space Between", R.string.settings_position_option_space_between)
)

/** Positions offered for this setting, in the order they are listed. */
fun PositionKind.options(): List<PositionOption> = when (this) {
    PositionKind.SETTINGS_BUTTON, PositionKind.SEARCH_ICON -> SIDE_OPTIONS
    PositionKind.CONTAINER -> CONTAINER_OPTIONS
}

/** The value currently stored for this setting. */
fun SettingsUiState.positionOf(kind: PositionKind): String = when (kind) {
    PositionKind.SETTINGS_BUTTON -> settingsButtonPosition
    PositionKind.SEARCH_ICON -> searchIconPosition
    PositionKind.CONTAINER -> containerPosition
}

/** The [SettingValue] that stores [position] for this setting. */
fun PositionKind.toSettingValue(position: String): SettingValue = when (this) {
    PositionKind.SETTINGS_BUTTON -> SettingValue.SettingsButtonPosition(position)
    PositionKind.SEARCH_ICON -> SettingValue.SearchIconPosition(position)
    PositionKind.CONTAINER -> SettingValue.ContainerPosition(position)
}
