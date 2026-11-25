package dev.mnascimentos.aureole.feature.home.components.onboarding.model

import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OnboardingNewItemTest {

    @Test
    fun `each item a step adds should have its chip`() {
        assertEquals(R.string.home_onboarding_new_clock, newItemChipRes(LauncherItemType.CLOCK))
        assertEquals(R.string.home_onboarding_new_widget, newItemChipRes(LauncherItemType.WIDGET_LIST))
        assertEquals(R.string.home_onboarding_new_widget, newItemChipRes(LauncherItemType.SINGLE_APP_WIDGET))
        assertEquals(R.string.home_onboarding_new_favorites, newItemChipRes(LauncherItemType.APPS_LIST))
        assertEquals(R.string.home_onboarding_new_group, newItemChipRes(LauncherItemType.SHORTCUTS_CONTAINER))
    }

    @Test
    fun `an item no step adds should have no chip`() {
        assertNull(newItemChipRes(LauncherItemType.SCROLL_VIEW))
        assertNull(newItemChipRes(null))
    }

    @Test
    fun `while it draws itself, the outline should grow and not march`() {
        assertEquals(NewItemDashes(visibleFraction = 0.4f, phasePx = 0f), newItemDashes(0.4f, 0.5f, dashPx = 10f))
    }

    @Test
    fun `once drawn, the dashes should march one cycle per phase`() {
        assertEquals(NewItemDashes(visibleFraction = 1f, phasePx = -10f), newItemDashes(1f, 0.5f, dashPx = 10f))
        assertEquals(0f, newItemDashes(1f, 0f, dashPx = 10f).phasePx, 0f)
    }

    @Test
    fun `the Visual tabs are Palette then Font`() {
        assertEquals(
            listOf(R.string.home_onboarding_visual_tab_palette, R.string.home_onboarding_visual_tab_font),
            AppearancePicker.entries.map { it.tabLabelRes }
        )
    }
}
