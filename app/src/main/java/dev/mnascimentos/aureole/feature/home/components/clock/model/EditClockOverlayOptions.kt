package dev.mnascimentos.aureole.feature.home.components.clock.model

import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayState

/**
 * Optional inputs of the clock editor overlay.
 *
 * @property hazeState Blur source behind the overlay; ignored when haze is off in settings.
 * @property overlayState Sheet state; previews and tests start it expanded.
 * @property initialTab Tab the pager starts on.
 * @property nowMillis Instant the format tiles preview; tests fix it.
 */
class EditClockOverlayOptions(
    val hazeState: HazeState? = null,
    val overlayState: AureoleEditOverlayState = AureoleEditOverlayState(),
    val initialTab: ClockTab = ClockTab.STYLE,
    val nowMillis: Long = System.currentTimeMillis()
)
