package dev.mnascimentos.aureole.feature.home.components.clock.extensions

import androidx.compose.ui.unit.DpSize
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/** Key of the clock of the list header, which has no grid item id, in `MainUiState.clockSizes`. */
const val HEADER_CLOCK_SIZE_KEY = "clock_header"

/** Key of the clock [clockId] in `MainUiState.clockSizes`: its grid item id, or the list header's. */
fun clockSizeKey(clockId: String?): String = clockId ?: HEADER_CLOCK_SIZE_KEY

/** Size the clock [clockId] has on the home; null while it was never measured. */
fun MainUiState.clockSizeOf(clockId: String?): DpSize? = clockSizes[clockSizeKey(clockId)]

/** [this] state with the clock [clockId] measured at [size]. */
fun MainUiState.withClockSize(clockId: String?, size: DpSize): MainUiState =
    copy(clockSizes = clockSizes + (clockSizeKey(clockId) to size))

/** Size on the home of the clock the editor is changing; null while it was never measured. */
val MainUiState.editingClockSize: DpSize?
    get() = clockSizeOf(editingClockId)
