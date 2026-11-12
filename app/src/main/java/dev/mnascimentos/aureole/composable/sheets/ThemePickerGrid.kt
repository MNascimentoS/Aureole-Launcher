package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS

/**
 * Sheet body of the palette and font pickers: a small section label above a grid with [columns] columns,
 * and the optional [footer] below it. The grid scrolls when it is taller than the sheet.
 */
@Composable
internal fun ThemePickerGrid(
    label: String,
    columns: Int,
    footer: (@Composable () -> Unit)?,
    modifier: Modifier = Modifier,
    items: LazyGridScope.() -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        AureoleText(
            text = label,
            color = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.padding(bottom = AureoleDS.dimens.xSmall),
            format = AureoleTextFormatParams(style = AureoleDS.typography.smallTitle, fontWeight = FontWeight.Medium)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall),
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall),
            modifier = Modifier.weight(1f, fill = false),
            content = items
        )
        footer?.invoke()
    }
}
