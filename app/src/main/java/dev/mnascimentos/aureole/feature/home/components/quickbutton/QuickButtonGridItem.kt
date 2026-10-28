package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.data.model.ShortcutType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@Composable
fun QuickButtonGridItem(
    model: QuickButtonModel,
    onAction: (ShortcutAction) -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier,
    appInfo: AppInfo? = null
) {
    QuickButtonGridItem(
        model = model,
        shape = RoundedCornerShape(AureoleDS.dimens.radiusMedium),
        onAction = onAction,
        onLongPress = onLongPress,
        modifier = modifier,
        appInfo = appInfo
    )
}

@Composable
fun QuickButtonGridItem(
    model: QuickButtonModel,
    shape: Shape,
    onAction: (ShortcutAction) -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier,
    appInfo: AppInfo? = null
) {
    val currentModel by rememberUpdatedState(model)
    val currentOnAction by rememberUpdatedState(onAction)
    val currentOnLongPress by rememberUpdatedState(onLongPress)

    val isDefaultBg = model.appearance.backgroundColor == 0 ||
        model.appearance.backgroundColor == 0xFF455A64.toInt()
    val backgroundColor = if (isDefaultBg) {
        AureoleDS.colors.surfaceVariant
    } else {
        Color(model.appearance.backgroundColor)
    }

    val baseModifier = modifier
        .fillMaxSize()
        .clip(shape)
        .background(backgroundColor)

    val strokeModifier = if (model.appearance.strokeEnabled) {
        baseModifier.border(AureoleDS.dimens.minimal, Color(model.appearance.strokeColor), shape)
    } else {
        baseModifier
    }

    Box(
        modifier = strokeModifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { currentOnAction(currentModel.shortcuts.click) },
                    onLongPress = { currentOnLongPress() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        val clickShortcut = model.shortcuts.click
        if (clickShortcut.type == ShortcutType.OPEN_APP && appInfo != null) {
            Image(
                bitmap = appInfo.getIconBitmap(),
                contentDescription = null,
                modifier = Modifier.size(AureoleDS.dimens.iconXLarge)
            )
        } else {
            Icon(
                painter = painterResource(id = model.appearance.icon.iconRes),
                contentDescription = null,
                tint = Color(model.appearance.iconColor),
                modifier = Modifier.size(AureoleDS.dimens.iconXLarge)
            )
        }
    }
}

@AureolePreview
@Composable
private fun QuickButtonGridItemPreview() {
    AureoleLauncherTheme {
        Box(modifier = Modifier.size(AureoleDS.dimens.radiusFull)) {
            QuickButtonGridItem(
                model = QuickButtonModel(),
                onAction = {},
                onLongPress = {}
            )
        }
    }
}
