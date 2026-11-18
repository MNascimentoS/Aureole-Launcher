package dev.mnascimentos.aureole.composable.icons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.LayoutDirection
import dev.mnascimentos.aureole.composable.icons.model.ICON_GRID_COLUMNS
import dev.mnascimentos.aureole.composable.icons.model.IconGridItem
import dev.mnascimentos.aureole.composable.icons.model.chipScrollDelta
import dev.mnascimentos.aureole.composable.icons.model.iconGridCategories
import dev.mnascimentos.aureole.composable.icons.model.iconGridCategoryAt
import dev.mnascimentos.aureole.composable.icons.model.iconGridHeaderIndex
import dev.mnascimentos.aureole.composable.icons.model.iconGridItems
import dev.mnascimentos.aureole.composable.icons.model.initialIconGridIndex
import dev.mnascimentos.aureole.composable.icons.model.isIconSelected
import dev.mnascimentos.aureole.composable.mirror.LeftHandMirror
import dev.mnascimentos.aureole.composable.sheets.edgeToEdgeRow
import dev.mnascimentos.aureole.composable.sheets.edgeToEdgeRowPadding
import dev.mnascimentos.aureole.composable.sheets.sheetContentInset
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.AureoleIconCategory
import dev.mnascimentos.aureole.core.designsystem.icons.AureoleIconEntry
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import kotlinx.coroutines.launch

/**
 * Every icon of the [dev.mnascimentos.aureole.core.designsystem.icons.AureoleIconCatalog] in one grid
 * ([ICON_GRID_COLUMNS] columns), grouped by category. Chips with the categories stay fixed above the grid: tapping
 * one scrolls to its category, and scrolling the grid marks the chip of the category on screen and brings that chip
 * fully into view. The name of the category on screen stays pinned at the top of the grid while it scrolls (a
 * `LazyVerticalGrid` has no sticky headers, so it is an overlay). The icon [selectedKey] (old keys included) is
 * drawn filled and the grid opens on its category. Mirrored in the left-handed mode.
 *
 * The grid scrolls by itself, so the parent must bound its height (for example `heightIn(max = ...)`) and must not
 * scroll vertically.
 */
@Composable
fun AureoleIconCatalogGrid(
    selectedKey: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    isLeftHanded: Boolean = LocalLeftHanded.current
) {
    val items = remember { iconGridItems() }
    val categories = remember(items) { iconGridCategories(items) }
    val gridState = rememberLazyGridState(initialFirstVisibleItemIndex = initialIconGridIndex(items, selectedKey))
    val active by remember(items) {
        derivedStateOf { iconGridCategoryAt(items, gridState.firstVisibleItemIndex) ?: categories.first() }
    }
    val scope = rememberCoroutineScope()
    // The category names keep the reading direction, at the start like every section label (RF-13).
    val readingDirection = LocalLayoutDirection.current
    LeftHandMirror(isLeftHanded = isLeftHanded) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
        ) {
            IconCategoryChips(
                categories = categories,
                active = active,
                onCategorySelected = { category ->
                    scope.launch { gridState.animateScrollToItem(iconGridHeaderIndex(items, category)) }
                }
            )
            Box(modifier = Modifier.fillMaxWidth().weight(1f, fill = false)) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(ICON_GRID_COLUMNS),
                    state = gridState,
                    horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall),
                    verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall),
                    contentPadding = PaddingValues(bottom = AureoleDS.dimens.xSmall)
                ) {
                    items(
                        items = items,
                        key = { it.gridKey() },
                        span = { GridItemSpan(if (it is IconGridItem.Header) maxLineSpan else 1) },
                        contentType = { it::class }
                    ) { item ->
                        when (item) {
                            is IconGridItem.Header -> CategoryHeader(item.category, readingDirection)
                            is IconGridItem.Icon -> IconTile(
                                entry = item.entry,
                                isSelected = isIconSelected(item.entry, selectedKey),
                                onClick = { onSelect(item.entry.key) }
                            )
                        }
                    }
                }
                CategoryHeader(
                    category = active,
                    readingDirection = readingDirection,
                    modifier = Modifier.align(Alignment.TopStart)
                )
            }
        }
    }
}

private fun IconGridItem.gridKey(): String = when (this) {
    is IconGridItem.Header -> "header_${category.name}"
    is IconGridItem.Icon -> entry.key
}

/** Category name row, opaque so the icons scroll under the pinned copy. */
@Composable
private fun CategoryHeader(
    category: AureoleIconCategory,
    readingDirection: LayoutDirection,
    modifier: Modifier = Modifier
) = CompositionLocalProvider(LocalLayoutDirection provides readingDirection) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(AureoleDS.dimens.xLarge)
            .background(AureoleDS.colors.surface),
        contentAlignment = Alignment.CenterStart
    ) {
        AureoleText(
            text = stringResource(category.label),
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(style = AureoleDS.typography.titleSmall, maxLines = 1)
        )
    }
}

@Composable
private fun IconCategoryChips(
    categories: List<AureoleIconCategory>,
    active: AureoleIconCategory,
    onCategorySelected: (AureoleIconCategory) -> Unit
) {
    val activeIndex = categories.indexOf(active).coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = activeIndex)
    // Keeps the marked chip entirely on screen, never cut at an edge.
    LaunchedEffect(activeIndex) {
        val info = listState.layoutInfo
        val chip = info.visibleItemsInfo.firstOrNull { it.index == activeIndex }
        if (chip == null) {
            listState.animateScrollToItem(activeIndex)
        } else {
            val delta = chipScrollDelta(
                chipStart = chip.offset,
                chipEnd = chip.offset + chip.size,
                viewportStart = info.viewportStartOffset + info.beforeContentPadding,
                viewportEnd = info.viewportEndOffset - info.afterContentPadding
            )
            if (delta != 0) listState.animateScrollBy(delta.toFloat())
        }
    }
    val inset = sheetContentInset()
    LazyRow(
        modifier = Modifier.fillMaxWidth().edgeToEdgeRow(inset),
        state = listState,
        contentPadding = edgeToEdgeRowPadding(inset),
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        items(categories, key = { it.name }) { category ->
            CategoryChip(
                label = stringResource(category.label),
                isSelected = category == active,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}

@Composable
private fun CategoryChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(AureoleDS.dimens.minTouchTarget)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .background(if (isSelected) AureoleDS.colors.onSurfaceHigh else AureoleDS.colors.surfaceVariant)
            .clickable(role = Role.RadioButton, onClick = onClick)
            .semantics { this.selected = isSelected }
            .padding(horizontal = AureoleDS.dimens.medium),
        contentAlignment = Alignment.Center
    ) {
        AureoleText(
            text = label,
            color = if (isSelected) AureoleDS.colors.surface else AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(style = AureoleDS.typography.labelLarge, maxLines = 1)
        )
    }
}

/** One icon: filled (light background, dark icon) when [isSelected], else a quiet tile. 48 dp to touch. */
@Composable
private fun IconTile(entry: AureoleIconEntry, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(AureoleDS.dimens.minTouchTarget)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .background(if (isSelected) AureoleDS.colors.onSurfaceHigh else AureoleDS.colors.surfaceVariant)
            .clickable(role = Role.RadioButton, onClick = onClick)
            .semantics { this.selected = isSelected },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(entry.res),
            contentDescription = null,
            modifier = Modifier.size(AureoleDS.dimens.iconMedium),
            tint = if (isSelected) AureoleDS.colors.surface else AureoleDS.colors.onSurfaceHigh
        )
    }
}

private const val PREVIEW_ROWS = 3

@AureolePreview
@Composable
private fun AureoleIconCatalogGridPreview() {
    AureoleLauncherTheme {
        AureoleIconCatalogGrid(
            selectedKey = "work",
            onSelect = {},
            modifier = Modifier.height(AureoleDS.dimens.sizeExtraLarge * PREVIEW_ROWS).padding(AureoleDS.dimens.medium)
        )
    }
}
