package dev.mnascimentos.aureole.feature.home.components.clock.extensions

import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.placeNewGridItem
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import kotlinx.coroutines.launch

/**
 * Customize › Add clock: places a new clock (a fresh UUID, even if the home has clocks) at the layout's clock spot
 * when it is free, else in the largest free slot, and leaves the grid in edit mode, as before. Placing it gives it
 * its own copy of the default look ([giveNewClockItsConfig]), so it does not follow later changes of that default.
 */
fun HomeViewModel.addClock() {
    placeNewGridItem(LauncherItemType.CLOCK, alwaysNewId = true)
}

/**
 * Saves the default look as the own config of the clock [clockId] that was just placed. A clock that already has
 * a config in the state keeps it, so the two placement paths that call it for the same clock save it once.
 */
internal fun HomeViewModel.giveNewClockItsConfig(clockId: String) {
    if (uiState.value.clockConfigs.containsKey(clockId)) return
    val config = clockConfigRepository.defaultConfig()
    updateUiState { it.copy(clockConfigs = it.clockConfigs + (clockId to config)) }
    viewModelScope.launch { clockConfigRepository.save(clockId, config) }
}

/**
 * Erases the config of the clock [clockId], just removed from the home, and returns what puts it back; null when
 * the config must stay. It stays in the grid edit mode, which can still be cancelled and bring the clock back, and
 * while the other layout (portrait or landscape) still shows a clock with the same id, which shares the config.
 * The delete is immediate and the undo saves the snapshot, as for the favorites lists.
 */
internal fun HomeViewModel.dropClockConfig(clockId: String): (() -> Unit)? {
    val state = uiState.value
    if (state.isGridEditMode || state.hasItemOnAnyLayout(clockId)) return null
    val snapshot = state.clockConfigOf(clockId)
    viewModelScope.launch { clockConfigRepository.remove(clockId) }
    return { viewModelScope.launch { clockConfigRepository.save(clockId, snapshot) } }
}

/** True when the portrait or the landscape layout has an item [id], at the top level or inside a scroll view. */
internal fun MainUiState.hasItemOnAnyLayout(id: String): Boolean =
    (portraitGridItems + landscapeGridItems).any { it.hasItem(id) }

private fun LauncherItemState.hasItem(id: String): Boolean = this.id == id || safeChildren.any { it.hasItem(id) }
