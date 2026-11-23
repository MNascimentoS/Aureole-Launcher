package dev.mnascimentos.aureole.composable.controls.model

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp

/**
 * How the segments of a segmented control look and read.
 *
 * @property itemHeight Height of each segment; null takes the 48 dp touch target.
 * @property role What TalkBack calls each segment: a tab by default; [Role.RadioButton] for a choice of values.
 */
data class SegmentStyle(val itemHeight: Dp? = null, val role: Role = Role.Tab)
