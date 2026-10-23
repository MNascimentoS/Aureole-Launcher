package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Check
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val RADIUS_NONE = 0
private const val RADIUS_COMPACT = 8
private const val RADIUS_SMALL = 12
private const val RADIUS_STANDARD = 16
private const val RADIUS_LARGE = 24

private val BORDER_RADIUS_OPTIONS = listOf(
    BorderRadiusOption(RADIUS_NONE, "None (0 dp)"),
    BorderRadiusOption(RADIUS_COMPACT, "Compact (8 dp)"),
    BorderRadiusOption(RADIUS_SMALL, "Small (12 dp)"),
    BorderRadiusOption(RADIUS_STANDARD, "Standard (16 dp)"),
    BorderRadiusOption(RADIUS_LARGE, "Large (24 dp)"),
)

@Suppress("LongMethod") // Bottom sheet composable for border radius slider and preset controls
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickBorderRadiusBottomSheet(
    selectedRadiusDp: Int,
    onRadiusSelected: (Int) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetContent = @Composable {
        BorderRadiusSheetContent(
            selectedRadiusDp = selectedRadiusDp,
            onRadiusSelected = onRadiusSelected
        )
    }

    if (LocalInspectionMode.current) {
        PickBorderRadiusInspectionLayout(
            modifier = modifier,
            content = sheetContent
        )
        return
    }

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
        modifier = modifier,
        dragHandle = null,
        containerColor = AureoleDS.colors.surface,
        shape = RoundedCornerShape(
            topStart = AureoleDS.dimens.radiusLarge,
            topEnd = AureoleDS.dimens.radiusLarge,
        ),
    ) {
        sheetContent()
    }
}

@Composable
private fun BorderRadiusSheetContent(
    selectedRadiusDp: Int,
    onRadiusSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = AureoleDS.dimens.large, bottom = AureoleDS.dimens.xLarge),
    ) {
        AureoleText(
            text = "Change Border",
            color = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.padding(horizontal = AureoleDS.dimens.xLarge),
            format = AureoleTextFormatParams(style = AureoleDS.typography.titleMedium)
        )

        Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))

        BorderRadiusOptionsList(
            selectedRadiusDp = selectedRadiusDp,
            onRadiusSelected = onRadiusSelected,
            modifier = Modifier.weight(1f, fill = false)
        )
    }
}

@Composable
private fun BorderRadiusOptionsList(
    selectedRadiusDp: Int,
    onRadiusSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        LazyColumn {
            items(
                items = BORDER_RADIUS_OPTIONS,
                key = { it.radiusDp },
                contentType = { "border_radius_option" }
            ) { option ->
                BorderRadiusOptionItem(
                    option = option,
                    isSelected = option.radiusDp == selectedRadiusDp,
                    onSelected = { onRadiusSelected(option.radiusDp) },
                )
            }
        }
    }
}

@Composable
private fun PickBorderRadiusInspectionLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = AureoleDS.dimens.radiusLarge,
                topEnd = AureoleDS.dimens.radiusLarge,
            ),
            color = AureoleDS.colors.surface,
            modifier = Modifier.fillMaxWidth(),
        ) {
            content()
        }
    }
}

private object BorderRadiusSwatchMetrics {
    val Width = 54.dp
    val Height = 34.dp
}

@Composable
private fun BorderRadiusOptionItem(
    option: BorderRadiusOption,
    isSelected: Boolean,
    onSelected: () -> Unit,
) {
    val shape = RoundedCornerShape(option.radiusDp.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AureoleDS.dimens.minTouchTarget)
            .clickable(
                role = Role.RadioButton,
                onClick = onSelected
            )
            .padding(horizontal = AureoleDS.dimens.xLarge, vertical = AureoleDS.dimens.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            val bgColor = if (isSelected) AureoleDS.colors.surfaceVariant else AureoleDS.colors.surface
            val borderColor = if (isSelected) AureoleDS.colors.onSurfaceHigh else AureoleDS.colors.outline

            Box(
                modifier = Modifier
                    .width(BorderRadiusSwatchMetrics.Width)
                    .height(BorderRadiusSwatchMetrics.Height)
                    .clip(shape)
                    .background(bgColor)
                    .border(
                        width = if (isSelected) AureoleDS.dimens.minimal else AureoleDS.dimens.borderMinimal,
                        color = borderColor,
                        shape = shape,
                    ),
            )

            val textColor = if (isSelected) AureoleDS.colors.onSurfaceHigh else AureoleDS.colors.onSurfaceMedium
            AureoleText(
                text = option.label,
                color = textColor,
                modifier = Modifier.padding(start = AureoleDS.dimens.medium),
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyLarge)
            )
        }

        if (isSelected) {
            AureoleDS.icons.Check(
                tint = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.size(AureoleDS.dimens.iconLarge),
            )
        }
    }
}

@AureolePreview
@Composable
private fun PickBorderRadiusBottomSheetPreview() {
    AureoleLauncherTheme {
        PickBorderRadiusBottomSheet(
            selectedRadiusDp = 16,
            onRadiusSelected = {},
            onDismissRequest = {}
        )
    }
}
