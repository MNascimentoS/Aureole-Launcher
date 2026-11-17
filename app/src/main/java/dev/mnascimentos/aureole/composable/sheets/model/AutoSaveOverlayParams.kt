package dev.mnascimentos.aureole.composable.sheets.model

import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayState
import dev.mnascimentos.aureole.core.designsystem.model.OverlayHostSpec

/**
 * Parameters of `AureoleAutoSaveOverlay`.
 *
 * @property title Title of the sheet.
 * @property isChanged Whether what the user chose differs from what the sheet opened with. Read when the sheet
 * closes, so it may look at state that changed while the sheet was open.
 * @property onCommit Saves the choice. Runs once, only when [isChanged], right before [onDismissRequest].
 * @property onDismissRequest Called when the overlay has closed, whichever way it closed.
 * @property isLeftHanded Mirrors the sheet for the left thumb.
 * @property onSettingsClick Opens the Aureole settings from the header logo; null hides the logo.
 * @property hazeState Blur state of the screen behind the overlay.
 * @property hasExternalBackdrop True when the screen around draws the backdrop itself.
 * @property host What the screen hosting several overlays decides for all of them.
 * @property dismissOnTapOutside Whether a tap outside the sheet closes (and saves) it.
 * @property overlayState State of the sheet, for a caller that follows it while it opens and closes; null keeps
 * one inside.
 */
data class AutoSaveOverlayParams(
    val title: String,
    val isChanged: () -> Boolean,
    val onCommit: () -> Unit,
    val onDismissRequest: () -> Unit,
    val isLeftHanded: Boolean = false,
    val onSettingsClick: (() -> Unit)? = null,
    val hazeState: HazeState? = null,
    val hasExternalBackdrop: Boolean = false,
    val host: OverlayHostSpec = OverlayHostSpec(),
    val dismissOnTapOutside: Boolean = true,
    val overlayState: AureoleEditOverlayState? = null
)

/** What closing an auto-save sheet has to do. */
enum class CloseOutcome { COMMIT, NO_CHANGE }

/**
 * Closing saves once, and only when something changed: [alreadyCommitted] guards against the close being reported
 * twice (back pressed while the sheet was already sliding out).
 */
fun closeOutcome(isChanged: Boolean, alreadyCommitted: Boolean): CloseOutcome =
    if (isChanged && !alreadyCommitted) CloseOutcome.COMMIT else CloseOutcome.NO_CHANGE

/** Same as above, comparing what the sheet opened with against what the user has now. */
fun <T> closeOutcome(initial: T, current: T, alreadyCommitted: Boolean = false): CloseOutcome =
    closeOutcome(isChanged = initial != current, alreadyCommitted = alreadyCommitted)
