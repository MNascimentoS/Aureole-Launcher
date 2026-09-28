@file:Suppress("MagicNumber", "MaxLineLength")

package dev.mnascimentos.aureole.feature.settings.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mnascimentos.aureole.composable.SettingsToggleItem
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.AlignRight
import dev.mnascimentos.aureole.core.designsystem.icons.Dots
import dev.mnascimentos.aureole.core.designsystem.icons.Header
import dev.mnascimentos.aureole.core.designsystem.icons.Press
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import kotlin.math.roundToInt

private const val BOTTOM_SPACER_RATIO = 0.15f
private const val MIN_OFFSET_PERCENT = 10
private const val MAX_OFFSET_PERCENT = 60

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBehaviorScreen(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    @Suppress("UNUSED_PARAMETER") onNavigateBack: () -> Unit
) {
    Scaffold(
        containerColor = AureoleDS.colors.surface
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val topSpacerHeight = (maxHeight * (uiState.headerOffsetPercent / 100f)).coerceAtLeast(16.dp)
            val bottomSpacerHeight = (maxHeight * BOTTOM_SPACER_RATIO).coerceAtLeast(24.dp)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(topSpacerHeight))

                AureoleText(
                    text = "Behavior",
                    style = AureoleTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    color = AureoleTheme.colors.onSurfaceMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                BehaviorMenuItems(uiState = uiState, actions = actions)

                Spacer(modifier = Modifier.height(bottomSpacerHeight))
            }
        }
    }
}

@Composable
private fun BehaviorMenuItems(
    uiState: SettingsUiState,
    actions: SettingsScreenActions
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        SettingsToggleItem(
            title = "Home opens all apps",
            checked = uiState.homeButtonOpensAllApps,
            onCheckedChange = { actions.onToggleHomeButtonOpensAllApps() },
            leadingContent = { AureoleDS.icons.Press() }
        )

        SettingsToggleItem(
            title = "All apps on left side",
            checked = uiState.isLeftHandedMode,
            onCheckedChange = { actions.onToggleLeftHandedMode() },
            leadingContent = { AureoleDS.icons.AlignRight() }
        )

        SettingsToggleItem(
            title = "Show widget indicator",
            checked = uiState.showWidgetDots,
            onCheckedChange = { actions.onToggleShowWidgetDots() },
            leadingContent = { AureoleDS.icons.Dots() }
        )

        HeaderOffsetPickerRow(
            headerOffsetPercent = uiState.headerOffsetPercent,
            onOffsetChanged = actions.onHeaderOffsetChanged
        )
    }
}

@Composable
private fun HeaderOffsetPickerRow(
    headerOffsetPercent: Int,
    onOffsetChanged: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleDS.icons.Header(
            tint = AureoleTheme.colors.onSurfaceMedium,
            modifier = Modifier.padding(end = 20.dp).size(20.dp)
        )

        AureoleText(
            text = "Header offset",
            style = AureoleTheme.typography.bodyLarge,
            fontWeight = FontWeight.Normal,
            color = AureoleTheme.colors.onSurfaceMedium,
            modifier = Modifier.weight(1f)
        )

        HeaderOffsetWheelPicker(
            value = headerOffsetPercent,
            range = MIN_OFFSET_PERCENT..MAX_OFFSET_PERCENT,
            onValueChange = onOffsetChanged,
            modifier = Modifier.height(72.dp).width(60.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HeaderOffsetWheelPicker(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = remember(range) { range.toList() }
    val initialIndex = remember(value, items) { (value - range.first).coerceIn(0, items.size - 1) }

    val itemHeightDp = 24.dp
    val itemHeightPx = with(LocalDensity.current) { itemHeightDp.toPx() }

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val centerIndex = (listState.firstVisibleItemIndex + (listState.firstVisibleItemScrollOffset / itemHeightPx).roundToInt())
                .coerceIn(0, items.size - 1)
            val selectedValue = items[centerIndex]
            if (selectedValue != value) {
                onValueChange(selectedValue)
            }
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
            itemsIndexed(items) { _, itemValue ->
                val isSelected = itemValue == value
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeightDp),
                    contentAlignment = Alignment.Center
                ) {
                    AureoleText(
                        text = "$itemValue%",
                        style = AureoleTheme.typography.bodyLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) {
                            AureoleTheme.colors.onSurfaceHigh
                        } else {
                            AureoleTheme.colors.onSurfaceLow.copy(alpha = 0.35f)
                        },
                        fontSize = if (isSelected) 15.sp else 12.sp
                    )
                }
            }
        }
    }
}
