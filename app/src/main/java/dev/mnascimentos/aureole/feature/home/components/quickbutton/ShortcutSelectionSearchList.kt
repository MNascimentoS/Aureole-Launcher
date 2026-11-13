package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.search.bestResultRing
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.ShortcutSearchCandidate

/**
 * Search results of the shortcut sheet. The list is reversed and anchored at the bottom: the first
 * result (the best one) sits last, next to the search field, with a hint below it.
 */
@Composable
internal fun ShortcutSearchResultsList(
    query: String,
    results: List<ShortcutSearchCandidate>,
    currentAction: ShortcutAction,
    onApplyShortcut: (ShortcutAction) -> Unit,
    modifier: Modifier = Modifier
) {
    if (results.isEmpty()) {
        ShortcutSearchEmpty(query = query, modifier = modifier)
        return
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        reverseLayout = true,
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xxSmall, Alignment.Bottom)
    ) {
        item(key = BEST_HINT_KEY) {
            AureoleText(
                text = stringResource(R.string.core_common_search_best_hint),
                color = AureoleDS.colors.onSurfaceLow,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = AureoleDS.dimens.xxSmall),
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.labelSmall,
                    textAlign = TextAlign.Center
                )
            )
        }
        itemsIndexed(results) { index, item ->
            val isSelected = currentAction.type == item.action.type && currentAction.target == item.action.target
            ShortcutActionRow(
                candidate = item,
                isSelected = currentAction == item.action,
                onClick = { onApplyShortcut(item.action) },
                modifier = Modifier.bestResultRing(isBest = index == 0)
            )
        }
    }
}

@Composable
private fun ShortcutSearchEmpty(query: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = AureoleDS.dimens.medium),
        contentAlignment = Alignment.BottomCenter
    ) {
        AureoleText(
            text = stringResource(R.string.core_common_search_empty, query),
            color = AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium, textAlign = TextAlign.Center)
        )
    }
}

private const val BEST_HINT_KEY = "best_hint"
