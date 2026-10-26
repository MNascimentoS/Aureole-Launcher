package dev.mnascimentos.aureole.composable.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.icons.Add
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val DEFAULT_HAZE_OPACITY = 0.5f
private const val ADD_BUTTON_HAZE_ALPHA_FACTOR = 0.8f
private const val HAZE_MIN_ALPHA_ADD_BUTTON = 0.2f
private const val HAZE_MAX_ALPHA_ADD_BUTTON = 0.6f
private const val ADD_BUTTON_ALPHA = 0.5f

@Composable
fun AddWidgetButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null,
    isHazeEnabled: Boolean = false,
    hazeOpacity: Float = DEFAULT_HAZE_OPACITY
) {
    val hazeModifier = if (isHazeEnabled && (hazeState != null)) {
        Modifier.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                blurRadius = 24.dp,
                tint = HazeTint(AureoleDS.colors.surfaceVariant.copy(alpha = hazeOpacity))
            )
        ) {
            blurEnabled = isHazeEnabled
        }
    } else {
        Modifier
    }

    val buttonAlpha = if (isHazeEnabled) {
        (hazeOpacity * ADD_BUTTON_HAZE_ALPHA_FACTOR).coerceIn(
            HAZE_MIN_ALPHA_ADD_BUTTON,
            HAZE_MAX_ALPHA_ADD_BUTTON
        )
    } else {
        ADD_BUTTON_ALPHA
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusLarge))
            .then(hazeModifier)
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = buttonAlpha))
            .clickable(
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AureoleDS.icons.Add(
                tint = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.padding(bottom = AureoleDS.dimens.xSmall)
            )
            Text(
                text = stringResource(R.string.home_widget_add),
                color = AureoleDS.colors.onSurfaceHigh,
                style = AureoleDS.typography.labelLarge
            )
        }
    }
}

@AureolePreview
@Composable
private fun AddWidgetButtonPreview() {
    AureoleLauncherTheme {
        AddWidgetButton(onClick = {})
    }
}
