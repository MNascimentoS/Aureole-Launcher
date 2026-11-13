package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Cancelling the favorites task's grid edit keeps the apps list it created, same id, all apps on (RF-13). */
class OnboardingFavoritesCancelTest {

    private val clock = LauncherItemState(id = "clock", type = LauncherItemType.CLOCK, col = 0, row = 0, colSpan = 4)
    private val created = LauncherItemState(
        id = "created",
        type = LauncherItemType.APPS_LIST,
        col = 0,
        row = 4,
        colSpan = 4,
        rowSpan = 4,
        showAllApps = false
    )

    private fun state(task: OnboardingTask?, createdId: String?, draft: List<LauncherItemState>) = MainUiState(
        isGridEditMode = true,
        gridItems = draft,
        onboarding = OnboardingUiState(pendingTask = task, createdItemId = createdId)
    )

    @Test
    fun `the created list goes back into the restored layout with all apps and the same id`() {
        val kept = state(OnboardingTask.ADD_FAVORITE_APPS, "created", listOf(clock, created))
            .keepingCreatedAppsList(restored = listOf(clock))

        assertEquals(listOf(clock, created.copy(showAllApps = true)), kept)
    }

    @Test
    fun `nothing is kept for another task, without a created list, or when the list is already there`() {
        val draft = listOf(clock, created)

        assertNull(state(OnboardingTask.ADD_CLOCK, "created", draft).keepingCreatedAppsList(listOf(clock)))
        assertNull(state(OnboardingTask.ADD_FAVORITE_APPS, null, draft).keepingCreatedAppsList(listOf(clock)))
        assertNull(state(OnboardingTask.ADD_FAVORITE_APPS, "created", draft).keepingCreatedAppsList(draft))
        assertNull(state(OnboardingTask.ADD_FAVORITE_APPS, "gone", draft).keepingCreatedAppsList(listOf(clock)))
    }

    @Test
    fun `nothing is kept when the restored layout has an item on the list's spot`() {
        val blocker = clock.copy(id = "blocker", row = 4)

        val kept = state(OnboardingTask.ADD_FAVORITE_APPS, "created", listOf(clock, created))
            .keepingCreatedAppsList(restored = listOf(clock, blocker))

        assertNull(kept)
    }
}
