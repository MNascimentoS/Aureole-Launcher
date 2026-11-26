package dev.mnascimentos.aureole.composable.colorpage

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.colorpage.model.ColorPageMetrics
import dev.mnascimentos.aureole.composable.colorpage.model.ColorPageTile
import dev.mnascimentos.aureole.composable.colorpage.model.ColorTileLook
import dev.mnascimentos.aureole.composable.menu.AureoleMenuRow
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorEntry
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.palette.readableOn
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import kotlin.math.sqrt

/**
 * A color row of a component's sheet (the clock's Colors tab, the group's Appearance tab): a dot of the current
 * [color] (null: the "None" dot), the [title], the color's name ([value]) and a chevron. [onClick] opens the color
 * page.
 */
@Composable
fun AureoleColorRow(
    title: String,
    value: String,
    color: Color?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AureoleMenuRow(
        MenuRowParams(
            title = title,
            subtitle = value,
            icon = { m, _ -> if (color == null) NoneColorDot(modifier = m) else ColorDot(color = color, modifier = m) },
            showChevron = true,
            onClick = onClick
        ),
        modifier = modifier
    )
}

/** Name of a color of a color page: "None", "Theme", the palette that has it, or "Current". */
@Composable
@ReadOnlyComposable
fun colorPageTileName(tile: ColorPageTile): String = when (tile) {
    ColorPageTile.None -> stringResource(R.string.home_clock_color_none)
    is ColorPageTile.Entry -> when (val entry = tile.entry) {
        ComponentColorEntry.Theme -> stringResource(R.string.theme_color_swatch_default)
        is ComponentColorEntry.Current -> stringResource(R.string.theme_color_option_current)
        is ComponentColorEntry.Palette -> entry.name
    }
}

/** A tile of a color page: [look] gives its background (null: none, the sheet's neutral surface) and its strip. */
@Composable
fun ColorPageTileChip(
    name: String,
    look: ColorTileLook,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AureoleDS.colors
    val background = look.background ?: colors.surface
    val shape = RoundedCornerShape(AureoleDS.dimens.radiusSmall)
    Column(
        modifier = modifier
            .defaultMinSize(minHeight = AureoleDS.dimens.sizeLarge)
            .clip(shape)
            .background(background)
            .border(
                if (isSelected) AureoleDS.dimens.borderMax else AureoleDS.dimens.borderMinimal,
                if (isSelected) colors.onSurfaceHigh else colors.outline,
                shape
            )
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = onSelect)
            .padding(AureoleDS.dimens.xSmall),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        val strip = look.strip
        if (strip == null) {
            NoneColorDot(modifier = Modifier.size(AureoleDS.dimens.iconMediumSmall))
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AureoleDS.dimens.iconMediumSmall)
                    .clip(RoundedCornerShape(AureoleDS.dimens.radiusMinimal))
                    .background(strip)
            )
        }
        AureoleText(
            text = name,
            color = readableOn(background, colors),
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.smallTitle,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        )
    }
}

/** A dot of [color] with a thin outline. */
@Composable
fun ColorDot(color: Color, modifier: Modifier = Modifier) {
    Box(modifier = modifier.padding(AureoleDS.dimens.xxSmall)) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape)
                .background(color)
                .border(AureoleDS.dimens.borderMinimal, AureoleDS.colors.outline, CircleShape)
        )
    }
}

/** The "None" color: a dashed circle crossed out by a diagonal line. */
@Composable
fun NoneColorDot(modifier: Modifier = Modifier) {
    val color = AureoleDS.colors.onSurfaceLow
    val strokeWidth = AureoleDS.dimens.borderMinimal
    Box(
        modifier = modifier
            .padding(AureoleDS.dimens.xxSmall)
            .drawBehind {
                val width = strokeWidth.toPx()
                val dash = size.minDimension * ColorPageMetrics.NONE_DOT_DASH_FRACTION
                val radius = (size.minDimension - width) / 2f
                drawCircle(
                    color = color,
                    radius = radius,
                    style = Stroke(width = width, pathEffect = PathEffect.dashPathEffect(floatArrayOf(dash, dash)))
                )
                // Corner to corner of the square inside the circle, so the line ends on the circle.
                val half = radius / sqrt(2f)
                drawLine(
                    color = color,
                    start = center + Offset(half, -half),
                    end = center + Offset(-half, half),
                    strokeWidth = width
                )
            }
    )
}

@AureolePreview
@Composable
private fun ColorPageComponentsPreview() {
    AureoleLauncherTheme {
        Column(modifier = Modifier.background(AureoleDS.colors.surface).padding(AureoleDS.dimens.medium)) {
            AureoleColorRow(title = "Group background", value = "Theme", color = AureoleDS.colors.surface, onClick = {})
            AureoleColorRow(title = "Item background", value = "None", color = null, onClick = {})
            Row {
                ColorPageTileChip(
                    name = "None",
                    look = ColorTileLook(background = null, strip = null),
                    isSelected = true,
                    onSelect = {},
                    modifier = Modifier.width(AureoleDS.dimens.sizeExtraLarge)
                )
            }
        }
    }
}
