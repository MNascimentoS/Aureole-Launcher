package dev.mnascimentos.aureole.feature.settings.sections

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.settings.model.PickerDimensions
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun HeaderOffsetWheelPicker(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = remember(range) { range.toList() }
    val initialIndex = remember(value, items) { (value - range.first).coerceIn(0, items.size - 1) }

    val itemHeightDp = PickerDimensions.ItemHeight
    val itemHeightPx = with(LocalDensity.current) { itemHeightDp.toPx() }

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    val currentCenteredIndex by remember {
        derivedStateOf {
            if (itemHeightPx <= 0f) {
                0
            } else {
                (listState.firstVisibleItemIndex + (listState.firstVisibleItemScrollOffset / itemHeightPx).roundToInt())
                    .coerceIn(0, items.size - 1)
            }
        }
    }

    val currentCenteredValue = remember(currentCenteredIndex, items) {
        items.getOrElse(currentCenteredIndex) { value }
    }

    LaunchedEffect(currentCenteredValue) {
        if (currentCenteredValue != value) {
            onValueChange(currentCenteredValue)
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = snapFlingBehavior,
            contentPadding = PaddingValues(vertical = itemHeightDp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(
                items = items,
                key = { _, itemValue -> itemValue },
                contentType = { _, _ -> "offset_wheel_item" }
            ) { _, itemValue ->
                OffsetWheelItem(
                    text = "$itemValue%",
                    isSelected = itemValue == currentCenteredValue,
                    height = itemHeightDp
                )
            }
        }
    }
}

@Composable
private fun OffsetWheelItem(text: String, isSelected: Boolean, height: Dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        contentAlignment = Alignment.Center
    ) {
        AureoleText(
            text = text,
            color = if (isSelected) {
                AureoleDS.colors.onSurfaceHigh
            } else {
                AureoleDS.colors.onSurfaceLow.copy(alpha = 0.35f)
            },
            format = AureoleTextFormatParams(
                style = if (isSelected) AureoleDS.typography.bodyLarge else AureoleDS.typography.bodySmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        )
    }
}
