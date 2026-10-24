package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.grid.model.ScrollViewChildParams
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ScrollViewChildEditTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val calls = mutableListOf<String>()
    private var dismissals = 0
    private var child by mutableStateOf(LauncherItemState(id = "child", type = LauncherItemType.CLOCK))

    private fun showCard() {
        val actions = createDefaultHomeScreenActions().copy(
            onOpenEditClockBottomSheet = { calls += "clock" },
            onOpenEditContainerDialog = { id -> calls += "container:$id" },
            onOpenWidgetStackBottomSheet = { _, stackId -> calls += "widgets:$stackId" },
            onOpenFavoritePicker = { id -> calls += "favorites:$id" },
            onOpenQuickButtonSettings = { id -> calls += "quick:$id" },
            onRemoveChildFromScrollView = { parentId, childId -> calls += "remove:$parentId/$childId" }
        )
        composeTestRule.setContent {
            AureoleLauncherTheme {
                ScrollViewChildItemCard(
                    params = ScrollViewChildParams(
                        parentId = "parent",
                        child = child,
                        childIndex = 0,
                        totalChildren = 1,
                        isVertical = true
                    ),
                    actions = actions,
                    onDismissRequest = { dismissals++ }
                )
            }
        }
    }

    /** The header's first button edits the child, the second removes it. */
    private fun clickHeaderButton(index: Int) {
        composeTestRule.onAllNodes(hasClickAction())[index].performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun `when editing each kind of child, should open its own editor and close the dialog`() {
        val expected = linkedMapOf(
            LauncherItemType.CLOCK to ("Relógio" to "clock"),
            LauncherItemType.SHORTCUTS_CONTAINER to ("Container" to "container:child"),
            LauncherItemType.SCROLL_VIEW to ("Container Scroll View" to "container:child"),
            LauncherItemType.WIDGET_LIST to ("Grupo de Widgets" to "widgets:child"),
            LauncherItemType.SINGLE_APP_WIDGET to ("Componente" to "widgets:child"),
            LauncherItemType.APPS_LIST to ("Lista de Aplicativos" to "favorites:child"),
            LauncherItemType.QUICK_BUTTON to ("Botão Rápido" to "quick:child"),
        )
        showCard()

        expected.forEach { (type, titleAndCall) ->
            child = child.copy(type = type)
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithText(titleAndCall.first).assertExists()

            clickHeaderButton(0)

            assertEquals("editing $type", titleAndCall.second, calls.last())
        }
        assertEquals(expected.size, dismissals)
    }

    @Test
    fun `when removing a child, should ask the parent to drop it`() {
        showCard()

        clickHeaderButton(1)

        assertEquals("remove:parent/child", calls.single())
    }
}
