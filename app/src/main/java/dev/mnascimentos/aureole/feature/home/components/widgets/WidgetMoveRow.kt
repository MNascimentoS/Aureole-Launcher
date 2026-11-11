package dev.mnascimentos.aureole.feature.home.components.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.mirror.LeftHandMirror
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.ArrowDown
import dev.mnascimentos.aureole.core.designsystem.model.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetMoveParams
import dev.mnascimentos.aureole.feature.home.components.widgets.model.moveLabelText

private const val POINT_LEFT = 90f
private const val POINT_RIGHT = -90f
private const val DISABLED_ALPHA = 0.38f

/**
 * "Move <widget> · n of m" with a back and a forward button of 48 dp: the way to reorder the stack without
 * dragging. The label sits at the far side and the buttons at the thumb side; the buttons keep their physical
 * order (back on the left) in both hands, like the cards above them. A null step disables its button.
 */
@Composable
fun WidgetMoveRow(
    params: WidgetMoveParams,
    modifier: Modifier = Modifier
) {
    LeftHandMirror {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = AureoleDS.dimens.menuRowHeight)
                .padding(horizontal = AureoleDS.dimens.xxSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
        ) {
            MoveLabel(name = params.name, modifier = Modifier.weight(1f))
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                MoveControls(params = params)
            }
        }
    }
}

/**
 * One text ("Move <widget>") so the reading order survives the mirror: the layout direction is forced to Ltr for
 * the text and the alignment is absolute, flush with the far edge (right in the left-handed mode).
 */
@Composable
private fun MoveLabel(name: String, modifier: Modifier = Modifier) {
    val label = stringResource(R.string.home_widget_move_label)
    val muted = AureoleDS.colors.onSurfaceMedium
    val strong = AureoleDS.colors.onSurfaceHigh
    val text = remember(label, name, muted, strong) { moveLabelText(label, name, muted, strong) }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        AureoleText(
            text = text,
            modifier = modifier,
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.bodyLarge,
                textAlign = if (LocalLeftHanded.current) TextAlign.Right else TextAlign.Left,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        )
    }
}

@Composable
private fun MoveControls(params: WidgetMoveParams) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        MoveButton(
            rotation = POINT_LEFT,
            description = stringResource(R.string.home_widget_move_previous, params.name),
            onClick = params.onPrevious
        )
        AureoleText(
            text = stringResource(R.string.home_widget_move_position, params.position, params.count),
            color = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.padding(horizontal = AureoleDS.dimens.xSmall),
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium, fontWeight = FontWeight.Bold)
        )
        MoveButton(
            rotation = POINT_RIGHT,
            description = stringResource(R.string.home_widget_move_next, params.name),
            onClick = params.onNext
        )
    }
}

@Composable
private fun MoveButton(rotation: Float, description: String, onClick: (() -> Unit)?) {
    val tint: Color = AureoleDS.colors.onSurfaceHigh.copy(alpha = if (onClick == null) DISABLED_ALPHA else 1f)
    IconButton(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        modifier = Modifier
            .size(AureoleDS.dimens.minTouchTarget)
            .semantics { contentDescription = description }
    ) {
        Box(
            modifier = Modifier
                .size(AureoleDS.dimens.iconXLarge)
                .background(AureoleDS.colors.surfaceVariant, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            AureoleDS.icons.ArrowDown(
                tint = tint,
                modifier = Modifier
                    .size(AureoleDS.dimens.iconMedium)
                    .rotate(rotation)
            )
        }
    }
}

@AureolePreview
@Composable
private fun WidgetMoveRowPreview() {
    AureoleLauncherTheme {
        WidgetMoveRow(
            params = WidgetMoveParams(
                name = "Clock",
                position = 1,
                count = 3,
                onPrevious = null,
                onNext = {}
            )
        )
    }
}
