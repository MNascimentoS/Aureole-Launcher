package dev.mnascimentos.aureole.feature.home.components.favorites.model

import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FavoritesScrollRuleTest {

    @Test
    fun `when favorites grow during the checklist, should scroll to the top`() {
        assertTrue(shouldScrollToFavorites(0, 1, OnboardingStage.CHECKLIST))
        assertTrue(shouldScrollToFavorites(2, 5, OnboardingStage.CHECKLIST))
    }

    @Test
    fun `when favorites do not grow, should not scroll`() {
        assertFalse(shouldScrollToFavorites(2, 2, OnboardingStage.CHECKLIST))
        assertFalse(shouldScrollToFavorites(3, 1, OnboardingStage.CHECKLIST))
    }

    @Test
    fun `when the checklist is not the stage, should not scroll`() {
        OnboardingStage.entries.filter { it != OnboardingStage.CHECKLIST }.forEach { stage ->
            assertFalse(shouldScrollToFavorites(0, 3, stage))
        }
    }
}
