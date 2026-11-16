package dev.mnascimentos.aureole.feature.home.model

/**
 * Which shortcut opened the apps list, so it closes the way it came in.
 *
 * - [BOTTOM]: swipe up, the Home button, a quick button, an "All apps" shortcut or the onboarding coach.
 * - [EDGE]: pulling the alphabet edge (the scrubber).
 */
enum class AllAppsOpenSource { BOTTOM, EDGE }

/** Where the apps list slides in from, and back out to. */
enum class DrawerMotion { FROM_BOTTOM, FROM_RIGHT, FROM_LEFT }

/** The bottom for every shortcut but the edge, which brings the list in from the side it was pulled from. */
fun drawerMotionFor(source: AllAppsOpenSource, isLeftHanded: Boolean): DrawerMotion = when {
    source == AllAppsOpenSource.BOTTOM -> DrawerMotion.FROM_BOTTOM
    isLeftHanded -> DrawerMotion.FROM_LEFT
    else -> DrawerMotion.FROM_RIGHT
}

/**
 * Opens or closes the apps list. Opening records [source]; it stays while the list is open (a second open request
 * does not change it) and while it closes, so the exit goes back the same way.
 */
fun MainUiState.withAllAppsDrawer(open: Boolean, source: AllAppsOpenSource = AllAppsOpenSource.BOTTOM): MainUiState =
    copy(
        isAllAppsDrawerOpen = open,
        allAppsOpenSource = if (open && !isAllAppsDrawerOpen) source else allAppsOpenSource
    )
