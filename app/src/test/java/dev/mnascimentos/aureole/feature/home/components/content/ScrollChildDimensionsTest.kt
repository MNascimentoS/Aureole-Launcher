package dev.mnascimentos.aureole.feature.home.components.content

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.composable.favorites.model.FavoritesListConfig
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerConfig
import dev.mnascimentos.aureole.feature.home.components.content.model.ScrollChildDimensions
import dev.mnascimentos.aureole.feature.home.components.content.model.ScrollViewContainerParams
import dev.mnascimentos.aureole.feature.home.components.widgets.model.StackedWidgetConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ScrollChildDimensionsTest {

    @Test
    fun `scroll child dimensions constants should have expected values`() {
        assertEquals(60.dp, ScrollChildDimensions.RowHeight)
        assertEquals(70.dp, ScrollChildDimensions.ColWidth)
        assertEquals(70.dp, ScrollChildDimensions.MinHeight)
        assertEquals(100.dp, ScrollChildDimensions.MinWidth)
    }

    @Test
    fun `scrollViewContainerParams should retain passed parameters`() {
        val item = LauncherItemState(id = "scroll_1")
        val favConfig = FavoritesListConfig(
            currentHeightDp = 100.dp,
            currentHeightPx = 100f,
            state = LazyListState()
        )
        val stackedWidgetConfig = StackedWidgetConfig(
            topWidgetIds = emptyList(),
            currentHeightDp = 100.dp,
            currentHeightPx = 100f
        )
        val containerConfig = ContainerConfig(panelId = "p1")

        val params = ScrollViewContainerParams(
            item = item,
            favConfig = favConfig,
            appWidgetHost = null,
            stackedWidgetConfig = stackedWidgetConfig,
            containerConfig = containerConfig
        )

        assertEquals(item, params.item)
        assertEquals(favConfig, params.favConfig)
        assertEquals(null, params.appWidgetHost)
        assertEquals(stackedWidgetConfig, params.stackedWidgetConfig)
        assertEquals(containerConfig, params.containerConfig)
        assertNotNull(params)
    }
}
