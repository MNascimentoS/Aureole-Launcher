package dev.mnascimentos.aureole.core.designsystem.utils

import androidx.compose.ui.platform.ViewConfiguration

/**
 * [ViewConfiguration] that keeps every system value except the long press timeout.
 * Lets a single gesture area wait longer than the system default before it fires a long press.
 */
internal class LongPressViewConfiguration(
    base: ViewConfiguration,
    override val longPressTimeoutMillis: Long
) : ViewConfiguration by base
