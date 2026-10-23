package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.grid.model.CornerResizeCallbacks
import dev.mnascimentos.aureole.feature.home.grid.model.GridCellParams
import dev.mnascimentos.aureole.feature.home.grid.model.ResizeHandleCallbacks
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DynamicGridResizeHandlesTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testItem = LauncherItemState(
        id = "test_item",
        type = LauncherItemType.CLOCK,
        col = 0,
        row = 0,
        colSpan = 2,
        rowSpan = 2
    )

    private val testParams = GridCellParams(
        item = testItem,
        items = listOf(testItem),
        isEditMode = true,
        cellWidthPx = 100f,
        cellHeightPx = 100f,
        cellWidthDp = 100.dp,
        cellHeightDp = 100.dp
    )

    @Test
    fun `when rendering DeleteActionHandle and clicked, should trigger onDeleteItem`() {
        var deleted = false
        composeTestRule.setContent {
            AureoleLauncherTheme {
                Box {
                    DeleteActionHandle(
                        itemId = "test_item",
                        onDeleteItem = { deleted = true },
                        modifier = Modifier.testTag("delete_handle")
                    )
                }
            }
        }

        // Needs pointerInput trigger or click on the layout node itself, which pointerInput handles
        composeTestRule.onNodeWithTag("delete_handle").performClick()
        assertTrue(deleted)
    }

    @Test
    fun `when rendering CornerResizeHandle and tapped, should trigger onEditItem`() {
        var editTriggered = false
        composeTestRule.setContent {
            AureoleLauncherTheme {
                Box {
                    CornerResizeHandle(
                        params = testParams,
                        callbacks = CornerResizeCallbacks(
                            onResizing = {},
                            onDelta = { _, _ -> },
                            onResetExtra = {},
                            onResizeItem = { _, _, _ -> },
                            onEditItem = { editTriggered = true }
                        ),
                        modifier = Modifier.testTag("corner_handle")
                    )
                }
            }
        }

        composeTestRule.onNodeWithTag("corner_handle").performClick()
        assertTrue(editTriggered)
    }

    @Test
    fun `when rendering RightEdgeResizeHandle, should compile and render without crashing`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                Box {
                    RightEdgeResizeHandle(
                        params = testParams,
                        callbacks = ResizeHandleCallbacks(
                            onResizing = {},
                            onDelta = {},
                            onResetExtra = {},
                            onResizeItem = { _, _, _ -> }
                        )
                    )
                }
            }
        }
    }

    @Test
    fun `when rendering BottomEdgeResizeHandle, should compile and render without crashing`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                Box {
                    BottomEdgeResizeHandle(
                        params = testParams,
                        callbacks = ResizeHandleCallbacks(
                            onResizing = {},
                            onDelta = {},
                            onResetExtra = {},
                            onResizeItem = { _, _, _ -> }
                        )
                    )
                }
            }
        }
    }
}
