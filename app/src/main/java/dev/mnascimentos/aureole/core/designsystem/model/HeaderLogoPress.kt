package dev.mnascimentos.aureole.core.designsystem.model

/** How long the logo takes to shrink and tilt under the finger. */
const val HEADER_LOGO_PRESS_MS = 180

/** Damping of the spring that brings the logo back: below 1, so it overshoots a little. */
const val HEADER_LOGO_RELEASE_DAMPING = 0.45f

/**
 * Pose of the sheet header logo (RF-06).
 *
 * @property scale Size of the logo, 1 at rest.
 * @property rotation Tilt in degrees, negative to the left.
 * @property haloAlpha Opacity of the light circle drawn behind the logo while it is held.
 */
data class HeaderLogoPose(
    val scale: Float,
    val rotation: Float,
    val haloAlpha: Float
)

/** The logo at rest. */
val HeaderLogoRestPose = HeaderLogoPose(scale = 1f, rotation = 0f, haloAlpha = 0f)

/** The logo while a finger is on it: smaller, tilted, with a light circle behind. */
val HeaderLogoPressedPose = HeaderLogoPose(scale = 0.86f, rotation = -18f, haloAlpha = 1f)

/**
 * Where the logo goes: the pressed pose while it is held, the rest pose otherwise. With the system animations off
 * nothing moves, so it always stays at rest (a tap still opens the settings).
 */
fun headerLogoPose(isPressed: Boolean, animationsEnabled: Boolean): HeaderLogoPose =
    if (isPressed && animationsEnabled) HeaderLogoPressedPose else HeaderLogoRestPose

/**
 * Whether the sheet header draws its end action: only when it is allowed ([showHeaderAction]) and it does
 * something or shows its own icon. A logo that opens nothing is left out (item editors, Settings sheets).
 */
fun hasHeaderAction(showHeaderAction: Boolean, hasClick: Boolean, hasCustomIcon: Boolean): Boolean =
    showHeaderAction && (hasClick || hasCustomIcon)
