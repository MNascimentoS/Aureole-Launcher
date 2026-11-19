package dev.mnascimentos.aureole.feature.home.components.favorites.model

import dev.mnascimentos.aureole.core.data.model.OnboardingStage

/**
 * Whether the apps list goes back to its top: during the onboarding checklist, when favorites were added. The
 * list keeps its position by item key, so without this it stays on "All apps" and the new favorites sit above it.
 */
internal fun shouldScrollToFavorites(
    previousCount: Int,
    count: Int,
    stage: OnboardingStage
): Boolean = stage == OnboardingStage.CHECKLIST && count > previousCount
